package com.notes.aionote.di

import android.content.Context
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.notes.aionote.R
import com.notes.aionote.common.AioFirebaseCollection
import com.notes.aionote.common.CollectionRef
import com.notes.aionote.data.repository.AuthRepositoryImp
import com.notes.aionote.domain.repository.AuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
	@Singleton
	@Provides
	fun provideAuthRepository(
		authRepository: AuthRepositoryImp
	): AuthRepository = authRepository
	
	@Singleton
	@Provides
	fun provideSignInRequest(@ApplicationContext context: Context): BeginSignInRequest =
		BeginSignInRequest.Builder()
			.setGoogleIdTokenRequestOptions(
				BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
					.setSupported(true)
					.setFilterByAuthorizedAccounts(false)
					.setServerClientId(context.getString(R.string.web_client_id))
					.build()
			)
			.setAutoSelectEnabled(true)
			.build()
	
	@Singleton
	@Provides
	fun provideGoogleSignInClient(@ApplicationContext context: Context): SignInClient =
		Identity.getSignInClient(context)
	
	@Singleton
	@Provides
	@CollectionRef(AioFirebaseCollection.USER)
	fun provideFireStoreUserCollection(): CollectionReference =
		Firebase.firestore.collection(AioFirebaseCollection.USER.ref)
	
	@Singleton
	@Provides
	@Named("UserCollection2")
	fun provideFireStoreAuthCollection2(): CollectionReference =
		Firebase.firestore.collection("user")
}