package com.notes.aionote

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.getValue
import androidx.core.app.ActivityCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity: ComponentActivity() {
	private val mainViewModel: MainViewModel by viewModels()
	@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterialNavigationApi::class)
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		
		ActivityCompat.requestPermissions(
			this,
			arrayOf(
				Manifest.permission.RECORD_AUDIO,
				Manifest.permission.READ_EXTERNAL_STORAGE,
				Manifest.permission.WRITE_EXTERNAL_STORAGE,
				Manifest.permission.CAMERA,
				Manifest.permission.POST_NOTIFICATIONS,
			),
			0
		)
		setContent {
			val mainUiState by mainViewModel.mainUiState.collectAsStateWithLifecycle()
			AioApp(mainUiState = mainUiState)
		}
	}
}