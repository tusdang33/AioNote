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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
	private val authRepository: AuthRepository,
	@Dispatcher(AioDispatcher.IO) private val ioDispatcher: CoroutineDispatcher
):
	RootViewModel<SettingUiState, SettingOneTimeEvent, SettingEvent>() {
	override val coroutineExceptionHandler: CoroutineExceptionHandler
		get() = CoroutineExceptionHandler { _, _ -> }
	
	private val _settingUiState = MutableStateFlow(SettingUiState())
	override val uiState: StateFlow<SettingUiState> = _settingUiState.asStateFlow()
	
	private fun fetchUserData() = viewModelScope.launch {
		authRepository.getCurrentUser<FirebaseUser>().success { fuser ->
			fuser?.let { user ->
				_settingUiState.update { uiState ->
					uiState.copy(
						userImage = if (user.photoUrl != null) user.photoUrl.toString() else null,
						userName = user.displayName?.ifBlank { null },
						userEmail = user.email ?: ""
					)
				}
			}
			
		}
	}
	
	override fun failHandle(errorMessage: String?) {
	}
	
	override fun reduceUiStateFromOneTimeEvent(
		uiState: SettingUiState,
		oneTimeEvent: SettingOneTimeEvent
	) {
		_settingUiState.value = uiState
	}
	
	
	override fun onEvent(event: SettingEvent) {
		when (event) {
			is SettingEvent.OnLogout -> {
				logout()
			}
			
			SettingEvent.OnChangePassword -> {
				sendEvent(SettingOneTimeEvent.OnChangePassword(_settingUiState.value.userEmail))
			}
			
			SettingEvent.OnEditProfile -> {
				val state = _settingUiState.value
				sendEvent(
					SettingOneTimeEvent.OnEditProfile(
						image = state.userImage,
						userName = state.userName,
						userEmail = state.userEmail
					)
				)
			}
			
			SettingEvent.OnFetchUserData -> {
				fetchUserData()
			}
		}
	}
	
	private fun logout() = viewModelScope.launch(ioDispatcher) {
		authRepository.logout().success {
			sendEvent(SettingOneTimeEvent.OnLogout)
			
		}
	}
}

sealed interface SettingOneTimeEvent: RootState.OneTimeEvent<SettingUiState> {
	override fun reduce(uiState: SettingUiState): SettingUiState {
		return uiState
	}
	
	object OnLogout: SettingOneTimeEvent
	data class OnEditProfile(
		val image: String?,
		val userName: String?,
		val userEmail: String
	): SettingOneTimeEvent
	
	data class OnChangePassword(val email: String): SettingOneTimeEvent
}

data class SettingUiState(
	override val isLoading: Boolean = false,
	override val errorMessage: String? = null,
	val userImage: String? = null,
	val userName: String? = null,
	val userEmail: String = ""
): RootState.ViewUiState

sealed class SettingEvent: RootState.ViewEvent {
	object OnLogout: SettingEvent()
	object OnFetchUserData: SettingEvent()
	object OnEditProfile: SettingEvent()
	object OnChangePassword: SettingEvent()
}