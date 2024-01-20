package com.notes.aionote.presentation.note.conflicted_note

import com.notes.aionote.domain.local_data.NoteEntity
import com.notes.aionote.domain.remote_data.FireNoteEntity

interface ResolveConflict {
	suspend fun callback(param: List<Pair<NoteEntity, FireNoteEntity>>, userId: String): List<Pair<NoteEntity?, FireNoteEntity?>>
}