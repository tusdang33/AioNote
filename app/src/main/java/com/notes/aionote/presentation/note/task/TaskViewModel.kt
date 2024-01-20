package com.notes.aionote.presentation.note.task

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.focus.FocusRequester
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.notes.aionote.common.AioConst.NOTIFICATION_ID
import com.notes.aionote.common.AioConst.NOTIFICATION_TITLE
import com.notes.aionote.common.AioConst.NOTIFICATION_WORK
import com.notes.aionote.common.AioDispatcher
import com.notes.aionote.common.Dispatcher
import com.notes.aionote.common.RootState
import com.notes.aionote.common.RootViewModel
import com.notes.aionote.common.success
import com.notes.aionote.data.model.CheckNote
import com.notes.aionote.data.model.toNote
import com.notes.aionote.data.model.toNoteContentEntity
import com.notes.aionote.domain.local_data.NoteEntity
import com.notes.aionote.domain.repository.NoteRepository
import com.notes.aionote.presentation.note.NoteType
import com.notes.aionote.presentation.note.normal_note.NoteEvent
import com.notes.aionote.worker.ReminderWork
import dagger.hilt.android.lifecycle.HiltViewModel
import io.realm.kotlin.ext.toRealmList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.mongodb.kbson.ObjectId
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
	private val savedStateHandle: SavedStateHandle,
	private val instanceWorkManager: WorkManager,
	private val noteRepository: NoteRepository,
	@Dispatcher(AioDispatcher.IO) private val ioDispatcher: CoroutineDispatcher,
	@Dispatcher(AioDispatcher.Main) private val mainDispatcher: CoroutineDispatcher,
): RootViewModel<TaskUiState, TaskOneTimeEvent, TaskEvent>() {
	private val currentTaskId: String = checkNotNull(savedStateHandle["taskId"])
	
	override val coroutineExceptionHandler: CoroutineExceptionHandler = CoroutineExceptionHandler { _, _ ->
	
	}
	private val _taskUiState = MutableStateFlow(TaskUiState())
	
	override val uiState: StateFlow<TaskUiState> = _taskUiState.asStateFlow()
	
	init {
		viewModelScope.launch {
			if (currentTaskId != "null") {
				noteRepository.getNoteById(currentTaskId).success { noteEntity ->
					val note = noteEntity?.toNote()
					if (note != null) {
						_taskUiState.update { uiState ->
							uiState.copy(
								listCheckNote = uiState.listCheckNote.apply {
									clear()
									addAll(note.notes.map { it as CheckNote })
								},
								deadline = note.deadLine,
							)
						}
					}
				}
			}
		}
	}
	
	private fun failHandle(errorMessage: String? = null) {
	}
	
	override fun reduceUiStateFromOneTimeEvent(
		uiState: TaskUiState,
		oneTimeEvent: TaskOneTimeEvent
	) {
	}
	
	override fun onEvent(event: TaskEvent) {
		when (event) {
			is TaskEvent.OnContentChange -> {
				val note = _taskUiState.value.listCheckNote.getOrNull(event.index)
				note?.let {
					_taskUiState.update {
						it.copy(
							listCheckNote = it.listCheckNote.apply {
								set(event.index, note.copy(content = event.content))
							}
						)
					}
				}
			}
			
			is TaskEvent.AddCheckNote -> viewModelScope.launch(mainDispatcher) {
				_taskUiState.update {
					it.copy(
						listCheckNote = it.listCheckNote.apply {
							add(event.index + 1, CheckNote())
						}
					)
				}
				delay(100L)
				try {
					_taskUiState.value.focusRequester.requestFocus()
				} catch (e: Exception) {
					/* ignore focus */
				}
			}
			
			is TaskEvent.OnCheckedChange -> {
				val note = _taskUiState.value.listCheckNote.getOrNull(event.index) ?: return
				_taskUiState.update {
					it.copy(
						listCheckNote = it.listCheckNote.apply {
							set(event.index, note.copy(checked = event.checked))
						}
					)
				}
			}
			
			is TaskEvent.DeleteItem -> {
				if (_taskUiState.value.listCheckNote.size == 1) return
				_taskUiState.update {
					it.copy(
						listCheckNote = it.listCheckNote.apply {
							removeAt(event.index)
						}
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
	
	private fun setReminder(delay: Long = 3000L, title: String = "This is title") {
		val notificationWork = OneTimeWorkRequestBuilder<ReminderWork>()
			.setInitialDelay(delay, TimeUnit.MILLISECONDS)
			.setInputData(
				Data.Builder()
					.putInt(NOTIFICATION_ID, 0)
					.putString(NOTIFICATION_TITLE, title)
					.build()
			)
			.build()
		
		instanceWorkManager.beginUniqueWork(
			NOTIFICATION_WORK,
			ExistingWorkPolicy.REPLACE,
			notificationWork
		).enqueue()
	}
	
	private fun saveNote() = viewModelScope.launch(NonCancellable + ioDispatcher) {
		val prepareNote = _taskUiState.value
		if (prepareNote.listCheckNote.all { it.content.isEmpty() }) return@launch
		val noteEntity = NoteEntity().apply {
			if (currentTaskId != "null") {
				noteId = ObjectId.invoke(hexString = currentTaskId)
			}
			notes = prepareNote.listCheckNote.map { it.toNoteContentEntity() }.toRealmList()
			deadLine = prepareNote.deadline
			noteType = NoteType.TASK.ordinal
		}
		if (currentTaskId != "null") {
			noteRepository.updateNote(noteEntity = noteEntity)
		} else {
			noteRepository.insertNote(noteEntity = noteEntity)
		}
		savedStateHandle["taskId"] = null
		setReminder()
	}
}

data class TaskUiState(
	override val isLoading: Boolean = false,
	override val errorMessage: String? = null,
	val listCheckNote: SnapshotStateList<CheckNote> = mutableStateListOf(CheckNote()),
	val deadline: Long? = null,
	val isShowDialog: Boolean = false,
	val focusRequester: FocusRequester = FocusRequester()
): RootState.ViewUiState

sealed interface TaskOneTimeEvent: RootState.OneTimeEvent<TaskUiState> {
}

sealed class TaskEvent: RootState.ViewEvent {
	data class OnContentChange(
		val index: Int,
		val content: String
	): TaskEvent()
	
	data class AddCheckNote(val index: Int): TaskEvent()
	data class OnCheckedChange(
		val index: Int,
		val checked: Boolean
	): TaskEvent()
	data class DeleteItem(val index: Int): TaskEvent()
	object SaveNote: TaskEvent()
	object DismissDialog: TaskEvent()
	data class OnDateTimeChange(val time: Long): TaskEvent()
	object PickDateTime: TaskEvent()
}