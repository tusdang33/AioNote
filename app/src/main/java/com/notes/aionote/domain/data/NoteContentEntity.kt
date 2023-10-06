package com.notes.aionote.domain.data

import io.realm.kotlin.types.EmbeddedRealmObject
import io.realm.kotlin.types.RealmAny
import io.realm.kotlin.types.RealmObject
import org.mongodb.kbson.ObjectId

class NoteContentEntity : EmbeddedRealmObject {
	/*
	0 -> TEXT
	1 -> CHECK
	2 -> MEDIA
	 */
	var noteContentType : Int = 0
	var content: String? = null
	var checked: Boolean? = null
	/*
	0 -> IMAGE
	1 -> VIDEO
	2 -> VOICE
	 */
	var mediaType: Int? = null
	var mediaPath: String? = null
}

