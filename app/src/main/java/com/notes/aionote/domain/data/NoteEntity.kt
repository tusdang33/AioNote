package com.notes.aionote.domain.data

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.Index
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class NoteEntity : RealmObject {
	@PrimaryKey
	var noteId: ObjectId = ObjectId.invoke()
	var notes: RealmList<NoteContentEntity> = realmListOf()
	var title: String? = ""
	@Index
	var createTime: Long = System.currentTimeMillis()
	/*
	0 -> NORMAL
	1 -> TASK
	 */
	var noteType: Int = 0
	var category: String = ""
	var deadLine: Long? = null
}