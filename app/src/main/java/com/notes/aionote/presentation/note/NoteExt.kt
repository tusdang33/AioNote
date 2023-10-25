package com.notes.aionote.presentation.note

enum class NoteType {
	NORMAL, TASK;
	
	companion object {
		fun convertNoteType(noteType: Int): NoteType {
			return if (noteType == 0) {
				NORMAL
			} else {
				TASK
			}
		}
	}
}

enum class NoteContentType {
	TEXT, CHECK, MEDIA
}