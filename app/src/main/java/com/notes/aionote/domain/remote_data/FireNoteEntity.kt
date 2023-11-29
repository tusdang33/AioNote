package com.notes.aionote.domain.remote_data

import com.notes.aionote.data.model.Category
import com.notes.aionote.data.model.NoteContent
import com.notes.aionote.presentation.note.NoteType

data class FireNoteEntity(
	val noteId: String = "",
	val notes: List<FireNoteContent> = listOf(),
	val title: String? = "",
	val createTime: Long = System.currentTimeMillis(),
	val noteType: Int = 0,
	val category: FireCategoryEntity? = null,
	val deadLine: Long? = null,
	val version: Long = 1
)