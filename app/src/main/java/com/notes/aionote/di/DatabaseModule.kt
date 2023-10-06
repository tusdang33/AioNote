package com.notes.aionote.di

import com.notes.aionote.common.AioNoteRepoType
import com.notes.aionote.common.NoteRepoType
import com.notes.aionote.domain.data.NoteContentEntity
import com.notes.aionote.domain.data.NoteEntity
import com.notes.aionote.data.repository.LocalNoteRepositoryImpl
import com.notes.aionote.domain.repository.NoteRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
	@Provides
	@Singleton
	fun provideRealmDatabase(): Realm {
		val config = RealmConfiguration.Builder(
			schema = setOf(
				NoteEntity::class,
				NoteContentEntity::class
			)
		).compactOnLaunch().build()
		return Realm.open(config)
	}
	
	@Provides
	@Singleton
	@NoteRepoType(AioNoteRepoType.LOCAL)
	fun provideRealmNoteRepository(realm: Realm): NoteRepository = LocalNoteRepositoryImpl(realm = realm)
}