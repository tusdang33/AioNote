package com.notes.aionote.presentation.note.conflicted_note

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.work.WorkManager
import com.notes.aionote.common.AioConst
import com.notes.aionote.common.RootState
import com.notes.aionote.common.RootViewModel
import com.notes.aionote.data.model.Note
import com.notes.aionote.data.model.toNote
import com.notes.aionote.data.model.toNoteEntity
import com.notes.aionote.domain.local_data.NoteEntity
import com.notes.aionote.domain.remote_data.FireNoteEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ConflictedNoteViewModel @Inject constructor():
	RootViewModel<ConflictedNoteUiState, ConflictedNoteOneTimeEvent, ConflictedNoteEvent>() {
	
	private val _conflictedNoteUiState = MutableStateFlow(ConflictedNoteUiState())
	override val uiState: StateFlow<ConflictedNoteUiState> = _conflictedNoteUiState.asStateFlow()
	
	override fun reduceUiStateFromOneTimeEvent(
		uiState: ConflictedNoteUiState,
		oneTimeEvent: ConflictedNoteOneTimeEvent
	) {
		_conflictedNoteUiState.value = uiState
	}
	
	override fun onEvent(event: ConflictedNoteEvent) {
		when (event) {
			ConflictedNoteEvent.OnAcceptLocal -> {
				ConflictPromise.completableDeferred.complete(
					ConflictPromise.listConflictedNote.map {
						Pair(it.first, null)
					}
				)
				dispose()
			}
			
			ConflictedNoteEvent.OnAcceptRemote -> {
				ConflictPromise.completableDeferred.complete(
					ConflictPromise.listConflictedNote.map {
						Pair(null, it.second)
					}
				)
				dispose()
			}
			
			is ConflictedNoteEvent.OnPickedNote -> {
				updatePickedNote(event.index)
			}
			
			ConflictedNoteEvent.OnResolve -> {
				val listPairConflict = ConflictPromise.listConflictedNote
				val listNote = _conflictedNoteUiState.value.listConflictedNote
				val result = mutableListOf<Pair<NoteEntity?, FireNoteEntity?>>()
				for (index in listNote.indices step 2) {
					if (listNote[index].isPicked) {
						result.add(Pair(listPairConflict[index].first, null))
					} else {
						result.add(Pair(null, listPairConflict[index].second))
					}
				}
				ConflictPromise.completableDeferred.complete(result)
				dispose()
			}
			
			ConflictedNoteEvent.OnFetchData -> {
				fetchData()
			}
			
			is ConflictedNoteEvent.OnPreviewNote -> {
				_conflictedNoteUiState.update {
					it.copy(
						notePreviewing = it.listConflictedNote[event.index].note
					)
				}
			}
			
			ConflictedNoteEvent.OnDisposeNote -> {
				_conflictedNoteUiState.update {
					it.copy(
						notePreviewing = null
					)
				}
			}
			
			ConflictedNoteEvent.OnDispose -> {
				dispose()
			}
		}
	}
	
	private fun fetchData() {
		val listNote = mutableListOf<Note>()
		ConflictPromise.listConflictedNote.forEach {
			listNote.add(it.first.toNote())
			listNote.add(it.second.toNoteEntity().toNote())
		}
		_conflictedNoteUiState.update { uiState ->
			uiState.copy(listConflictedNote = listNote.map { PickingConflictNote(note = it) }
				.toMutableStateList())
		}
	}
	
	private fun updatePickedNote(index: Int) {
		val listNote = _conflictedNoteUiState.value.listConflictedNote
		val note = listNote.getOrNull(index)
		note?.let { pickingNote ->
			_conflictedNoteUiState.update { uiState ->
				uiState.copy(
					listConflictedNote = uiState.listConflictedNote.apply {
						if (index % 2 == 0) {
							val nextNote = listNote[index + 1]
							if (nextNote.isPicked) {
								set(index + 1, nextNote.copy(isPicked = false))
							}
							
						} else {
							val prevNote = listNote[index - 1]
							if (prevNote.isPicked) {
								set(index - 1, prevNote.copy(isPicked = false))
							}
						}
						set(index, pickingNote.copy(isPicked = !pickingNote.isPicked))
					},
					enabledResolve = uiState.listConflictedNote.filter { it.isPicked }.size == uiState.listConflictedNote.size / 2
				)
			}
		}
	}
	
	private fun dispose() {
		ConflictPromise.dispose()
	}
}

data class ConflictedNoteUiState(
	override val isLoading: Boolean = false,
	override val errorMessage: String? = null,
	val listConflictedNote: SnapshotStateList<PickingConflictNote> = mutableStateListOf(),
	val notePreviewing: Note? = null,
	val enabledResolve: Boolean = false
): RootState.ViewUiState

sealed interface ConflictedNoteOneTimeEvent: RootState.OneTimeEvent<ConflictedNoteUiState>

sealed class ConflictedNoteEvent: RootState.ViewEvent {
	object OnAcceptLocal: ConflictedNoteEvent()
	object OnAcceptRemote: ConflictedNoteEvent()
	object OnResolve: ConflictedNoteEvent()
	object OnFetchData: ConflictedNoteEvent()
	data class OnPickedNote(val index: Int): ConflictedNoteEvent()
	data class OnPreviewNote(val index: Int): ConflictedNoteEvent()
	object OnDisposeNote: ConflictedNoteEvent()
	object OnDispose: ConflictedNoteEvent()
}

data class PickingConflictNote(
	val note: Note,
	val isPicked: Boolean = false
)
