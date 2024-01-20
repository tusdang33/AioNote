package com.notes.aionote.data.model

import com.notes.aionote.domain.local_data.NoteEntity
import com.notes.aionote.domain.remote_data.FireNoteEntity
import com.notes.aionote.domain.repository.AudioRecorder
import com.notes.aionote.presentation.note.NoteType
import io.realm.kotlin.ext.toRealmList
import org.mongodb.kbson.ObjectId

data class Note(
	val noteId: String,
	val notes: List<NoteContent> = listOf(),
	val title: String? = "",
	val createTime: Long = System.currentTimeMillis(),
	val noteType: NoteType,
	val category: Category? = null,
	val deadLine: Long? = null,
	val version: Long = 1
)

fun NoteEntity.toNote(audioRecorder: AudioRecorder? = null) : Note {
	return Note(
		noteId = this.noteId.toHexString(),
		notes = this.notes.map { it.toNoteContent(audioRecorder) },
		title = this.title,
		createTime = this.createTime,
		noteType = NoteType.convertNoteType(this.noteType),
		category = this.category?.toCategory(),
		deadLine = this.deadline,
		version = this.version
	)
}

fun NoteEntity.toFireNote() : FireNoteEntity {
	return FireNoteEntity(
		noteId = this.noteId.toHexString(),
		notes = this.notes.map { it.toFireNoteContent() },
		title = this.title,
		createTime = this.createTime,
		noteType = this.noteType,
		category = this.category?.toFireCategory(),
		deadLine = this.deadline,
		version = this.version,
		lastModifierTime = this.lastModifierTime
	)
}

fun FireNoteEntity.toNoteEntity(): NoteEntity {
	return NoteEntity().apply {
		noteId = ObjectId.invoke(hexString = this@toNoteEntity.noteId)
		notes = this@toNoteEntity.notes.map { it.toNoteContentEntity() }.toRealmList()
		title = this@toNoteEntity.title
		createTime = this@toNoteEntity.createTime
		noteType = this@toNoteEntity.noteType
		category = this@toNoteEntity.category?.toCategoryEntity()
		deadline = this@toNoteEntity.deadLine
		version = this@toNoteEntity.version
		lastModifierTime = this@toNoteEntity.lastModifierTime
	}
}