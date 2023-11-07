package com.notes.aionote.data.model

import com.notes.aionote.domain.local_data.CategoryEntity

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