package com.notes.aionote.presentation.category

import com.notes.aionote.common.RootState
import com.notes.aionote.common.RootViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(

) : RootViewModel<CategoryUiState, CategoryOneTimeEvent, CategoryEvent>() {
	override val coroutineExceptionHandler: CoroutineExceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
	
	}
	private val _categoryUiState = MutableStateFlow(CategoryUiState())
	override val uiState: StateFlow<CategoryUiState> = _categoryUiState.asStateFlow()
	
	override fun failHandle(errorMessage: String?) {
	}
	
	override fun reduceUiStateFromOneTimeEvent(
		uiState: CategoryUiState,
		oneTimeEvent: CategoryOneTimeEvent
	) {
		_categoryUiState.value = uiState
	}
	
	override fun onEvent(event: CategoryEvent) {
		TODO("Not yet implemented")
	}
}

data class CategoryUiState(
	override val isLoading: Boolean = false,
	override val errorMessage: String? = null
): RootState.ViewUiState

interface CategoryOneTimeEvent: RootState.OneTimeEvent<CategoryUiState>
sealed class CategoryEvent: RootState.ViewEvent