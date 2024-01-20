package com.notes.aionote.presentation.search

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.viewModelScope
import com.notes.aionote.common.RootState
import com.notes.aionote.common.RootViewModel
import com.notes.aionote.common.fail
import com.notes.aionote.common.success
import com.notes.aionote.data.model.Note
import com.notes.aionote.data.model.toNote
import com.notes.aionote.domain.repository.NoteRepository
import com.notes.aionote.presentation.note.NoteType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
	private val noteRepository: NoteRepository,
): RootViewModel<SearchUiState, SearchOneTimeEvent, SearchEvent>() {
	
	private val _searchUiState = MutableStateFlow(SearchUiState())
	override val uiState: StateFlow<SearchUiState> = _searchUiState.asStateFlow()
	
	
	override fun reduceUiStateFromOneTimeEvent(
		uiState: SearchUiState,
		oneTimeEvent: SearchOneTimeEvent
	) {
		_searchUiState.value = uiState
	}
	
	override fun onEvent(event: SearchEvent) {
		when (event) {
			is SearchEvent.AddNoteToCategory -> {
				sendEvent(SearchOneTimeEvent.NavigateToCategory(event.noteId))
			}
			
			is SearchEvent.DeleteNote -> {
				removeNote(event.index)
			}
			
			is SearchEvent.OnNoteClick -> {
				if (event.noteType == NoteType.NORMAL) {
					sendEvent(SearchOneTimeEvent.NavigateToNote(event.noteId))
					
				} else {
					sendEvent(SearchOneTimeEvent.NavigateToTask(event.noteId))
				}
			}
			
			SearchEvent.OnQuery -> {
				queryData()
			}
			
			is SearchEvent.OnSearchChange -> {
				_searchUiState.update { uiState ->
					uiState.copy(
						searchInput = event.search,
						errorMessage = null
					)
				}
			}
		}
	}
	
	private fun queryData() = viewModelScope.launch {
		noteRepository.getNoteByKeyword(_searchUiState.value.searchInput).collect { result ->
			result.success { notes ->
				_searchUiState.update { uiState ->
					val queryResult = notes?.map { it.toNote() }?.toMutableStateList()
						?: mutableStateListOf()
					uiState.copy(
						searchResult = queryResult,
						errorMessage = if (queryResult.isEmpty()) "Error" else null
					)
				}
			}.fail {
				_searchUiState.update { uiState ->
					uiState.copy(
						errorMessage = "Error"
					)
				}
			}
		}
	}
	
	private fun removeNote(index: Int) = viewModelScope.launch {
		noteRepository.deleteNote(_searchUiState.value.searchResult[index].noteId)
	}
}

data class SearchUiState(
	override val isLoading: Boolean = false,
	override val errorMessage: String? = null,
	val searchResult: SnapshotStateList<Note> = mutableStateListOf(),
	val searchInput: String = "",
): RootState.ViewUiState

sealed interface SearchOneTimeEvent: RootState.OneTimeEvent<SearchUiState> {
	override fun reduce(uiState: SearchUiState): SearchUiState {
		return uiState
	}
	
	data class NavigateToCategory(val noteId: String? = null): SearchOneTimeEvent
	data class NavigateToNote(val noteId: String): SearchOneTimeEvent
	data class NavigateToTask(val noteId: String): SearchOneTimeEvent
	
}

sealed class SearchEvent: RootState.ViewEvent {
	data class OnNoteClick(
		val noteId: String,
		val noteType: NoteType
	): SearchEvent()
	
	data class AddNoteToCategory(val noteId: String): SearchEvent()
	data class DeleteNote(val index: Int): SearchEvent()
	data class OnSearchChange(val search: String): SearchEvent()
	object OnQuery: SearchEvent()
}