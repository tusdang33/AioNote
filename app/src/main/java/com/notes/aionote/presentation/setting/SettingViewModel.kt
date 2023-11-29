package com.notes.aionote.presentation.setting

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.google.firebase.auth.FirebaseUser
import com.notes.aionote.common.AioConst
import com.notes.aionote.common.AioDispatcher
import com.notes.aionote.common.Dispatcher
import com.notes.aionote.common.FirebaseConst
import com.notes.aionote.common.RootState
import com.notes.aionote.common.RootViewModel
import com.notes.aionote.common.success
import com.notes.aionote.domain.repository.AuthRepository
import com.notes.aionote.domain.repository.SyncRepository
import com.notes.aionote.worker.SyncWork
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
	private val instanceWorkManager: WorkManager,
	private val syncRepository: SyncRepository,
	@Dispatcher(AioDispatcher.IO) private val ioDispatcher: CoroutineDispatcher
):
	RootViewModel<SettingUiState, SettingOneTimeEvent, SettingEvent>() {
	override val coroutineExceptionHandler: CoroutineExceptionHandler
		get() = CoroutineExceptionHandler { _, _ -> }
	
	private val _settingUiState = MutableStateFlow(SettingUiState())
	override val uiState: StateFlow<SettingUiState> = _settingUiState.asStateFlow()
	
	private var workLiveData: LiveData<WorkInfo>? = null
	private var workObserver: Observer<WorkInfo?> = Observer { workInfo ->
		if (workInfo == null ) return@Observer
		if (workInfo.state == WorkInfo.State.ENQUEUED || workInfo.state == WorkInfo.State.RUNNING) {
			_settingUiState.update {
				it.copy(
					isSyncing = true
				)
			}
		} else {
			_settingUiState.update {
				it.copy(
					isSyncing = false
				)
			}
		}
	}
	
	private fun fetchUserData() = viewModelScope.launch {
		authRepository.getCurrentUser<FirebaseUser>().success { fuser ->
			fuser?.let { user ->
				_settingUiState.update { uiState ->
					uiState.copy(
						userImage = if (user.photoUrl != null) user.photoUrl.toString() else null,
						userName = user.displayName?.ifBlank { null },
						userEmail = user.email ?: "",
						userId = user.uid
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
			
			SettingEvent.OnChangePassword -> viewModelScope.launch {
				sendEvent(SettingOneTimeEvent.OnChangePassword(_settingUiState.value.userEmail))
			}
			
			SettingEvent.OnSync -> {
				syncToRemote(_settingUiState.value.userId)
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
	
	private fun syncToRemote(userId: String) {
		val syncWork = OneTimeWorkRequestBuilder<SyncWork>()
			.setInputData(
				Data.Builder()
					.putString(FirebaseConst.FIREBASE_SYNC_USER_ID, userId)
					.build()
			)
			.build()
		instanceWorkManager.beginUniqueWork(
			AioConst.SYNC_WORK,
			ExistingWorkPolicy.REPLACE,
			syncWork
		).enqueue()
		
		workLiveData = instanceWorkManager.getWorkInfoByIdLiveData(syncWork.id)
		workLiveData?.observeForever(workObserver)
	}
	
	override fun onCleared() {
		workLiveData?.removeObserver(workObserver)
		workLiveData = null
		super.onCleared()
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
	val userEmail: String = "",
	val userId: String = "",
	val isSyncing : Boolean = false
): RootState.ViewUiState

sealed class SettingEvent: RootState.ViewEvent {
	object OnLogout: SettingEvent()
	object OnFetchUserData: SettingEvent()
	object OnEditProfile: SettingEvent()
	object OnChangePassword: SettingEvent()
	object OnSync: SettingEvent()
}