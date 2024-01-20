package com.notes.aionote.data.model

import com.notes.aionote.domain.local_data.CategoryEntity
import com.notes.aionote.domain.remote_data.FireCategoryEntity
import org.mongodb.kbson.ObjectId

data class Category(
	val categoryId: String = "",
	val category: String? = null
)

fun CategoryEntity.toCategory(): Category {
	return Category(
		categoryId = this.categoryId.toHexString(),
		category = this.category
	)
}

fun FireCategoryEntity.toCategory() : Category {
	return Category(
		categoryId = this.categoryId,
		category = this.category
	)
}

fun FireCategoryEntity.toCategoryEntity() : CategoryEntity {
	return CategoryEntity().apply {
		categoryId = ObjectId(hexString = this@toCategoryEntity.categoryId)
		category = this@toCategoryEntity.category
	}
}

fun CategoryEntity.toFireCategory() : FireCategoryEntity {
	return FireCategoryEntity(
		categoryId = this.categoryId.toHexString(),
		category = this.category
	)
}