package com.notes.aionote.presentation.home

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.notes.aionote.common.AioDispatcher
import com.notes.aionote.common.AioRepoType
import com.notes.aionote.common.DefaultCategory
import com.notes.aionote.common.Dispatcher
import com.notes.aionote.common.RepoType
import com.notes.aionote.common.RootState
import com.notes.aionote.common.RootViewModel
import com.notes.aionote.common.fail
import com.notes.aionote.common.success
import com.notes.aionote.data.model.Category
import com.notes.aionote.data.model.CheckNote
import com.notes.aionote.data.model.MediaNote
import com.notes.aionote.data.model.MediaType
import com.notes.aionote.data.model.Note
import com.notes.aionote.data.model.toCategory
import com.notes.aionote.data.model.toNote
import com.notes.aionote.data.model.toNoteContentEntity
import com.notes.aionote.domain.local_data.NoteEntity
import com.notes.aionote.domain.repository.AudioRecorder
import com.notes.aionote.domain.repository.CategoryRepository
import com.notes.aionote.domain.repository.NoteRepository
import com.notes.aionote.presentation.note.NoteType
import dagger.hilt.android.lifecycle.HiltViewModel
import io.realm.kotlin.ext.toRealmList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.mongodb.kbson.ObjectId
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
	private val savedStateHandle: SavedStateHandle,
	private val localNoteRepository: NoteRepository,
	private val localCategoryRepository: CategoryRepository,
	@Dispatcher(AioDispatcher.IO) private val ioDispatcher: CoroutineDispatcher,
	@Dispatcher(AioDispatcher.Main) private val mainDispatcher: CoroutineDispatcher,
	private val audioRecorder: AudioRecorder,
): RootViewModel<HomeUiState, HomeOneTimeEvent, HomeEvent>() {
	private val filter: String? = savedStateHandle["filterId"]
	
	override val coroutineExceptionHandler: CoroutineExceptionHandler = CoroutineExceptionHandler { _, _ -> }
	
	private val _homeUiState = MutableStateFlow(HomeUiState())
	override val uiState: StateFlow<HomeUiState> = _homeUiState.asStateFlow()
	
	init {
		fetchNoteData() {
			if(!filter.isNullOrBlank()) {
				filterNote(filter)
			}
		}
		fetchCategoryData() {
			if(!filter.isNullOrBlank()) {
				val filterIndex = _homeUiState.value.listFilter.map { it.category }.indexOf(filter)
				sendEvent(HomeOneTimeEvent.ScrollToFilter(filterIndex = filterIndex))
				
			}
		}
	}
	
	private fun fetchCategoryData(onFetchSuccess: () -> Unit) = viewModelScope.launch {
		localCategoryRepository.getAllCategory().collect { resourceCategory ->
			resourceCategory.success { listCategory ->
				_homeUiState.update { uiState ->
					uiState.copy(
						listFilter = buildList {
							addAll(
								DefaultCategory.values()
									.map { Category(category = it.category) })
							addAll(
								listCategory?.map { it.toCategory() }
									?.filter { it.category != null }
									?.toMutableStateList()
									?: mutableStateListOf()
							)
						}.toMutableStateList()
					)
				}
				onFetchSuccess.invoke()
			}
		}
	}
	
	private fun fetchNoteData(onFetchSuccess: () -> Unit) = viewModelScope.launch(ioDispatcher) {
		localNoteRepository.getAllNote().collect { note ->
			note.success { listNoteEntity ->
				withContext(mainDispatcher) {
					_homeUiState.update { uiState ->
						uiState.copy(
							listTask = listNoteEntity?.filter { it.noteType == NoteType.TASK.ordinal }
								?.map { it.toNote(audioRecorder) }
								?.toMutableStateList() ?: mutableStateListOf(),
							listNote = listNoteEntity?.filter { it.noteType == NoteType.NORMAL.ordinal }
								?.map { it.toNote(audioRecorder) }
								?.toMutableStateList() ?: mutableStateListOf(),
						)
					}
					onFetchSuccess.invoke()
				}
			}.fail {
				failHandle(it)
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
			
			is HomeEvent.DeleteNote -> {
				removeNote(event.index)
			}
			
			is HomeEvent.DeleteTask -> {
				removeTask(event.index)
			}
			
			is HomeEvent.ChangePage -> {
				sendEvent(HomeOneTimeEvent.ChangeCurrentPage(event.index))
			}
			
			is HomeEvent.NavigateToEditTask -> {
				sendEvent(HomeOneTimeEvent.NavigateToTask(event.taskId))
			}
			
			is HomeEvent.AddNoteToCategory -> {
				sendEvent(HomeOneTimeEvent.NavigateToCategory(event.noteId))
			}
			
			is HomeEvent.NavigateToCategory -> {
				sendEvent(HomeOneTimeEvent.NavigateToCategory(null))
			}
			
			is HomeEvent.OnFilter -> {
				filterNote(event.filter)
			}
			
			is HomeEvent.OnTaskCheckedChange -> {
				updateCheckedState(
					noteIndex = event.noteIndex,
					noteContentIndex = event.noteContentIndex,
					checked = event.checked
				)
			}
		}
	}
	
	private fun updateCheckedState(
		noteIndex: Int,
		noteContentIndex: Int,
		checked: Boolean
	) = viewModelScope.launch {
		val prepareTask = _homeUiState.value.listTask.getOrNull(noteIndex)
		prepareTask?.let { note ->
			val noteEntity = NoteEntity().apply {
				noteId = ObjectId.invoke(hexString = note.noteId)
				notes = note.notes.toMutableList().apply {
					val checkNote = getOrNull(noteContentIndex)
					if (checkNote is CheckNote) {
						set(noteContentIndex, checkNote.copy(checked = checked))
					}
				}.map { it.toNoteContentEntity() }.toRealmList()
				deadLine = prepareTask.deadLine
				noteType = NoteType.TASK.ordinal
			}
			localNoteRepository.updateNote(noteEntity = noteEntity)
		}
	}
	
	private fun filterNote(filter: String) {
		_homeUiState.update { uiState ->
			uiState.copy(
				currentFilter = filter
			)
		}
		when (filter) {
			DefaultCategory.IMAGE.category -> {
				defaultFilter(MediaType.IMAGE)
			}
			
			DefaultCategory.VIDEO.category -> {
				defaultFilter(MediaType.VIDEO)
			}
			
			DefaultCategory.VOICE.category -> {
				defaultFilter(MediaType.VOICE)
			}
			
			DefaultCategory.ATTACHMENT.category -> {
				defaultFilter(MediaType.ATTACHMENT)
			}
			
			else -> {
				_homeUiState.update { uiState ->
					uiState.copy(
						listNoteFiltered = uiState.listNote.filter { note -> note.category?.category == filter }
							.toMutableStateList(),
					)
				}
			}
		}
	}
	
	private fun defaultFilter(mediaType: MediaType) {
		_homeUiState.update { uiState ->
			uiState.copy(
				listNoteFiltered = uiState.listNote.filter { note -> note.notes.any { (it as? MediaNote)?.mediaType == mediaType } }
					.toMutableStateList(),
			)
		}
	}
	
	private fun removeNote(index: Int) = viewModelScope.launch {
		localNoteRepository.deleteNote(_homeUiState.value.listNote[index].noteId)
	}
	
	private fun removeTask(index: Int) = viewModelScope.launch {
		localNoteRepository.deleteNote(_homeUiState.value.listTask[index].noteId)
	}
}

data class HomeUiState(
	override val isLoading: Boolean = false,
	override val errorMessage: String? = null,
	val listNote: SnapshotStateList<Note> = mutableStateListOf(),
	val listNoteFiltered: SnapshotStateList<Note> = mutableStateListOf(),
	val listTask: SnapshotStateList<Note> = mutableStateListOf(),
	val listFilter: SnapshotStateList<Category> = mutableStateListOf(),
	val currentFilter: String = DefaultCategory.ALL.category
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
	data class NavigateToCategory(val noteId: String? = null): HomeOneTimeEvent
	data class NavigateToNote(val noteId: String): HomeOneTimeEvent
	data class NavigateToTask(val noteId: String): HomeOneTimeEvent
	data class ChangeCurrentPage(val page: Int): HomeOneTimeEvent
	data class ScrollToFilter(val filterIndex: Int): HomeOneTimeEvent
	data class Fail(val errorMessage: String? = null): HomeOneTimeEvent
}

sealed class HomeEvent: RootState.ViewEvent {
	data class NavigateToEditNote(val noteId: String): HomeEvent()
	data class NavigateToEditTask(val taskId: String): HomeEvent()
	object NavigateToCategory: HomeEvent()
	data class OnFilter(val filter: String): HomeEvent()
	data class DeleteNote(val index: Int): HomeEvent()
	data class AddNoteToCategory(val noteId: String): HomeEvent()
	data class OnTaskCheckedChange(
		val noteIndex: Int,
		val noteContentIndex: Int,
		val checked: Boolean
	): HomeEvent()
	
	data class DeleteTask(val index: Int): HomeEvent()
	data class ChangePage(val index: Int): HomeEvent()
}