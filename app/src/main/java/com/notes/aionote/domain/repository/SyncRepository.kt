package com.notes.aionote.domain.repository

import com.notes.aionote.common.Resource
import com.notes.aionote.domain.local_data.NoteEntity
import com.notes.aionote.domain.remote_data.FireNoteEntity

interface SyncRepository {
	suspend fun sync(userId: String): Resource<Unit>
}