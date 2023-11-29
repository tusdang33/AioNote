package com.notes.aionote.domain.repository

import com.notes.aionote.common.Resource
import com.notes.aionote.domain.local_data.CategoryEntity
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
	fun getAllCategory(): Flow<Resource<List<CategoryEntity>>>
	suspend fun addCategory(category: CategoryEntity) :Resource<Unit>
	suspend fun updateCategory(category: CategoryEntity)
	suspend fun deleteCategory(categoryId: String)
	suspend fun deleteAllCategory()
}