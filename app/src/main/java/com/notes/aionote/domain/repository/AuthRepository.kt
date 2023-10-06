package com.notes.aionote.domain.repository

import android.content.Intent
import android.content.IntentSender
import com.notes.aionote.common.Resource
import com.notes.aionote.data.model.User

interface AuthRepository {
	suspend fun <T> getCurrentUser(): Resource<T>
	suspend fun login(
		email: String,
		pass: String
	): Resource<Unit>
	
	suspend fun logout(): Resource<Unit>
	suspend fun register(
		email: String,
		pass: String,
		fullName: String
	): Resource<User>
	
	suspend fun updatePass(pass: String): Resource<Unit>
	suspend fun updateProfile(
		name: String,
		email: String
	): Resource<Unit>
	
	suspend fun oneTapSignIn(): Resource<IntentSender>
	suspend fun signInWithGoogle(intent: Intent): Resource<Unit>
}