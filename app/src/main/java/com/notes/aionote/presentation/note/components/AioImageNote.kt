package com.notes.aionote.presentation.note.components

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import coil.compose.AsyncImage
import com.notes.aionote.conditional

@Composable
fun AioImageNote(
	modifier: Modifier = Modifier,
	image: String,
	zoomed: Boolean = true,
	onToolbarItemClick: (ImageNoteContentToolbarItem) -> Unit,
) {
	var showToolbar by remember {
		mutableStateOf(false)
	}
	
	val imageFraction by animateFloatAsState(targetValue = if (zoomed) 1f else 0.5f)

	Box(
		modifier = modifier,
		contentAlignment = Alignment.Center
	) {
		AsyncImage(
			modifier = Modifier
				.padding(vertical = 12.dp)
				.clip(RoundedCornerShape(12.dp))
				.fillMaxWidth(imageFraction)
				.pointerInput(Unit) {
					detectTapGestures(onLongPress = {
						showToolbar = !showToolbar
					})
				},
			model = image,
			contentDescription = "",
			contentScale = ContentScale.FillWidth
		)
		AioNoteToolbar(
			toolbarItem = ImageNoteContentToolbarItem.values().toList(),
			showToolbar = showToolbar,
			onItemClick = {
				showToolbar = false
				onToolbarItemClick.invoke(it as ImageNoteContentToolbarItem)
			}
		)
	}
}