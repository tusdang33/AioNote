package com.notes.aionote.domain.repository

import com.notes.aionote.common.Resource
import com.notes.aionote.domain.local_data.NoteEntity
import com.notes.aionote.domain.remote_data.FireNoteEntity

interface SyncRepository {
	suspend fun sync(
		userId: String,
		resolveConflict: suspend (List<Pair<NoteEntity, FireNoteEntity>>) -> List<Pair<NoteEntity?, FireNoteEntity?>>
	): Resource<Unit>
}