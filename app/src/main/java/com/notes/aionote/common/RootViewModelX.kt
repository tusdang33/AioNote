package com.notes.aionote.common

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

abstract class RootViewModel<S: RootState.ViewUiState, O: RootState.OneTimeEvent<S>, E: RootState.ViewEvent>:
	ViewModel() {
	abstract val coroutineExceptionHandler: CoroutineExceptionHandler
	abstract val uiState: StateFlow<S>
	abstract fun onEvent(event: E)
	abstract fun reduceUiStateFromOneTimeEvent(
		uiState: S,
		oneTimeEvent: O
	)
	
	private val _oneTimeEvent = Channel<O>(Channel.UNLIMITED)
	val oneTimeEvent: Flow<O> = _oneTimeEvent.receiveAsFlow()
	
	fun sendEvent(
		oneTimeEvent: O,
	) = viewModelScope.launch {
		_oneTimeEvent.send(oneTimeEvent)
		reduceUiStateFromOneTimeEvent(
			uiState = oneTimeEvent.reduce(uiState.value),
			oneTimeEvent = oneTimeEvent
		)
	}
}

interface RootState {
	interface ViewUiState: RootState {
		val isLoading: Boolean
		val errorMessage: String?
		
		object None: ViewUiState {
			override val isLoading = false
			override val errorMessage = ""
		}
	}
	
	interface OneTimeEvent<S: ViewUiState>: RootState {
		fun reduce(uiState: S): S
	}
	
	interface ViewEvent: RootState
}