package com.notes.aionote.data.repository

import android.util.Log
import com.notes.aionote.common.Resource
import com.notes.aionote.domain.data.CategoryEntity
import com.notes.aionote.domain.data.NoteEntity
import com.notes.aionote.domain.repository.NoteRepository
import io.realm.kotlin.Realm
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import org.mongodb.kbson.ObjectId
import javax.inject.Inject

class LocalNoteRepositoryImpl @Inject constructor(
	private val realm: Realm
): NoteRepository {
	override fun getAllNote(): Flow<Resource<List<NoteEntity>>> {
		return try {
			realm.query<NoteEntity>().asFlow().map { Resource.Success(it.list) }
		} catch (e: Exception) {
			flow { emit(Resource.Fail(e.message)) }
		}
	}
	
	override fun getNoteById(noteId: String): Resource<NoteEntity?> {
		return try {
			val result = realm.query<NoteEntity>(
				query = "noteId == $0",
				ObjectId(hexString = noteId)
			)
				.first()
				.find()
			Resource.Success(result)
		} catch (e: Exception) {
			Resource.Fail(errorMessage = e.message)
		}
	}
	
	override fun getCategory(): Flow<Resource<List<String>>> {
//		return try {
//			realm.query<NoteEntity>()
//				.asFlow()
//				.map { Resource.Success(it.list.map { noteEntity -> noteEntity.category }) }
//		} catch (e: Exception) {
//			flow { emit(Resource.Fail(e.message)) }
//		}
		return flow { emit(Resource.Fail("")) }
	}
	
	override suspend fun insertNote(noteEntity: NoteEntity) {
		realm.write { copyToRealm(noteEntity) }
	}
	
	override suspend fun updateNote(noteEntity: NoteEntity): Resource<Unit> {
		return try {
			realm.write { copyToRealm(instance = noteEntity, updatePolicy = UpdatePolicy.ALL) }
			Resource.Success(Unit)
		} catch (e: Exception) {
			Resource.Fail(e.message)
		}
	}
	
	override suspend fun updateNoteCategory(
		categoryId: String,
		noteId: String
	) {
		realm.write {
			val noteEntity = query<NoteEntity>(
				query = "noteId == $0",
				ObjectId(hexString = noteId)
			).first().find()
			val category = query<CategoryEntity>(
				query = "categoryId = $0",
				ObjectId(hexString = categoryId)
			).first().find()
			noteEntity?.category = category
		}
	}
	
	override suspend fun deleteNote(noteId: String) {
		realm.write {
			val noteEntity = query<NoteEntity>(
				query = "noteId == $0",
				ObjectId(hexString = noteId)
			).first().find()
			try {
				noteEntity?.let { delete(it) }
			} catch (e: Exception) {
				Log.d("tudm", "${e.message}")
			}
		}
	}
	
}