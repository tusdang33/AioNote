package com.notes.aionote.presentation.note.conflicted_note

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.notes.aionote.domain.local_data.NoteEntity
import com.notes.aionote.domain.remote_data.FireNoteEntity
import kotlinx.coroutines.CompletableDeferred

object ConflictPromise {
	var completableDeferred = CompletableDeferred<List<Pair<NoteEntity?, FireNoteEntity?>>>()
	var listConflictedNote = listOf<Pair<NoteEntity, FireNoteEntity>>()
	var resolveConflictScreenState by mutableStateOf(false)
	fun dispose() {
		listConflictedNote = listOf()
		resolveConflictScreenState = false
		completableDeferred = CompletableDeferred()
	}
}