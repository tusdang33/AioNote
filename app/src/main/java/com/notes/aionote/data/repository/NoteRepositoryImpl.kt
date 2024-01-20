package com.notes.aionote.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.notes.aionote.common.Resource
import com.notes.aionote.domain.local_data.CategoryEntity
import com.notes.aionote.domain.local_data.DeletedNoteEntity
import com.notes.aionote.domain.local_data.NoteEntity
import com.notes.aionote.domain.repository.NoteRepository
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.types.annotations.Index
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import org.mongodb.kbson.ObjectId
import javax.inject.Inject

class NoteRepositoryImpl @Inject constructor(
	private val realm: Realm
): NoteRepository {
	override fun getAllNote(): Flow<Resource<List<NoteEntity>>> {
		return try {
			realm.query<NoteEntity>().asFlow().map { Resource.Success(it.list) }
		} catch (e: Exception) {
			flow { emit(Resource.Fail(e.message)) }
		}
	}
	
	override fun getAllTask(): Flow<Resource<List<NoteEntity>>> {
		return try {
			realm.query<NoteEntity>(
				query = "noteType == 1"
			).asFlow().map { Resource.Success(it.list) }
		} catch (e: Exception) {
			flow { emit(Resource.Fail(e.message)) }
		}
	}
	
	override suspend fun getSnapShotOfAllNote(): Resource<List<NoteEntity>> {
		return try {
			Resource.Success(realm.query<NoteEntity>().find())
		} catch (e: Exception) {
			Resource.Fail(e.message)
		}
	}
	
	override suspend fun getNoteById(noteId: String): Resource<NoteEntity?> {
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
	
	override fun getNoteByKeyword(keyword: String): Flow<Resource<List<NoteEntity>>> {
		return try {
			realm.query<NoteEntity>(
				query = "ANY notes.content CONTAINS[c] $0 OR title CONTAINS[c] $0",
				keyword
			).asFlow().map { Resource.Success(it.list) }
		} catch (e: Exception) {
			flow { emit(Resource.Fail(e.message)) }
		}
	}
	
	override fun getNoteQuantityByCategory(categoryId: String): Flow<Resource<Int>> {
		return try {
			realm.query<NoteEntity>(
				query = "category.categoryId == $0 ",
				ObjectId(hexString = categoryId)
			).asFlow().map { Resource.Success(it.list.size) }
		} catch (e: Exception) {
			flow { emit(Resource.Fail(e.message)) }
		}
	}
	
	override suspend fun insertNote(noteEntity: NoteEntity) {
		realm.write { copyToRealm(noteEntity) }
	}
	
	override suspend fun updateNote(
		noteEntity: NoteEntity,
		updateVersion: Boolean
	): Resource<Unit> {
		return try {
			realm.write {
				val realmNoteEntity = query<NoteEntity>(
					query = "noteId == $0",
					noteEntity.noteId
				).find().first()
				findLatest(realmNoteEntity)?.let {
					it.notes = noteEntity.notes
					it.notes = noteEntity.notes
					it.title = noteEntity.title
					it.createTime = noteEntity.createTime
					it.noteType = noteEntity.noteType
					it.category = noteEntity.category
					it.deadLine = noteEntity.deadLine
					it.version = it.version + if(updateVersion) 1L else 0L
				}
			}
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
			noteEntity?.version = noteEntity?.version?.plus(1L) ?: 0L
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
				copyToRealm(DeletedNoteEntity().apply { deletedId = noteId })
			} catch (e: Exception) {
				Log.d("tudm", "${e.message}")
			}
		}
	}
	
	override suspend fun deleteAllNote() {
		realm.write {
			val noteEntity = query<NoteEntity>().find()
			try {
				delete(noteEntity)
			} catch (e: Exception) {
				Log.d("tudm", "${e.message}")
			}
		}
	}
	
	override suspend fun getDeletedNoteId(): Resource<List<String>> {
		return try {
			Resource.Success(realm.query<DeletedNoteEntity>().find().mapNotNull { it.deletedId })
		} catch (e: Exception) {
			Resource.Fail(e.message)
		}
	}
	
	override suspend fun deleteAllDeletedNoteId() {
		realm.write {
			val deletedNoteEntity = query<DeletedNoteEntity>().find()
			try {
				delete(deletedNoteEntity)
			} catch (e: Exception) {
				Log.d("tudm", "${e.message}")
			}
		}
	}
}