package com.notes.aionote

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.media.ThumbnailUtils
import android.net.Uri
import android.provider.MediaStore
import androidx.compose.foundation.gestures.GestureCancellationException
import androidx.compose.foundation.gestures.PressGestureScope
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.consumeDownChange
import androidx.compose.ui.input.pointer.isOutOfBounds
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChangeConsumed
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.Density
import androidx.compose.ui.util.fastAll
import androidx.compose.ui.util.fastAny
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withContext

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

