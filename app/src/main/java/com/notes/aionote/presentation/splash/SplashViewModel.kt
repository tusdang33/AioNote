package com.notes.aionote.presentation.splash

import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.notes.aionote.common.AioDispatcher
import com.notes.aionote.common.Dispatcher
import com.notes.aionote.common.RootState
import com.notes.aionote.common.RootViewModel
import com.notes.aionote.common.success
import com.notes.aionote.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
	private val authRepository: AuthRepository,
	@Dispatcher(AioDispatcher.IO) private val ioDispatcher: CoroutineDispatcher
): RootViewModel<RootState.ViewUiState, SplashOneTimeEvent, SplashEvent>() {
	override val coroutineExceptionHandler: CoroutineExceptionHandler
		get() = CoroutineExceptionHandler { _, _ ->
			sendEvent(SplashOneTimeEvent.LoginFail)
		}
	override val uiState: StateFlow<RootState.ViewUiState> = MutableStateFlow(RootState.ViewUiState.None)
	override fun failHandle(errorMessage: String?) {
		sendEvent(SplashOneTimeEvent.LoginFail)
	}
	
	override fun reduceUiStateFromOneTimeEvent(
		uiState: RootState.ViewUiState,
		oneTimeEvent: SplashOneTimeEvent
	) {
		/*noop*/
	}
	
	override fun onEvent(event: SplashEvent) {
		when (event) {
			SplashEvent.CheckCurrentUser -> {
				checkCurrentUser()
			}
		}
	}
	
	private fun checkCurrentUser() =
		viewModelScope.launch(ioDispatcher + coroutineExceptionHandler) {
			delay(3000L)
			authRepository.getCurrentUser<FirebaseUser>()
				.success {
					if (it != null) {
						sendEvent(SplashOneTimeEvent.LoginSuccess)
					} else {
						failHandle()
					}
				}
		}
}

sealed interface SplashOneTimeEvent: RootState.OneTimeEvent<RootState.ViewUiState> {
	
	override fun reduce(uiState: RootState.ViewUiState): RootState.ViewUiState =
		RootState.ViewUiState.None
	
	object LoginSuccess: SplashOneTimeEvent
	object LoginFail: SplashOneTimeEvent
}

sealed class SplashEvent: RootState.ViewEvent {
	object CheckCurrentUser: SplashEvent()
}