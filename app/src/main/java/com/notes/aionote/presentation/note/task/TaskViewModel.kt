package com.notes.aionote.presentation.note.task

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.notes.aionote.common.AioConst.NOTIFICATION_ID
import com.notes.aionote.common.AioConst.NOTIFICATION_TITLE
import com.notes.aionote.common.AioConst.NOTIFICATION_WORK
import com.notes.aionote.common.AioDispatcher
import com.notes.aionote.common.AioRepoType
import com.notes.aionote.common.Dispatcher
import com.notes.aionote.common.RepoType
import com.notes.aionote.common.RootState
import com.notes.aionote.common.RootViewModel
import com.notes.aionote.common.success
import com.notes.aionote.data.model.CheckNote
import com.notes.aionote.data.model.toNote
import com.notes.aionote.data.model.toNoteContentEntity
import com.notes.aionote.domain.data.NoteEntity
import com.notes.aionote.domain.repository.NoteRepository
import com.notes.aionote.presentation.note.NoteType
import com.notes.aionote.worker.ReminderWork
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.realm.kotlin.ext.toRealmList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.NonCancellable
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
	@RepoType(AioRepoType.LOCAL) private val noteRepository: NoteRepository,
	@Dispatcher(AioDispatcher.IO) private val ioDispatcher: CoroutineDispatcher,
): RootViewModel<TaskUiState, TaskOneTimeEvent, TaskEvent>() {
	private val currentTaskId: String = checkNotNull(savedStateHandle["taskId"])
	
	override val coroutineExceptionHandler: CoroutineExceptionHandler = CoroutineExceptionHandler { _, _ ->
	
	}
	private val _taskUiState = MutableStateFlow(TaskUiState())
	
	override val uiState: StateFlow<TaskUiState> = _taskUiState.asStateFlow()
	
	init {
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
			
			is TaskEvent.AddCheckNote -> {
				_taskUiState.update {
					it.copy(
						listCheckNote = it.listCheckNote.apply {
							add(event.index + 1, CheckNote())
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
		val notificationWork = OneTimeWorkRequest.Builder(ReminderWork::class.java)
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
	val isShowDialog: Boolean = false
): RootState.ViewUiState

sealed interface TaskOneTimeEvent: RootState.OneTimeEvent<TaskUiState> {
}

sealed class TaskEvent: RootState.ViewEvent {
	data class OnContentChange(
		val index: Int,
		val content: String
	): TaskEvent()
	
	data class AddCheckNote(val index: Int): TaskEvent()
	data class DeleteItem(val index: Int): TaskEvent()
	object SaveNote: TaskEvent()
	object DismissDialog: TaskEvent()
	data class OnDateTimeChange(val time: Long): TaskEvent()
	object PickDateTime: TaskEvent()
}