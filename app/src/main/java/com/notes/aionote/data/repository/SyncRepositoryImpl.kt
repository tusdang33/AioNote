package com.notes.aionote.data.repository

import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.notes.aionote.common.AioFirebaseCollection
import com.notes.aionote.common.CollectionRef
import com.notes.aionote.common.FirebaseConst
import com.notes.aionote.common.Resource
import com.notes.aionote.common.fail
import com.notes.aionote.common.success
import com.notes.aionote.data.model.toFireNote
import com.notes.aionote.data.model.toNoteEntity
import com.notes.aionote.domain.local_data.NoteEntity
import com.notes.aionote.domain.remote_data.FireNoteEntity
import com.notes.aionote.domain.repository.AuthRepository
import com.notes.aionote.domain.repository.MediaRepository
import com.notes.aionote.domain.repository.NoteRepository
import com.notes.aionote.domain.repository.SyncRepository
import io.realm.kotlin.ext.toRealmList
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class SyncRepositoryImpl @Inject constructor(
	private val authRepository: AuthRepository,
	private val noteRepository: NoteRepository,
	private val mediaRepository: MediaRepository,
	@CollectionRef(AioFirebaseCollection.USER) private val fireStoreUserCollection: CollectionReference,
): SyncRepository {
	private fun compareNoteForLocalSync(
		remoteData: List<FireNoteEntity>,
		localData: List<NoteEntity>
	): Pair<List<NoteEntity>, List<NoteEntity>> {
		val newNote = mutableListOf<NoteEntity>()
		val updateNote = mutableListOf<NoteEntity>()
		newNote.addAll(remoteData.filter { fireNote ->
			val existNoteInLocal = localData.firstOrNull { noteEntity ->
				noteEntity.noteId.toHexString() == fireNote.noteId
			}
			if (existNoteInLocal != null && existNoteInLocal.version < fireNote.version) {
				updateNote.add(fireNote.toNoteEntity())
			}
			fireNote.noteId !in localData.map { it.noteId.toHexString() }
		}.map { it.toNoteEntity() })
		
		return Pair(newNote, updateNote)
	}
	
	private fun compareNoteForRemoteSync(
		remoteData: List<FireNoteEntity>,
		localData: List<NoteEntity>
	): Pair<List<FireNoteEntity>, List<FireNoteEntity>> {
		val newNote = mutableListOf<FireNoteEntity>()
		val updateNote = mutableListOf<FireNoteEntity>()
		newNote.addAll(localData.filter { noteEntity ->
			val existNoteInRemote = remoteData.firstOrNull { fireNote ->
				fireNote.noteId == noteEntity.noteId.toHexString()
			}
			if (existNoteInRemote != null && existNoteInRemote.version < noteEntity.version) {
				updateNote.add(noteEntity.toFireNote())
			}
			noteEntity.noteId.toHexString() !in remoteData.map { it.noteId }
		}.map { it.toFireNote() })
		
		return Pair(newNote, updateNote)
	}
	
	private suspend fun getLocalData() : List<NoteEntity> {
		val completableDeferred = CompletableDeferred<List<NoteEntity>>()
		
		noteRepository.getSnapShotOfAllNote().success {
			val localData = it ?: listOf()
			completableDeferred.complete(localData)
		}
		
		return completableDeferred.await()
	}
	
	private suspend fun getRemoteData(userId: String) : List<FireNoteEntity> {
		val completableDeferred = CompletableDeferred<List<FireNoteEntity>>()
		fireStoreUserCollection.document(userId)
			.collection(FirebaseConst.FIREBASE_NOTE_COL_REF)
			.get()
			.addOnSuccessListener { snapshot ->
				completableDeferred.complete(snapshot.documents.mapNotNull { it.toObject<FireNoteEntity>() })
			}
		return completableDeferred.await()
	}
	
	private suspend fun downloadMedia(
		listNote: List<NoteEntity>,
		userId: String
	): List<NoteEntity> {
		return listNote.map { note ->
			note.apply {
				notes = note.notes.map { noteContentEntity ->
					if (noteContentEntity.mediaPath.isNullOrBlank()) {
						noteContentEntity
					} else {
						var path = ""
						mediaRepository.downloadMedia(
							fileName = noteContentEntity.mediaPath!!,
							userId = userId,
							noteId = note.noteId.toHexString()
						).success {
							path = it ?: ""
						}.fail {
							throw Exception("Download media fail")
						}
						noteContentEntity.apply {
							mediaPath = path
						}
					}
				}.toRealmList()
			}
		}
	}
	
	private suspend fun uploadMedia(
		notes: List<FireNoteEntity>,
		userId: String
	): List<FireNoteEntity> {
		return notes.map { note ->
			note.copy(
				notes = note.notes.map { noteContent ->
					if (noteContent.mediaPath.isNullOrBlank()) {
						noteContent
					} else {
						var path = ""
						mediaRepository.uploadMedia(
							mediaPath = noteContent.mediaPath!!,
							userId = userId,
							noteId = note.noteId
						).success {
							path = it ?: ""
						}.fail {
							throw Exception("Upload media fail")
						}
						noteContent.copy(
							mediaPath = path
						)
					}
				}
			)
		}
	}
	
	private suspend fun getUserNoteRef(userId: String): String? {
		val completableDeferred = CompletableDeferred<String?>()
		authRepository.getUserNoteRef(userId).success {
			completableDeferred.complete(it)
		}
		return completableDeferred.await()
	}
	
	private suspend fun syncDeletedNote(userNoteDocumentRef: CollectionReference) {
		noteRepository.getDeletedNoteId().success { deletedList ->
			Firebase.firestore.runTransaction { transaction ->
				deletedList?.forEach { noteId ->
					transaction.delete(userNoteDocumentRef.document(noteId))
				}
			}.await()
			noteRepository.deleteAllDeletedNoteId()
		}
		
	}
	
	private suspend fun syncToDevice(userId: String, remoteData: List<FireNoteEntity>, localData: List<NoteEntity>) {
		val pairNote = compareNoteForLocalSync(remoteData, localData)
		val prepareListNote = downloadMedia(
			listNote = pairNote.first + pairNote.second,
			userId = userId
		)
		prepareListNote.subList(0, pairNote.first.size).forEach {
			noteRepository.insertNote(it)
		}
		prepareListNote.subList(pairNote.first.size, prepareListNote.size).forEach {
			noteRepository.updateNote(it)
		}
	}
	
	private suspend fun syncToRemote(
		userId: String,
		userNoteDocumentRef: CollectionReference,
		remoteData: List<FireNoteEntity>,
		localData: List<NoteEntity>
	) {
		val pairNote = compareNoteForRemoteSync(remoteData, localData)
		val prepareListNote = uploadMedia(notes = pairNote.first + pairNote.second, userId = userId)
		Firebase.firestore.runTransaction { transaction ->
			prepareListNote.forEach { fireNote ->
				transaction.set(
					userNoteDocumentRef.document(fireNote.noteId),
					fireNote,
					SetOptions.merge()
				)
			}
		}.await()
	}
	
	override suspend fun sync(userId: String): Resource<Unit> {
		return try {
			val userNoteRef = getUserNoteRef(userId) ?: throw Exception("Get user note ref fail $userId")
			val userNoteDocumentRef = fireStoreUserCollection.document(userNoteRef)
				.collection(FirebaseConst.FIREBASE_NOTE_COL_REF)

			syncDeletedNote(userNoteDocumentRef)
			val localData =  getLocalData()
			val remoteData = getRemoteData(userId)
			
			syncToDevice(userId, remoteData, localData)
			syncToRemote(userId, userNoteDocumentRef, remoteData, localData)
			Resource.Success(Unit)
		} catch (e: Exception) {
			Resource.Fail(errorMessage = e.message)
		}
	}
}