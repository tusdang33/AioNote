package com.notes.aionote.presentation.authentication.sign_up

import android.content.Intent
import android.content.IntentSender
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.notes.aionote.common.AioDispatcher
import com.notes.aionote.common.Dispatcher
import com.notes.aionote.common.RootState
import com.notes.aionote.common.RootViewModel
import com.notes.aionote.common.fail
import com.notes.aionote.common.success
import com.notes.aionote.domain.repository.AuthRepository
import com.notes.aionote.domain.use_case.authentication.ValidateEmailUseCase
import com.notes.aionote.domain.use_case.authentication.ValidatePasswordUseCase
import com.notes.aionote.domain.use_case.authentication.ValidateRetypePasswordUseCase
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
class SignUpViewModel @Inject constructor(
	private val authRepository: AuthRepository,
	private val validateEmailUseCase: ValidateEmailUseCase,
	private val validatePasswordUseCase: ValidatePasswordUseCase,
	private val validateRetypePasswordUseCase: ValidateRetypePasswordUseCase,
	@Dispatcher(AioDispatcher.IO) private val ioDispatcher: CoroutineDispatcher
): RootViewModel<SignUpUiState, SignUpOneTimeEvent, SignUpEvent>() {
	
	override val coroutineExceptionHandler: CoroutineExceptionHandler
		get() = CoroutineExceptionHandler { _, throwable ->
			sendEvent(SignUpOneTimeEvent.Fail(throwable.message))
		}
	private val _signUpUiState = MutableStateFlow(SignUpUiState())
	override val uiState: StateFlow<SignUpUiState> = _signUpUiState.asStateFlow()
	
	override fun reduceUiStateFromOneTimeEvent(
		uiState: SignUpUiState,
		oneTimeEvent: SignUpOneTimeEvent
	) {
		_signUpUiState.value = uiState
	}
	
	override fun failHandle(errorMessage: String?) {
		Log.e("tudm", "failHandle $errorMessage ")
		sendEvent(SignUpOneTimeEvent.Fail(errorMessage))
	}
	
	override fun onEvent(event: SignUpEvent) {
		when (event) {
			is SignUpEvent.OnEmailChange -> {
				_signUpUiState.update {
					it.copy(email = event.email, emailError = null)
				}
			}
			
			is SignUpEvent.OnFullNameChange -> {
				_signUpUiState.update {
					it.copy(fullName = event.fullName)
				}
			}
			
			is SignUpEvent.OnPasswordChange -> {
				_signUpUiState.update {
					it.copy(
						password = event.password,
						passwordError = null
					)
				}
			}
			
			is SignUpEvent.OnRetypePasswordChange -> {
				_signUpUiState.update {
					it.copy(
						retypePassword = event.password,
						retypePasswordError = null
					)
				}
			}
			
			is SignUpEvent.OnPasswordVisibleChange -> {
				_signUpUiState.update {
					it.copy(isPasswordVisible = event.visible)
				}
			}
			
			is SignUpEvent.OnSignUpByDefault -> signUp()
			
			is SignUpEvent.OnGetGoogleIntent -> getGoogleIntent()
			
			is SignUpEvent.OnLoginByGoogle -> loginByGoogle(event.intent)
		}
	}
	
	private fun getGoogleIntent() = viewModelScope.launch(ioDispatcher) {
		sendEvent(SignUpOneTimeEvent.Loading)
		authRepository.oneTapSignIn().success { intentSender ->
			intentSender?.let {
				sendEvent(SignUpOneTimeEvent.GetLoginIntent(it))
			}
		}.fail {
			failHandle(it)
		}
	}
	
	private fun loginByGoogle(intent: Intent) = viewModelScope.launch(ioDispatcher) {
		sendEvent(SignUpOneTimeEvent.Loading)
		authRepository.signInWithGoogle(intent).success {
			sendEvent(SignUpOneTimeEvent.Success)
		}.fail {
			failHandle(it)
		}
	}
	
	private fun signUp() = viewModelScope.launch(ioDispatcher + coroutineExceptionHandler) {
		sendEvent(SignUpOneTimeEvent.Loading)
		val emailValidateResult = validateEmailUseCase(_signUpUiState.value.email)
		val passwordValidateResult = validatePasswordUseCase(_signUpUiState.value.password)
		val retypePasswordValidateResult = validateRetypePasswordUseCase(
			_signUpUiState.value.password,
			_signUpUiState.value.retypePassword
		)
		
		val hasError = listOf(
			emailValidateResult,
			passwordValidateResult,
			retypePasswordValidateResult
		).any { !it.successful }
		
		if (hasError) {
			_signUpUiState.update {
				it.copy(
					emailError = emailValidateResult.errorMessage,
					passwordError = passwordValidateResult.errorMessage,
					retypePasswordError = retypePasswordValidateResult.errorMessage
				)
			}
			failHandle()
			return@launch
		}
		
		authRepository.register(
			_signUpUiState.value.email,
			_signUpUiState.value.password,
			_signUpUiState.value.fullName
		)
			.success {
				
				sendEvent(SignUpOneTimeEvent.Success)
			}
			.fail {
				sendEvent(SignUpOneTimeEvent.Fail(it))
			}
	}
	
}

data class SignUpUiState(
	override val isLoading: Boolean = false,
	override val errorMessage: String? = null,
	val fullName: String = "",
	val email: String = "",
	val emailError: String? = null,
	val password: String = "",
	val passwordError: String? = null,
	val retypePassword: String = "",
	val retypePasswordError: String? = null,
	val isPasswordVisible: Boolean = false,
): RootState.ViewUiState

sealed interface SignUpOneTimeEvent: RootState.OneTimeEvent<SignUpUiState> {
	override fun reduce(uiState: SignUpUiState): SignUpUiState {
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
	
	object Loading: SignUpOneTimeEvent
	object Success: SignUpOneTimeEvent
	data class GetLoginIntent(val intentSender: IntentSender): SignUpOneTimeEvent
	data class Fail(val errorMessage: String? = null): SignUpOneTimeEvent
}

sealed class SignUpEvent: RootState.ViewEvent {
	data class OnFullNameChange(val fullName: String): SignUpEvent()
	data class OnEmailChange(val email: String): SignUpEvent()
	data class OnPasswordChange(val password: String): SignUpEvent()
	data class OnRetypePasswordChange(val password: String): SignUpEvent()
	data class OnPasswordVisibleChange(val visible: Boolean): SignUpEvent()
	object OnSignUpByDefault: SignUpEvent()
	object OnGetGoogleIntent: SignUpEvent()
	data class OnLoginByGoogle(val intent: Intent): SignUpEvent()
}