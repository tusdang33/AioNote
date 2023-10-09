package com.notes.aionote.data.repository

import android.util.Log
import com.notes.aionote.domain.data.NoteContentEntity
import com.notes.aionote.domain.data.NoteEntity
import com.notes.aionote.domain.repository.NoteRepository
import io.realm.kotlin.Realm
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.annotations.Index
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.mongodb.kbson.ObjectId
import javax.inject.Inject

class LocalNoteRepositoryImpl @Inject constructor(
	private val realm: Realm
): NoteRepository {
	override fun getAllNote(): Flow<List<NoteEntity>> =
		realm.query<NoteEntity>().asFlow().map { it.list }
	
	override fun getAllTask(): Flow<List<NoteEntity>> =
		realm.query<NoteEntity>(query = "noteType == $0", 2).asFlow().map { it.list }
	
	override fun getNoteById(noteId: String): NoteEntity? {
		return realm.query<NoteEntity>(query = "noteId == $0", ObjectId(hexString = noteId))
			.first()
			.find()
	}
	
	override suspend fun insertNote(noteEntity: NoteEntity) {
		realm.write { copyToRealm(noteEntity) }
	}
	
	override suspend fun updateNote(noteEntity: NoteEntity) {
		realm.write { copyToRealm(instance = noteEntity, updatePolicy = UpdatePolicy.ALL) }
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