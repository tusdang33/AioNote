import android.annotation.SuppressLint
import android.view.MotionEvent
import android.widget.TextView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.notes.aionote.presentation.note.components.AioNoteToolbar
import com.notes.aionote.presentation.note.components.NoteToolbarItem

@SuppressLint("UnsafeOptInUsageError")
@Composable
fun AioVideoNote(
	modifier: Modifier = Modifier,
	videoUrl: String,
	onToolbarItemClick: (NoteToolbarItem) -> Unit,
) {
	val context = LocalContext.current
	
	val exoPlayer by remember {
		mutableStateOf(
			ExoPlayer.Builder(context)
				.build()
		)
	}
	
	var showToolbar by remember {
		mutableStateOf(false)
	}
	
	val longPressChecker by remember {
		mutableStateOf(LongPressChecker(0, 0))
	}
	
	LaunchedEffect(Unit) {
		exoPlayer.apply {
			val mediaItem = MediaItem.fromUri(videoUrl)
			setMediaItem(mediaItem)
			prepare()
		}
	}
	
	DisposableEffect(exoPlayer) {
		onDispose {
			exoPlayer.release()
			exoPlayer.stop()
		}
	}
	
	Box(
		modifier = modifier,
		contentAlignment = Alignment.Center
	) {
		AndroidView(
			modifier = Modifier
				.padding(vertical = 12.dp)
				.clip(RoundedCornerShape(12.dp))
				.aspectRatio(9f / 16),
			factory = { context ->
				PlayerView(context).apply {
					player = exoPlayer
					useController = true
					resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
					
					setOnTouchListener { _, motionEvent ->
						if (motionEvent.action == MotionEvent.ACTION_DOWN) {
							performClick()
							longPressChecker.start = System.currentTimeMillis()
						}
						if (motionEvent.action == MotionEvent.ACTION_UP) {
							longPressChecker.end = System.currentTimeMillis()
							showToolbar = longPressChecker.isLongPress
						}
						return@setOnTouchListener false
					}
				}
			}
		)
		AioNoteToolbar(
			toolbarItem = NoteToolbarItem.values().toList(),
			showToolbar = showToolbar,
			onItemClick =  {
				showToolbar = false
				onToolbarItemClick.invoke(it)
			}
		)
	}
}

internal data class LongPressChecker(
	var start: Long,
	var end: Long
) {
	val isLongPress: Boolean
		get() = (end - start) > 600L
}