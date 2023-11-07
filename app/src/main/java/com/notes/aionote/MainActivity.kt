package com.notes.aionote

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.work.WorkManager
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.notes.aionote.ui.theme.AioComposeTheme
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp

@AndroidEntryPoint
class MainActivity: ComponentActivity() {
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
			AioComposeTheme {
				AioApp()
			}
		}
	}
}