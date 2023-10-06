package com.notes.aionote.presentation.setting

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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
	private val authRepository: AuthRepository,
	@Dispatcher(AioDispatcher.IO) private val ioDispatcher: CoroutineDispatcher
):
	RootViewModel<RootState.ViewUiState, SettingOneTimeEvent, SettingEvent>() {
	override val coroutineExceptionHandler: CoroutineExceptionHandler
		get() = CoroutineExceptionHandler { _, _ ->  }
	override val uiState: StateFlow<RootState.ViewUiState>
		get() = MutableStateFlow(RootState.ViewUiState.None)
	
	override fun failHandle(errorMessage: String?) {
	}
	
	override fun reduceUiStateFromOneTimeEvent(
		uiState: RootState.ViewUiState,
		oneTimeEvent: SettingOneTimeEvent
	) {
		/*noop*/
	}
	
	val currentUser: MutableStateFlow<FirebaseUser?> = MutableStateFlow(null)
	
	override fun onEvent(event: SettingEvent) {
		when(event) {
			is SettingEvent.OnLogout -> {
				logout()
			}
			is SettingEvent.OnGetCurrentUser -> {
				getCurrentUser()
			}
		}
	}
	
	private fun logout() = viewModelScope.launch(ioDispatcher) {
		authRepository.logout().success {
			sendEvent(SettingOneTimeEvent.OnLogout)
			
		}
	}
	private fun getCurrentUser() =viewModelScope.launch{
		authRepository.getCurrentUser<FirebaseUser>().success {
			currentUser.value = it
		}
	}
}

sealed interface SettingOneTimeEvent: RootState.OneTimeEvent<RootState.ViewUiState> {
	override fun reduce(uiState: RootState.ViewUiState): RootState.ViewUiState {
		return RootState.ViewUiState.None
	}
	object OnLogout: SettingOneTimeEvent
}

sealed class SettingEvent: RootState.ViewEvent {
	object OnLogout: SettingEvent()
	object OnGetCurrentUser: SettingEvent()
}