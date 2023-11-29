package com.notes.aionote.data.repository

import android.content.Intent
import android.content.IntentSender
import androidx.core.net.toUri
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.toObject
import com.notes.aionote.common.AioFirebaseCollection
import com.notes.aionote.common.CollectionRef
import com.notes.aionote.common.FirebaseConst
import com.notes.aionote.common.Resource
import com.notes.aionote.domain.remote_data.FireUserEntity
import com.notes.aionote.domain.repository.AuthRepository
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImp @Inject constructor (
	private val oneTapClient: SignInClient,
	private val signInRequest: BeginSignInRequest,
	@CollectionRef(AioFirebaseCollection.USER) private val fireStoreUserCollection : CollectionReference,
) : AuthRepository {
	private val firebaseAuth = FirebaseAuth.getInstance()
	
	@Suppress("UNCHECKED_CAST")
	override suspend fun <T> getCurrentUser(): Resource<T> {
		return try {
			Resource.Success(result = firebaseAuth.currentUser as T)
		} catch (e: Exception) {
			Resource.Fail(errorMessage = e.message)
		}
	}
	
	override suspend fun getUserNoteRef(userId: String): Resource<String?> {
		return try {
			val completableDeferred = CompletableDeferred<String?>()
			fireStoreUserCollection.document(userId)
				.collection(FirebaseConst.FIREBASE_INFO_COL_REF)
				.document(userId)
				.get().addOnSuccessListener { snapshot ->
					completableDeferred.complete(
						snapshot.toObject<FireUserEntity>()?.noteContentRef
					)
				}
			Resource.Success(completableDeferred.await())
		} catch (e: Exception) {
			Resource.Fail(errorMessage = "Get user note ref fail ${e.message}")
		}
	}
	
	override suspend fun login(
		email: String,
		pass: String
	): Resource<FireUserEntity> {
		return try {
			val fireUser = firebaseAuth.signInWithEmailAndPassword(email, pass).await().user!!
			val user = FireUserEntity(fireUser.uid, fireUser.displayName, email, null, fireUser.uid)
			Resource.Success(user)
		} catch (e: Exception) {
			Resource.Fail(errorMessage = e.message)
		}
	}
	
	override suspend fun logout(): Resource<Unit> {
		return try {
			oneTapClient.signOut().await()
			firebaseAuth.signOut()
			Resource.Success(Unit)
		} catch (e: Exception) {
			Resource.Fail(errorMessage = e.message)
		}
		
	}
	
	override suspend fun register(
		email: String,
		pass: String,
		fullName: String?,
	): Resource<FireUserEntity> {
		return try {
			val fireUser = firebaseAuth.createUserWithEmailAndPassword(email, pass).await().user!!
			val profileUpdate = userProfileChangeRequest {
				displayName = fullName
			}
			fireUser.updateProfile(profileUpdate).await()
			val user = FireUserEntity(fireUser.uid, fullName, email, null, fireUser.uid)
			fireStoreUserCollection.document(fireUser.uid)
				.collection("info")
				.document(fireUser.uid)
				.set(user)
				.await()
			Resource.Success(user)
		} catch (e: Exception) {
			Resource.Fail(errorMessage = e.message)
		}
	}
	
	override suspend fun updatePass(pass: String): Resource<Unit> {
		return try {
			firebaseAuth.currentUser!!.updatePassword(pass).await()
			Resource.Success(Unit)
		} catch (e: Exception) {
			Resource.Fail(errorMessage = e.message)
		}
	}
	
	override suspend fun updateProfile(
		name: String?,
		email: String?,
		image: String?
	): Resource<Unit> {
		return try {
			val currentUser = firebaseAuth.currentUser!!
			val profileUpdates = userProfileChangeRequest {
				name?.let {
					displayName = it
				}
				image?.let {
					photoUri = it.toUri()
				}
			}
			currentUser.updateProfile(profileUpdates).await()
			email?.let {fEmail ->
				currentUser.verifyBeforeUpdateEmail(fEmail).addOnSuccessListener {
					currentUser.updateEmail(fEmail)
				}.addOnFailureListener {
				}.await()
			}
			Resource.Success(Unit)
		} catch (e: Exception) {
			Resource.Fail(errorMessage = e.message)
		}
	}
	
	override suspend fun oneTapSignIn(): Resource<IntentSender> {
		return try {
			val result = oneTapClient.beginSignIn(
				signInRequest
			).await()
			Resource.Success(result.pendingIntent.intentSender)
		} catch(e: Exception) {
			Resource.Fail(errorMessage = e.message)
		}
	}
	
	override suspend fun signInWithGoogle(intent: Intent): Resource<Unit> {
		val googleIdToken = oneTapClient.getSignInCredentialFromIntent(intent)
		val googleCredential = GoogleAuthProvider.getCredential(googleIdToken.googleIdToken, null)
		return try {
			val fireUser = firebaseAuth.signInWithCredential(googleCredential).await().user!!
			val user = FireUserEntity(
				fireUser.uid,
				fireUser.displayName,
				fireUser.email ?: "",
				null,
				fireUser.uid,
			)
			fireStoreUserCollection.document(fireUser.uid)
				.set(user)
				.await()
			Resource.Success(Unit)
		} catch (e: Exception) {
			Resource.Fail(errorMessage = e.message)
		}
	}
}