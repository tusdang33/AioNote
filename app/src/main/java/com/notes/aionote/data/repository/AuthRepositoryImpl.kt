package com.notes.aionote.data.repository

import android.content.Intent
import android.content.IntentSender
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.CollectionReference
import com.notes.aionote.common.AioFirebaseCollection
import com.notes.aionote.common.CollectionRef
import com.notes.aionote.common.Resource
import com.notes.aionote.data.model.User
import com.notes.aionote.domain.repository.AuthRepository
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
	
	override suspend fun login(
		email: String,
		pass: String
	): Resource<Unit> {
		return try {
			firebaseAuth.signInWithEmailAndPassword(email, pass).await()
			Resource.Success(Unit)
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
		fullName: String,
	): Resource<User> {
		return try {
			val fireUser = firebaseAuth.createUserWithEmailAndPassword(email, pass).await().user!!
			val user = User(fireUser.uid, fullName, email, null)
			fireStoreUserCollection.document(fireUser.uid)
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
		name: String,
		email: String
	): Resource<Unit> {
		return try {
			val profileUpdates = userProfileChangeRequest {
				displayName = name
			}
			firebaseAuth.currentUser!!.updateProfile(profileUpdates).await()
			firebaseAuth.currentUser!!.updateEmail(email).await()
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
			val user = User(fireUser.uid, fireUser.displayName, fireUser.email ?: "", null)
			fireStoreUserCollection.document(fireUser.uid)
				.set(user)
				.await()
			Resource.Success(Unit)
		} catch (e: Exception) {
			Resource.Fail(errorMessage = e.message)
		}
	}
}