package com.notes.aionote.data.model

import com.notes.aionote.domain.data.NoteEntity
import com.notes.aionote.domain.repository.AudioRecorder
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.ext.toRealmList

data class Note(
	val noteId: String,
	val notes: List<NoteContent> = listOf(),
	val title: String? = "",
	val createTime: Long = System.currentTimeMillis(),
	val noteType: Int,
	val deadLine: Long? = null
)

fun NoteEntity.toNote(audioRecorder: AudioRecorder? = null) : Note {
	return Note(
		noteId = this.noteId.toHexString(),
		notes = this.notes.map { it.toNoteContent(audioRecorder) },
		title = this.title,
		createTime = this.createTime,
		noteType = 1,
		deadLine = this.deadLine
	)
}

//fun Note.toNoteEntity() : NoteEntity {
//	return NoteEntity().apply {
//		notes = this@toNoteEntity.notes.map { it.toNoteContentEntity() }.toRealmList()
//		title = this@toNoteEntity.title
//		createTime = this@toNoteEntity.createTime
//		noteType = 1
//		deadLine = this@toNoteEntity.deadLine
//	}
//}