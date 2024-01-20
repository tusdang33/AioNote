package com.notes.aionote.presentation.setting.edit_profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.notes.aionote.common.RootState
import com.notes.aionote.common.RootViewModel
import com.notes.aionote.common.fail
import com.notes.aionote.common.success
import com.notes.aionote.domain.repository.AuthRepository
import com.notes.aionote.domain.use_case.authentication.ValidateEmailUseCase
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
class EditProfileViewModel @Inject constructor(
	private val authRepository: AuthRepository,
	private val savedStateHandle: SavedStateHandle,
	private val validateEmailUseCase: ValidateEmailUseCase,
): RootViewModel<EditProfileUiState, EditProfileOneTimeEvent, EditProfileEvent>() {
	
	private val image: String? = savedStateHandle["image"]
	private val name: String? = savedStateHandle["userName"]
	private val email: String? = savedStateHandle["userEmail"]

	private val _editProfileUiState = MutableStateFlow(EditProfileUiState())
	override val uiState: StateFlow<EditProfileUiState> = _editProfileUiState.asStateFlow()
	
	init {
		_editProfileUiState.update {
			it.copy(
				image = if (image == "null") null else image,
				name = if (name == "null") null else name,
				email = email ?: ""
			)
		}
	}
	
	override fun reduceUiStateFromOneTimeEvent(
		uiState: EditProfileUiState,
		oneTimeEvent: EditProfileOneTimeEvent
	) {
		_editProfileUiState.value = uiState
	}
	
	override fun onEvent(event: EditProfileEvent) {
		when (event) {
			EditProfileEvent.OnChangeImage -> {
				sendEvent(EditProfileOneTimeEvent.OnImageChange)
			}
			
			is EditProfileEvent.OnEmailChange -> {
				_editProfileUiState.update {
					it.copy(
						email = event.email,
						emailErrorMessage = null
					)
				}
			}
			
			is EditProfileEvent.OnNameChange -> {
				_editProfileUiState.update {
					it.copy(
						name = event.name
					)
				}
			}
			
			EditProfileEvent.OnSubmit -> {
				submit()
			}
			
			EditProfileEvent.OnDismissDialog -> {
				_editProfileUiState.update {
					it.copy(
						isSubmitSuccess = null
					)
				}
			}
			
			is EditProfileEvent.AddImage -> {
				_editProfileUiState.update {
					it.copy(
						image = event.image
					)
				}
			}
		}
	}
	
	private fun submit() = viewModelScope.launch(NonCancellable) {
		val state = _editProfileUiState.value
		sendEvent(EditProfileOneTimeEvent.Loading)
		val emailValidateResult = validateEmailUseCase(state.email)
		if (!emailValidateResult.successful) {
			_editProfileUiState.update {
				it.copy(
					emailErrorMessage = emailValidateResult.errorMessage
				)
			}
			return@launch
		}
		authRepository.updateProfile(
			image = state.image,
			email = if (state.email != email) state.email else null,
			name = state.name
		).success {
			_editProfileUiState.update { uiState ->
				uiState.copy(
					isSubmitSuccess = true
				)
			}
		}.fail {
			_editProfileUiState.update { uiState ->
				uiState.copy(
					isSubmitSuccess = false
				)
			}
		}
		sendEvent(EditProfileOneTimeEvent.Success)
	}
}

data class EditProfileUiState(
	override val isLoading: Boolean = false,
	override val errorMessage: String? = null,
	val image: String? = null,
	val name: String? = null,
	val email: String = "",
	val emailErrorMessage: String? = null,
	val isSubmitSuccess: Boolean? = null
): RootState.ViewUiState

sealed interface EditProfileOneTimeEvent: RootState.OneTimeEvent<EditProfileUiState> {
	override fun reduce(uiState: EditProfileUiState): EditProfileUiState {
		return when (this) {
			
			is EditProfileOneTimeEvent.Loading -> uiState.copy(
				isLoading = true,
				errorMessage = ""
			)
			
			is EditProfileOneTimeEvent.Success -> uiState.copy(
				isLoading = false,
				errorMessage = ""
			)
			
			else -> uiState
		}
	}
	
	object Loading: EditProfileOneTimeEvent
	object Success: EditProfileOneTimeEvent
	object OnImageChange: EditProfileOneTimeEvent
}

sealed class EditProfileEvent: RootState.ViewEvent {
	data class OnNameChange(val name: String): EditProfileEvent()
	data class OnEmailChange(val email: String): EditProfileEvent()
	data class AddImage(val image: String): EditProfileEvent()
	object OnSubmit: EditProfileEvent()
	object OnChangeImage: EditProfileEvent()
	object OnDismissDialog: EditProfileEvent()
}