package com.notes.aionote.presentation.finished

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.viewModelScope
import com.notes.aionote.common.AioDispatcher
import com.notes.aionote.common.Dispatcher
import com.notes.aionote.common.RootState
import com.notes.aionote.common.RootViewModel
import com.notes.aionote.common.success
import com.notes.aionote.data.model.Note
import com.notes.aionote.data.model.toNote
import com.notes.aionote.domain.repository.NoteRepository
import com.notes.aionote.presentation.home.HomeEvent
import com.notes.aionote.presentation.home.HomeOneTimeEvent
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
class FinishedViewModel @Inject constructor(
	private val noteRepository: NoteRepository,
	@Dispatcher(AioDispatcher.IO) private val ioDispatcher: CoroutineDispatcher,
	@Dispatcher(AioDispatcher.Main) private val mainDispatcher: CoroutineDispatcher,
): RootViewModel<FinishedUiState, FinishedOneTimeEvent, FinishedViewEvent>() {
	override val coroutineExceptionHandler: CoroutineExceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
	
	}
	
	private val _finishedUiState = MutableStateFlow(FinishedUiState())
	override val uiState: StateFlow<FinishedUiState> = _finishedUiState.asStateFlow()
	
	init {
		fetchTaskData()
	}
	
	private fun fetchTaskData() = viewModelScope.launch(ioDispatcher) {
		noteRepository.getAllTask().collect { resource ->
			resource.success { listNote ->
				withContext(mainDispatcher) {
					_finishedUiState.update { uiState ->
						uiState.copy(
							listTask = listNote?.filter { noteEntity ->
								noteEntity.notes.all { noteContentEntity ->
									noteContentEntity.checked == true
								}
							}?.map { it.toNote() }?.toMutableStateList() ?: mutableStateListOf()
						)
					}
				}
			}
		}
	}
	
	fun failHandle(errorMessage: String? = null) {
		TODO("Not yet implemented")
	}
	
	override fun reduceUiStateFromOneTimeEvent(
		uiState: FinishedUiState,
		oneTimeEvent: FinishedOneTimeEvent
	) {
		_finishedUiState.value = uiState
	}
	
	override fun onEvent(event: FinishedViewEvent) {
		when(event) {
			is FinishedViewEvent.DeleteTask -> {
				removeTask(event.index)
			}
			is FinishedViewEvent.NavigateToEditTask -> {
				sendEvent(FinishedOneTimeEvent.NavigateToTask(event.taskId))
			}
			is FinishedViewEvent.OnTaskCheckedChange -> TODO()
		}
	}
	
	private fun removeTask(index: Int) = viewModelScope.launch{
		noteRepository.deleteNote(_finishedUiState.value.listTask[index].noteId)
	}
}

data class FinishedUiState(
	override val isLoading: Boolean = false,
	override val errorMessage: String? = null,
	val listTask: SnapshotStateList<Note> = mutableStateListOf()
): RootState.ViewUiState

interface FinishedOneTimeEvent: RootState.OneTimeEvent<FinishedUiState> {
	override fun reduce(uiState: FinishedUiState): FinishedUiState {
		return uiState
	}
	
	data class NavigateToTask(val taskId: String) : FinishedOneTimeEvent
}

sealed class FinishedViewEvent: RootState.ViewEvent {
	data class NavigateToEditTask(val taskId: String): FinishedViewEvent()
	data class DeleteTask(val index: Int): FinishedViewEvent()
	data class OnTaskCheckedChange(
		val noteIndex: Int,
		val noteContentIndex: Int,
		val checked: Boolean
	): FinishedViewEvent()
	
}