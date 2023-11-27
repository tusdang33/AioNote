package com.notes.aionote.domain.local_data

import io.realm.kotlin.types.RealmObject

class DeletedNoteEntity: RealmObject {
	var deletedId: String? = null
}