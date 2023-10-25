package com.notes.aionote.data.repository

import android.util.Log
import com.notes.aionote.common.Resource
import com.notes.aionote.domain.data.CategoryEntity
import com.notes.aionote.domain.repository.CategoryRepository
import io.realm.kotlin.Realm
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import org.mongodb.kbson.BsonObjectId
import javax.inject.Inject

class LocalCategoryRepositoryImpl @Inject constructor(
	private val realm: Realm
): CategoryRepository {
	override fun getAllCategory(): Flow<Resource<List<CategoryEntity>>> {
		return try {
			realm.query<CategoryEntity>().asFlow().map { Resource.Success(it.list) }
		} catch (e: Exception) {
			flow { emit(Resource.Fail(e.message)) }
		}
	}
	
	override suspend fun addCategory(category: CategoryEntity): Resource<Unit> {
		return try {
			realm.write { copyToRealm(instance = category) }
			Resource.Success(Unit)
		} catch (e :Exception) {
			Resource.Fail(e.message)
		}
	}
	
	override suspend fun updateCategory(category: CategoryEntity) {
		realm.write { copyToRealm(instance = category, updatePolicy = UpdatePolicy.ALL) }
	}
	
	override suspend fun deleteCategory(categoryId: String) {
		realm.write {
			val noteEntity = query<CategoryEntity>(
				query = "categoryId == $0",
				BsonObjectId(hexString = categoryId)
			).first().find()
			try {
				noteEntity?.let { delete(it) }
			} catch (e: Exception) {
				Log.d("tudm", "${e.message}")
			}
		}
	}
}