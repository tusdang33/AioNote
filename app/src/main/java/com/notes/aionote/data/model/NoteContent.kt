package com.notes.aionote.data.model

import android.net.Uri
import androidx.core.net.toUri
import com.notes.aionote.common.success
import com.notes.aionote.domain.local_data.NoteContentEntity
import com.notes.aionote.domain.remote_data.FireNoteContent
import com.notes.aionote.domain.repository.AudioRecorder

interface NoteContent

object NoteContentNone: NoteContent

data class TextNote(
	val text: String = ""
): NoteContent

data class CheckNote(
	val checked: Boolean = false,
	val content: String = ""
): NoteContent

data class MediaNote(
	val mediaType: MediaType,
	val isPlaying: Boolean = false,
	val mediaPath: String = "",
	val mediaDuration: Long? = null
): NoteContent

enum class MediaType {
	IMAGE, VIDEO, VOICE, ATTACHMENT
}

fun NoteContentEntity.toNoteContent(audioRecorder: AudioRecorder? = null): NoteContent {
	return when (this.noteContentType) {
		0 -> {
			TextNote(text = this.content ?: "")
		}
		
		1 -> {
			CheckNote(
				checked = this.checked ?: false,
				content = this.content ?: ""
			)
		}
		
		2 -> {
			if (this.mediaType == MediaType.VOICE.ordinal) {
				MediaNote(
					mediaType = MediaType.VOICE,
					mediaPath = this.mediaPath ?: "",
					mediaDuration = run {
						audioRecorder?.getAudioDuration(this.mediaPath?.toUri() ?: Uri.EMPTY)
							?.success {
								return@run it
							}
						null
					}
				)
			} else {
				MediaNote(
					mediaType = this.mediaType?.toTypeMediaType() ?: MediaType.IMAGE,
					mediaPath = this.mediaPath ?: ""
				)
			}
		}
		
		else -> NoteContentNone
	}
}

fun NoteContent.toNoteContentEntity(): NoteContentEntity {
	return when (this) {
		is TextNote -> {
			NoteContentEntity().apply {
				noteContentType = 0
				content = this@toNoteContentEntity.text
			}
		}
		
		is CheckNote -> {
			NoteContentEntity().apply {
				noteContentType = 1
				content = this@toNoteContentEntity.content
				checked = this@toNoteContentEntity.checked
			}
		}
		
		is MediaNote -> {
			NoteContentEntity().apply {
				noteContentType = 2
				mediaType = this@toNoteContentEntity.mediaType.ordinal
				mediaPath = this@toNoteContentEntity.mediaPath
			}
		}
		
		else -> NoteContentEntity()
	}
}

fun NoteContentEntity.toFireNoteContent(): FireNoteContent {
	return FireNoteContent(
		noteContentType = this.noteContentType,
		content = this.content,
		checked = this.checked,
		mediaType = this.mediaType,
		mediaPath = this.mediaPath
	)
}

fun FireNoteContent.toNoteContentEntity(): NoteContentEntity {
	return NoteContentEntity().apply {
		noteContentType = this@toNoteContentEntity.noteContentType
		content = this@toNoteContentEntity.content
		checked = this@toNoteContentEntity.checked
		mediaType = this@toNoteContentEntity.mediaType
		mediaPath = this@toNoteContentEntity.mediaPath
	}
}

fun Int.toTypeMediaType(): MediaType {
	return when (this) {
		0 -> {
			MediaType.IMAGE
		}
		
		1 -> {
			MediaType.VIDEO
		}
		
		2 -> {
			MediaType.VOICE
		}
		
		3 -> {
			MediaType.ATTACHMENT
		}
		
		else -> MediaType.IMAGE
	}
}