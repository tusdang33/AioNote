package com.notes.aionote.presentation.authentication.sign_in

import android.content.Intent
import android.content.IntentSender
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.notes.aionote.common.AioConst
import com.notes.aionote.common.AioDispatcher
import com.notes.aionote.common.Dispatcher
import com.notes.aionote.common.FirebaseConst
import com.notes.aionote.common.RootState
import com.notes.aionote.common.RootViewModel
import com.notes.aionote.common.fail
import com.notes.aionote.common.success
import com.notes.aionote.domain.repository.AuthRepository
import com.notes.aionote.domain.repository.CategoryRepository
import com.notes.aionote.domain.repository.NoteRepository
import com.notes.aionote.domain.use_case.authentication.ValidateEmailUseCase
import com.notes.aionote.domain.use_case.authentication.ValidatePasswordUseCase
import com.notes.aionote.worker.SyncWork
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
	private val authRepository: AuthRepository,
	private val validateEmailUseCase: ValidateEmailUseCase,
	private val validatePasswordUseCase: ValidatePasswordUseCase,
	private val instanceWorkManager : WorkManager,
	private val noteRepository: NoteRepository,
	private val categoryRepository: CategoryRepository,
	@Dispatcher(AioDispatcher.IO) private val ioDispatcher: CoroutineDispatcher
): RootViewModel<SignInUiState, SignInOneTimeEvent, SignInEvent>() {
	private val coroutineExceptionHandler: CoroutineExceptionHandler
		get() = CoroutineExceptionHandler { _, exception ->
			failHandle(exception.message)
		}
	
	private val _signInUiState = MutableStateFlow(SignInUiState())
	override val uiState: StateFlow<SignInUiState> = _signInUiState.asStateFlow()
	
	override fun reduceUiStateFromOneTimeEvent(
		uiState: SignInUiState,
		oneTimeEvent: SignInOneTimeEvent
	) {
		_signInUiState.value = uiState
	}
	
	private fun failHandle(errorMessage: String? = null) {
		sendEvent(SignInOneTimeEvent.Fail(errorMessage))
	}
	
	override fun onEvent(event: SignInEvent) {
		when (event) {
			is SignInEvent.OnEmailChange -> {
				_signInUiState.update {
					it.copy(
						email = event.email,
						emailError = null
					)
				}
			}
			
			is SignInEvent.OnPasswordChange -> {
				_signInUiState.update {
					it.copy(
						password = event.password,
						passwordError = null
					)
				}
			}
			
			is SignInEvent.OnPasswordVisibleChange -> {
				_signInUiState.update {
					it.copy(
						isPasswordVisible = event.visible
					)
				}
			}
			
			is SignInEvent.OnLoginDefault -> login()
			is SignInEvent.OnGetGoogleIntent -> getGoogleIntent()
			is SignInEvent.OnLoginByGoogle -> loginByGoogle(event.intent)
		}
	}
	
	private fun login() = viewModelScope.launch(ioDispatcher + coroutineExceptionHandler) {
		sendEvent(SignInOneTimeEvent.Loading)
		delay(300L)
		val emailValidateResult = validateEmailUseCase(_signInUiState.value.email)
		val passwordValidateResult = validatePasswordUseCase(_signInUiState.value.password)
		
		val hasError = listOf(
			emailValidateResult,
			passwordValidateResult,
		).any { !it.successful }
		
		if (hasError) {
			_signInUiState.update {
				it.copy(
					emailError = emailValidateResult.errorMessage,
					passwordError = passwordValidateResult.errorMessage,
				)
			}
			failHandle()
			return@launch
		}
		
		noteRepository.deleteAllNote()
		noteRepository.deleteAllDeletedNoteId()
		categoryRepository.deleteAllCategory()
		authRepository.login(_signInUiState.value.email, _signInUiState.value.password)
			.success {
				sendEvent(SignInOneTimeEvent.Success)
				syncData(it?.id ?: "")
			}
			.fail {
				failHandle("Sign In Fail")
			}
	}
	
	private fun syncData(userId: String) {
//		val syncWork = OneTimeWorkRequestBuilder<SyncWork>()
//			.setInputData(
//				Data.Builder()
//					.putString(FirebaseConst.FIREBASE_SYNC_USER_ID, userId)
//					.build()
//			)
//			.build()
//		instanceWorkManager.beginUniqueWork(
//			AioConst.SYNC_WORK,
//			ExistingWorkPolicy.REPLACE,
//			syncWork
//		).enqueue()
	}
	
	private fun getGoogleIntent() = viewModelScope.launch(ioDispatcher) {
		sendEvent(SignInOneTimeEvent.Loading)
		authRepository.oneTapSignIn().success { intentSender ->
			intentSender?.let {
				sendEvent(SignInOneTimeEvent.GetLoginIntent(it))
			}
		}.fail {
			failHandle(it)
		}
	}
	
	private fun loginByGoogle(intent: Intent) = viewModelScope.launch(ioDispatcher) {
		sendEvent(SignInOneTimeEvent.Loading)
		authRepository.signInWithGoogle(intent).success {
			sendEvent(SignInOneTimeEvent.Success)
		}.fail {
			failHandle(it)
		}
	}
}

data class SignInUiState(
	val email: String = "",
	val emailError: String? = null,
	val password: String = "",
	val passwordError: String? = null,
	val isPasswordVisible: Boolean = false,
	override val isLoading: Boolean = false,
	override val errorMessage: String? = null,
): RootState.ViewUiState

sealed interface SignInOneTimeEvent: RootState.OneTimeEvent<SignInUiState> {
	override fun reduce(uiState: SignInUiState): SignInUiState {
		return when (this) {
			is Fail -> uiState.copy(
				isLoading = false,
				errorMessage = errorMessage
			)
			
			is Loading -> uiState.copy(
				isLoading = true,
				errorMessage = null
			)
			
			is Success, is GetLoginIntent-> uiState.copy(
				isLoading = false,
				errorMessage = null
			)
		}
	}
	
	object Loading: SignInOneTimeEvent
	object Success: SignInOneTimeEvent
	data class GetLoginIntent(val intentSender: IntentSender): SignInOneTimeEvent
	data class Fail(val errorMessage: String? = null): SignInOneTimeEvent
}

sealed class SignInEvent: RootState.ViewEvent {
	data class OnEmailChange(val email: String): SignInEvent()
	data class OnPasswordChange(val password: String): SignInEvent()
	data class OnPasswordVisibleChange(val visible: Boolean): SignInEvent()
	object OnLoginDefault: SignInEvent()
	object OnGetGoogleIntent: SignInEvent()
	data class OnLoginByGoogle(val intent: Intent): SignInEvent()
}