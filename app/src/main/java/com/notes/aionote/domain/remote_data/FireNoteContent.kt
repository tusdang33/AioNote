package com.notes.aionote.domain.remote_data

data class FireNoteContent(
	/*
	0 -> TEXT
	1 -> CHECK
	2 -> MEDIA
	 */
	var noteContentType: Int = 0,
	var content: String? = null,
	var checked: Boolean? = null,
	/*
	0 -> IMAGE
	1 -> VIDEO
	2 -> VOICE
	2 -> ATTACHMENT
	 */
	var mediaType: Int? = null,
	var mediaPath: String? = null
)