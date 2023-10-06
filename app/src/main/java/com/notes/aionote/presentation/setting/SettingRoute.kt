package com.notes.aionote.presentation.setting

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.notes.aionote.ui.component.AioButton

@Composable
fun SettingRoute(
	navigateToSignIn: () -> Unit,
	settingViewModel: SettingViewModel = hiltViewModel()
) {
	val currentUser by settingViewModel.currentUser.collectAsStateWithLifecycle()
	val settingOneTimeEvent by settingViewModel.oneTimeEvent.collectAsStateWithLifecycle(
		initialValue = null
	)
	Column() {
		AioButton(onClick = { settingViewModel.onEvent(SettingEvent.OnLogout) }) {
			Text(text = "logout")
		}
		
		AioButton(onClick = { settingViewModel.onEvent(SettingEvent.OnGetCurrentUser) }) {
			Text(text = "get current user")
		}
		
		Text(text = currentUser?.uid ?: "none")
	}
	
	
	when(settingOneTimeEvent) {
		is SettingOneTimeEvent.OnLogout -> {
			navigateToSignIn.invoke()
		}
		
		else -> {}
	}
}