package com.notes.aionote.domain.remote_data

data class FireNoteEntity(
	val noteId: String = "",
	val notes: List<FireNoteContent> = listOf(),
	val title: String? = "",
	val createTime: Long = System.currentTimeMillis(),
	val noteType: Int = 0,
	val category: FireCategoryEntity? = null,
	val deadLine: Long? = null,
	val version: Long = 1,
	val lastModifierTime: Long = 0
)