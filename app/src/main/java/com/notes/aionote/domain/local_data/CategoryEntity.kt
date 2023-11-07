package com.notes.aionote.domain.local_data

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class CategoryEntity: RealmObject {
	@PrimaryKey
	var categoryId: ObjectId = ObjectId.invoke()
	var category: String? = null
}