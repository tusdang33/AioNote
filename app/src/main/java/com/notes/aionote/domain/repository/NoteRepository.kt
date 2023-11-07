package com.notes.aionote.domain.repository

import com.notes.aionote.common.Resource
import com.notes.aionote.domain.local_data.NoteEntity
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
	fun getAllNote(): Flow<Resource<List<NoteEntity>>>
	fun getNoteById(noteId: String): Resource<NoteEntity?>
	fun getCategory(): Flow<Resource<List<String>>>
	suspend fun insertNote(noteEntity: NoteEntity)
	suspend fun updateNote(noteEntity: NoteEntity) : Resource<Unit>
	suspend fun updateNoteCategory(
		categoryId: String,
		noteId: String
	)
	
	suspend fun deleteNote(noteId: String)
}