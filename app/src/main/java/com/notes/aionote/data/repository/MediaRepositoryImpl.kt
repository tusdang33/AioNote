package com.notes.aionote.data.repository

import android.content.Context
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.notes.aionote.common.FirebaseConst
import com.notes.aionote.common.Resource
import com.notes.aionote.domain.repository.MediaRepository
import com.notes.aionote.getFileName
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import java.io.File
import javax.inject.Inject

class MediaRepositoryImpl @Inject constructor(
	@ApplicationContext private val context: Context
): MediaRepository {
	private val storageRef = Firebase.storage.reference
	override suspend fun uploadMedia(
		mediaPath: String,
		userId: String,
		noteId: String
	): Resource<String> {
		return try {
			val fileName = getFileName(mediaPath.toUri(), context) ?: System.currentTimeMillis()
				.toString()
			val childRef = storageRef.child(userId)
				.child(FirebaseConst.FIREBASE_NOTE_COL_REF)
				.child(noteId)
				.child(fileName)
			childRef.putFile(mediaPath.toUri()).await()
			Resource.Success(fileName)
		} catch (e: Exception) {
			Resource.Fail(e.message)
		}
	}
	
	override suspend fun downloadMedia(
		fileName: String,
		userId: String,
		noteId: String
	): Resource<String> {
		return try {
			val childRef = storageRef.child(userId)
				.child(FirebaseConst.FIREBASE_NOTE_COL_REF)
				.child(noteId)
				.child(fileName)
			val localFile = File(
				context.cacheDir,
				noteId
			)
			if (!localFile.exists()) {
				localFile.mkdirs()
			}
			
			val file = File(localFile, fileName)
			childRef.getFile(file).await()
			val fileUri = FileProvider.getUriForFile(
				context,
				context.packageName + ".file_provider",
				file
			)
			Resource.Success(fileUri.toString())
		} catch (e: Exception) {
			Resource.Fail(e.message)
		}
	}
	
	override suspend fun getDownloadUrl(
		fileName: String,
		userId: String,
		noteId: String
	): Resource<String> {
		return try {
			val childRef = storageRef.child(userId)
				.child(FirebaseConst.FIREBASE_NOTE_COL_REF)
				.child(noteId)
				.child(fileName)
			Resource.Success(childRef.downloadUrl.await().toString())
		} catch (e: Exception) {
			Resource.Fail(e.message)
		}
	}
}