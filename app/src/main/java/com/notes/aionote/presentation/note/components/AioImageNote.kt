package com.notes.aionote.presentation.note.components

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest

@Composable
fun AioImageNote(
	modifier: Modifier = Modifier,
	image: String,
	onToolbarItemClick: (NoteToolbarItem) -> Unit,
) {
	var showToolbar by remember {
		mutableStateOf(false)
	}
	Box(
		modifier = modifier,
		contentAlignment = Alignment.Center
	) {
		AsyncImage(
			modifier = Modifier
				.padding(vertical = 12.dp)
				.clip(RoundedCornerShape(12.dp))
				.fillMaxWidth()
				.pointerInput(Unit) {
					detectTapGestures(onLongPress = {
						showToolbar = !showToolbar
					})
				},
			model = image,
			contentDescription = "",
			contentScale = ContentScale.Crop
		)
		AioNoteToolbar(
			toolbarItem = NoteToolbarItem.values().toList(),
			showToolbar = showToolbar,
			onItemClick = {
				showToolbar = false
				onToolbarItemClick.invoke(it as NoteToolbarItem)
			}
		)
	}
	
}