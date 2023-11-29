package com.notes.aionote.domain.repository

import com.notes.aionote.common.Resource
import com.notes.aionote.domain.local_data.NoteEntity
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
	fun getAllNote(): Flow<Resource<List<NoteEntity>>>
	suspend fun getSnapShotOfAllNote(): Resource<List<NoteEntity>>
	suspend fun getNoteById(noteId: String): Resource<NoteEntity?>
	fun getNoteByKeyword(keyword: String): Flow<Resource<List<NoteEntity>>>
	fun getNoteQuantityByCategory(categoryId : String): Flow<Resource<Int>>
	
	suspend fun insertNote(noteEntity: NoteEntity)
	suspend fun updateNote(noteEntity: NoteEntity, updateVersion: Boolean = true) : Resource<Unit>
	suspend fun updateNoteCategory(
		categoryId: String,
		noteId: String
	)
	
	suspend fun deleteNote(noteId: String)
	suspend fun deleteAllNote()
	suspend fun getDeletedNoteId() : Resource<List<String>>
	suspend fun deleteAllDeletedNoteId()
}