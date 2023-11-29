package com.notes.aionote

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File

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
	try {
		val intent = Intent(Intent.ACTION_VIEW)
		intent.data = uri
		intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
		context.startActivity(intent)
	} catch (e: ActivityNotFoundException) {
		e.printStackTrace()
	}
}

fun Modifier.conditional(
	condition: Boolean,
	modifier: @Composable Modifier.() -> Modifier
): Modifier = composed {
	if (condition) {
		then(modifier(Modifier))
	} else {
		this
	}
}

fun getVideoThumbnail(context: Context, videoUri: Uri): Bitmap? {
	val retriever = MediaMetadataRetriever()
	try {
		retriever.setDataSource(context, videoUri)
		return retriever.frameAtTime
	} catch (e: Exception) {
		e.printStackTrace()
	} finally {
		retriever.release()
	}
	return null
}

fun getFileExtension(
	uri: Uri,
	context: Context
): String? {
	val contentResolver = context.contentResolver
	val mimeTypeMap = MimeTypeMap.getSingleton()
	
	val fileType = contentResolver.getType(uri)
	
	return mimeTypeMap.getExtensionFromMimeType(fileType)
}

fun getFileName(
	uri: Uri,
	context: Context
): String? {
	
	var fileName: String? = null
	val contentResolver = context.contentResolver
	val cursor = contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
	
	cursor?.use {
		if (it.moveToFirst()) {
			val displayName = it.getString(
				it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
					.coerceAtLeast(0)
			)
			
			if (!displayName.isNullOrEmpty()) {
				fileName = displayName
			}
		}
	} ?: run {
		fileName = File(uri.path ?: "").name
		
	}
	
	return fileName
}

