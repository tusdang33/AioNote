package com.notes.aionote

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.content.FileProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@Suppress("ComposableNaming")
@Composable
fun <T> Flow<T>.collectInLaunchedEffectWithLifecycle(
	vararg keys: Any?,
	lifecycle: Lifecycle = LocalLifecycleOwner.current.lifecycle,
	minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
	collector: suspend CoroutineScope.(T) -> Unit
) {
	val flow = this
	val currentCollector by rememberUpdatedState(collector)
	
	LaunchedEffect(flow, lifecycle, minActiveState, *keys) {
		withContext(Dispatchers.Main.immediate) {
			lifecycle.repeatOnLifecycle(minActiveState) {
				flow.collect { currentCollector(it) }
			}
		}
	}
}

fun grantReadPermissionToUri(
	context: Context,
	uri: Uri
) {
	context.grantUriPermission(
		context.packageName,
		uri,
		Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
	)
	context.contentResolver.takePersistableUriPermission(
		uri,
		Intent.FLAG_GRANT_READ_URI_PERMISSION
	)
}

fun viewDocument(context: Context, uri: Uri) {
	val intent = Intent(Intent.ACTION_VIEW)
	intent.data = uri
	intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
	
	try {
		context.startActivity(intent)
	} catch (e: ActivityNotFoundException) {
	}
}
