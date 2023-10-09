package com.notes.aionote.presentation.note.task

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.notes.aionote.common.AioDispatcher
import com.notes.aionote.common.AioNoteRepoType
import com.notes.aionote.common.Dispatcher
import com.notes.aionote.common.NoteRepoType
import com.notes.aionote.common.RootState
import com.notes.aionote.common.RootViewModel
import com.notes.aionote.data.model.CheckNote
import com.notes.aionote.data.model.toNote
import com.notes.aionote.data.model.toNoteContentEntity
import com.notes.aionote.domain.data.NoteEntity
import com.notes.aionote.domain.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.realm.kotlin.ext.realmListOf
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.mongodb.kbson.ObjectId
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
	private val savedStateHandle: SavedStateHandle,
	@NoteRepoType(AioNoteRepoType.LOCAL) private val noteRepository: NoteRepository,
	@Dispatcher(AioDispatcher.IO) private val ioDispatcher: CoroutineDispatcher,
): RootViewModel<TaskUiState, TaskOneTimeEvent, TaskEvent>() {
	private val currentTaskId: String = checkNotNull(savedStateHandle["taskId"])
	
	override val coroutineExceptionHandler: CoroutineExceptionHandler = CoroutineExceptionHandler { _, _ ->
	
	}
	private val _taskUiState = MutableStateFlow(TaskUiState())
	
	override val uiState: StateFlow<TaskUiState> = _taskUiState.asStateFlow()
	
	init {
		if (currentTaskId != "null") {
			val note = noteRepository.getNoteById(currentTaskId)?.toNote()
			if (note != null) {
				_taskUiState.update { uiState ->
					uiState.copy(
						checked = (note.notes.firstOrNull() as CheckNote).checked,
						content = (note.notes.firstOrNull() as CheckNote).content,
						deadline = note.deadLine,
					)
				}
			}
		}
	}
	
	override fun failHandle(errorMessage: String?) {
	}
	
	override fun reduceUiStateFromOneTimeEvent(
		uiState: TaskUiState,
		oneTimeEvent: TaskOneTimeEvent
	) {
	}
	
	override fun onEvent(event: TaskEvent) {
		when (event) {
			is TaskEvent.OnContentChange -> {
				_taskUiState.update {
					it.copy(
						content = event.content
					)
				}
			}
			
			TaskEvent.SaveNote -> {
				saveNote()
			}
			
			TaskEvent.PickDateTime -> {
				_taskUiState.update {
					it.copy(
						isShowDialog = true
					)
				}
			}
			
			is TaskEvent.OnDateTimeChange -> {
				_taskUiState.update {
					it.copy(
						deadline = if (event.time < System.currentTimeMillis()) System.currentTimeMillis() else event.time
					)
				}
			}
			
			TaskEvent.DismissDialog -> {
				_taskUiState.update {
					it.copy(
						isShowDialog = false
					)
				}
			}
		}
	}
	
	private fun saveNote() = viewModelScope.launch(NonCancellable + ioDispatcher) {
		val prepareNote = _taskUiState.value
		if (prepareNote.content.isBlank()) return@launch
		val noteEntity = NoteEntity().apply {
			if (currentTaskId != "null") {
				noteId = ObjectId.invoke(hexString = currentTaskId)
			}
			notes = realmListOf(
				CheckNote(
					checked = false,
					content = prepareNote.content
				).toNoteContentEntity()
			)
			deadLine = prepareNote.deadline
			noteType = 2
		}
		if (currentTaskId != "null") {
			noteRepository.updateNote(noteEntity = noteEntity)
		} else {
			noteRepository.insertNote(noteEntity = noteEntity)
		}
		savedStateHandle["taskId"] = null
	}
}

data class TaskUiState(
	override val isLoading: Boolean = false,
	override val errorMessage: String? = null,
	val content: String = "",
	val checked: Boolean = false,
	val deadline: Long? = null,
	val isShowDialog: Boolean = false
): RootState.ViewUiState

sealed interface TaskOneTimeEvent: RootState.OneTimeEvent<TaskUiState> {
}

sealed class TaskEvent: RootState.ViewEvent {
	data class OnContentChange(val content: String): TaskEvent()
	object SaveNote: TaskEvent()
	object DismissDialog: TaskEvent()
	data class OnDateTimeChange(val time: Long): TaskEvent()
	object PickDateTime: TaskEvent()
}