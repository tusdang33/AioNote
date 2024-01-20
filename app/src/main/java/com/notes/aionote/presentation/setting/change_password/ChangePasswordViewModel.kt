package com.notes.aionote.presentation.setting.change_password

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.notes.aionote.common.RootState
import com.notes.aionote.common.RootViewModel
import com.notes.aionote.common.fail
import com.notes.aionote.common.success
import com.notes.aionote.domain.repository.AuthRepository
import com.notes.aionote.domain.use_case.authentication.ValidatePasswordUseCase
import com.notes.aionote.domain.use_case.authentication.ValidateRetypePasswordUseCase
import com.notes.aionote.presentation.authentication.sign_up.SignUpEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
	private val savedStateHandle: SavedStateHandle,
	private val authRepository: AuthRepository,
	private val validatePasswordUseCase: ValidatePasswordUseCase,
	private val validateRetypePasswordUseCase: ValidateRetypePasswordUseCase
): RootViewModel<ChangePasswordUiState, ChangePasswordOneTimeEvent, ChangePasswordEvent>() {
	private val userEmail: String? = savedStateHandle["email"]
	
	override val coroutineExceptionHandler: CoroutineExceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
	
	}
	
	private val _changePasswordUiState = MutableStateFlow(ChangePasswordUiState())
	override val uiState: StateFlow<ChangePasswordUiState> = _changePasswordUiState.asStateFlow()
	
	private fun failHandle(errorMessage: String? = null) {
	}
	
	override fun reduceUiStateFromOneTimeEvent(
		uiState: ChangePasswordUiState,
		oneTimeEvent: ChangePasswordOneTimeEvent
	) {
		_changePasswordUiState.value = uiState
	}
	
	override fun onEvent(event: ChangePasswordEvent) {
		when (event) {
			is ChangePasswordEvent.OnCurrentPassChange -> {
				_changePasswordUiState.update {
					it.copy(
						currentPass = event.pass
					)
				}
			}
			
			is ChangePasswordEvent.OnNewPassChange -> {
				_changePasswordUiState.update {
					it.copy(
						newPass = event.pass
					)
				}
			}
			
			is ChangePasswordEvent.OnRetypePassChange -> {
				_changePasswordUiState.update {
					it.copy(
						retypePass = event.pass
					)
				}
			}
			
			ChangePasswordEvent.OnSubmit -> {
				submitPassword()
			}
			
			ChangePasswordEvent.OnDismissDialog -> {
				_changePasswordUiState.update {
					it.copy(
						isSubmitSuccess = null,
					)
				}
			}
			
			is ChangePasswordEvent.OnPasswordVisibleChange -> {
				_changePasswordUiState.update {
					it.copy(isPasswordVisible = event.visible)
				}
			}
		}
	}
	
	private fun submitPassword() = viewModelScope.launch(NonCancellable) {
		val state = _changePasswordUiState.value
		val passwordValidateResult = validatePasswordUseCase(state.newPass)
		val retypePasswordValidateResult = validateRetypePasswordUseCase(
			state.newPass,
			state.retypePass
		)
		
		val hasError = listOf(
			passwordValidateResult,
			retypePasswordValidateResult
		).any { !it.successful }
		
		if (hasError) {
			_changePasswordUiState.update {
				it.copy(
					newPassErrorMessage = passwordValidateResult.errorMessage,
					retypePassErrorMessage = retypePasswordValidateResult.errorMessage
				)
			}
			failHandle()
			return@launch
		}
		authRepository.login(userEmail ?: "", state.currentPass).fail {
			_changePasswordUiState.update {
				it.copy(
					currentPassErrorMessage = "Wrong current password",
				)
			}
		}.success {
			updatePass()
		}
	}
	
	private fun updatePass() = viewModelScope.launch(NonCancellable) {
		val state = _changePasswordUiState.value
		authRepository.updatePass(state.newPass).success {
			_changePasswordUiState.update {
				it.copy(
					isSubmitSuccess = true,
				)
			}
		}.fail {
			_changePasswordUiState.update {
				it.copy(
					isSubmitSuccess = false,
				)
			}
		}
	}
}

data class ChangePasswordUiState(
	override val isLoading: Boolean = false,
	override val errorMessage: String? = null,
	val currentPass: String = "",
	val currentPassErrorMessage: String? = null,
	val isPasswordVisible: Boolean = false,
	val newPass: String = "",
	val newPassErrorMessage: String? = null,
	val retypePass: String = "",
	val retypePassErrorMessage: String? = null,
	val isSubmitSuccess: Boolean? = null
): RootState.ViewUiState

sealed interface ChangePasswordOneTimeEvent: RootState.OneTimeEvent<ChangePasswordUiState>

sealed class ChangePasswordEvent: RootState.ViewEvent {
	data class OnCurrentPassChange(val pass: String): ChangePasswordEvent()
	data class OnNewPassChange(val pass: String): ChangePasswordEvent()
	data class OnRetypePassChange(val pass: String): ChangePasswordEvent()
	object OnSubmit: ChangePasswordEvent()
	data class OnPasswordVisibleChange(val visible: Boolean): ChangePasswordEvent()
	object OnDismissDialog: ChangePasswordEvent()
}