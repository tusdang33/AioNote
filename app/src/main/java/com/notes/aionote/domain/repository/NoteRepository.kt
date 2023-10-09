package com.notes.aionote.domain.repository

import com.notes.aionote.common.Resource
import com.notes.aionote.domain.data.NoteEntity
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
	fun getAllNote(): Flow<Resource<List<NoteEntity>>>
	fun getNoteById(noteId: String): Resource<NoteEntity?>
	suspend fun insertNote(noteEntity: NoteEntity)
	suspend fun updateNote(noteEntity: NoteEntity)
	suspend fun deleteNote(noteId: String)
}