package com.notes.aionote.presentation.home

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.viewModelScope
import com.notes.aionote.common.AioDispatcher
import com.notes.aionote.common.AioNoteRepoType
import com.notes.aionote.common.Dispatcher
import com.notes.aionote.common.NoteRepoType
import com.notes.aionote.common.RootState
import com.notes.aionote.common.RootViewModel
import com.notes.aionote.data.model.Note
import com.notes.aionote.data.model.toNote
import com.notes.aionote.domain.repository.AudioRecorder
import com.notes.aionote.domain.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
	@NoteRepoType(AioNoteRepoType.LOCAL) private val localNoteRepository: NoteRepository,
	@Dispatcher(AioDispatcher.IO) private val ioDispatcher: CoroutineDispatcher,
	@Dispatcher(AioDispatcher.Main) private val mainDispatcher: CoroutineDispatcher,
	private val audioRecorder: AudioRecorder,
): RootViewModel<HomeUiState, HomeOneTimeEvent, HomeEvent>() {
	override val coroutineExceptionHandler: CoroutineExceptionHandler = CoroutineExceptionHandler { _, _ -> }
	
	private val _homeUiState = MutableStateFlow(HomeUiState())
	override val uiState: StateFlow<HomeUiState> = _homeUiState.asStateFlow()
	
	init {
		fetchHomeData()
	}
	
	private fun fetchHomeData() = viewModelScope.launch(ioDispatcher) {
		localNoteRepository.getAllNote().collect { notes ->
			withContext(mainDispatcher) {
				_homeUiState.update { uiState ->
					uiState.copy(
						listNote = notes.map { it.toNote(audioRecorder) }.toMutableStateList()
					)
				}
			}
		}
	}
	
	override fun failHandle(errorMessage: String?) {
		sendEvent(HomeOneTimeEvent.Fail())
	}
	
	override fun reduceUiStateFromOneTimeEvent(
		uiState: HomeUiState,
		oneTimeEvent: HomeOneTimeEvent
	) {
		_homeUiState.value = uiState
	}
	
	override fun onEvent(event: HomeEvent) {
		when (event) {
			is HomeEvent.NavigateToEditNote -> {
				sendEvent(HomeOneTimeEvent.NavigateToNote(event.noteId))
			}
			
			is HomeEvent.DeleteItem -> {
				removeNote(event.index)
			}
		}
	}
	
	private fun removeNote(index: Int) = viewModelScope.launch {
		localNoteRepository.deleteNote(_homeUiState.value.listNote[index].noteId)
	}
}

data class HomeUiState(
	override val isLoading: Boolean = false,
	override val errorMessage: String? = null,
	val listNote: SnapshotStateList<Note> = mutableStateListOf()
): RootState.ViewUiState

sealed interface HomeOneTimeEvent: RootState.OneTimeEvent<HomeUiState> {
	override fun reduce(uiState: HomeUiState): HomeUiState {
		return when (this) {
			is Fail -> uiState.copy(
				isLoading = false,
				errorMessage = this.errorMessage
			)
			
			is Loading -> uiState.copy(
				isLoading = true,
				errorMessage = ""
			)
			
			is Success -> uiState.copy(
				isLoading = false,
				errorMessage = ""
			)
			
			else -> uiState
		}
	}
	
	object Loading: HomeOneTimeEvent
	object Success: HomeOneTimeEvent
	data class NavigateToNote(val noteId: String): HomeOneTimeEvent
	data class Fail(val errorMessage: String? = null): HomeOneTimeEvent
}

sealed class HomeEvent: RootState.ViewEvent {
	data class NavigateToEditNote(val noteId: String): HomeEvent()
	data class DeleteItem(val index: Int): HomeEvent()
}