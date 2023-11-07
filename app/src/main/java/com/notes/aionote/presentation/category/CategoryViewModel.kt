package com.notes.aionote.presentation.category

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
import com.notes.aionote.common.success
import com.notes.aionote.data.model.Category
import com.notes.aionote.data.model.toCategory
import com.notes.aionote.domain.local_data.CategoryEntity
import com.notes.aionote.domain.repository.AudioRecorder
import com.notes.aionote.domain.repository.CategoryRepository
import com.notes.aionote.domain.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
	private val savedStateHandle: SavedStateHandle,
	@RepoType(AioRepoType.LOCAL) private val localNoteRepository: NoteRepository,
	@RepoType(AioRepoType.LOCAL) private val localCategoryRepository: CategoryRepository,
	@Dispatcher(AioDispatcher.IO) private val ioDispatcher: CoroutineDispatcher,
	@Dispatcher(AioDispatcher.Main) private val mainDispatcher: CoroutineDispatcher,
	private val audioRecorder: AudioRecorder,
) : RootViewModel<CategoryUiState, CategoryOneTimeEvent, CategoryEvent>() {
	private val creatingCategoryNoteId: String? = savedStateHandle["noteId"]
	
	override val coroutineExceptionHandler: CoroutineExceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
	
	}
	private val _categoryUiState = MutableStateFlow(CategoryUiState())
	override val uiState: StateFlow<CategoryUiState> = _categoryUiState.asStateFlow()
	
	init {
		fetchAllCategory()
	}
	
	private fun fetchAllCategory() = viewModelScope.launch {
		localCategoryRepository.getAllCategory().collect { resourceCategory ->
			resourceCategory.success { listCategory ->
				_categoryUiState.update { uiState ->
					uiState.copy(
						categoryList = buildList {
							if(creatingCategoryNoteId.isNullOrBlank()) {
								addAll(
									DefaultCategory.values()
										.map { Category(category = it.category) })
							}
							addAll(
								listCategory?.map { it.toCategory() }
									?.filter { it.category != null }
									?.toMutableStateList()
									?: mutableStateListOf()
							)
						}.toMutableStateList()
					)
				}
			}
		}
	}
	
	override fun failHandle(errorMessage: String?) {
		sendEvent(CategoryOneTimeEvent.Fail(errorMessage = errorMessage))
	}
	
	override fun reduceUiStateFromOneTimeEvent(
		uiState: CategoryUiState,
		oneTimeEvent: CategoryOneTimeEvent
	) {
		_categoryUiState.value = uiState
	}
	
	override fun onEvent(event: CategoryEvent) {
		when (event) {
			is CategoryEvent.OnCategoryClick -> {
				handleCategoryClick(event.category)
			}
			
			CategoryEvent.OnSubmitCategory -> {
				_categoryUiState.update {
					it.copy(
						isCreatingCategory = false
					)
				}
				createCategory()
			}
			
			is CategoryEvent.OnNewCategoryChange -> {
				_categoryUiState.update {
					it.copy(
						newCategory = event.category
					)
				}
			}
			
			CategoryEvent.OnCreateCategoryClick -> {
				_categoryUiState.update {
					it.copy(
						isCreatingCategory = true
					)
				}
			}
			
			CategoryEvent.OnCloseCreateCategory -> {
				_categoryUiState.update {
					it.copy(
						isCreatingCategory = false
					)
				}
			}
			
			is CategoryEvent.OnDeleteCategory -> {
				deleteCategory(event.categoryId)
			}
		}
	}
	
	private fun handleCategoryClick(category: Category) = viewModelScope.launch (NonCancellable) {
		if (!creatingCategoryNoteId.isNullOrBlank()) {
			localNoteRepository.updateNoteCategory(
				categoryId = category.categoryId,
				noteId = creatingCategoryNoteId
			)
		}
		sendEvent(CategoryOneTimeEvent.OnFilterNote(category = category))
	}
	
	private fun deleteCategory(categoryId: String) = viewModelScope.launch {
		localCategoryRepository.deleteCategory(categoryId)
	}
	
	private fun createCategory() = viewModelScope.launch(NonCancellable + ioDispatcher) {
		if (_categoryUiState.value.isLoading) return@launch
		sendEvent(CategoryOneTimeEvent.Loading)
		val prepareCategory = _categoryUiState.value
		val addingCategory = CategoryEntity().apply { category = prepareCategory.newCategory }
		localCategoryRepository.addCategory(addingCategory).success { sendEvent(CategoryOneTimeEvent.Success) }
		if (!creatingCategoryNoteId.isNullOrBlank()) {
			localNoteRepository.updateNoteCategory(
				categoryId = addingCategory.categoryId.toHexString(),
				noteId = creatingCategoryNoteId
			)
		}
		savedStateHandle["noteId"] = null
	}
}

data class CategoryUiState(
	override val isLoading: Boolean = false,
	override val errorMessage: String? = null,
	val categoryList: SnapshotStateList<Category> = mutableStateListOf(),
	val currentCategory: Category? = null,
	val newCategory: String = "",
	val isCreatingCategory: Boolean = false
): RootState.ViewUiState

interface CategoryOneTimeEvent: RootState.OneTimeEvent<CategoryUiState> {
	override fun reduce(uiState: CategoryUiState): CategoryUiState {
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
	
	data class OnFilterNote(val category: Category): CategoryOneTimeEvent
	object Loading: CategoryOneTimeEvent
	object Success: CategoryOneTimeEvent
	data class Fail(val errorMessage: String? = null): CategoryOneTimeEvent
}

sealed class CategoryEvent: RootState.ViewEvent {
	data class OnCategoryClick(val category: Category): CategoryEvent()
	data class OnNewCategoryChange(val category: String): CategoryEvent()
	object OnSubmitCategory: CategoryEvent()
	data class OnDeleteCategory(val categoryId: String): CategoryEvent()
	object OnCloseCreateCategory: CategoryEvent()
	object OnCreateCategoryClick: CategoryEvent()
}