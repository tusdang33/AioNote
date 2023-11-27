package com.notes.aionote.di

import com.notes.aionote.data.repository.LocalCategoryRepositoryImpl
import com.notes.aionote.data.repository.NoteRepositoryImpl
import com.notes.aionote.domain.local_data.CategoryEntity
import com.notes.aionote.domain.local_data.DeletedNoteEntity
import com.notes.aionote.domain.local_data.NoteContentEntity
import com.notes.aionote.domain.local_data.NoteEntity
import com.notes.aionote.domain.repository.CategoryRepository
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
				NoteContentEntity::class,
				CategoryEntity::class,
				DeletedNoteEntity::class
			)
		).compactOnLaunch().build()
		return Realm.open(config)
	}
	
	@Provides
	@Singleton
	fun provideRealmNoteRepository(realm: Realm): NoteRepository =
		NoteRepositoryImpl(realm = realm)
	
	@Provides
	@Singleton
	fun provideRealmCategoryRepository(realm: Realm): CategoryRepository =
		LocalCategoryRepositoryImpl(realm = realm)
}