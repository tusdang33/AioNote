package com.notes.aionote.presentation.note.components

import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.notes.aionote.R
import com.notes.aionote.data.model.NoteContent
import com.notes.aionote.ui.component.AioIconButton
import com.notes.aionote.ui.theme.AioComposeTheme
import com.notes.aionote.ui.theme.AioTheme

@Composable
fun AioNoteToolbar(
	modifier: Modifier = Modifier,
	toolbarItem: List<NoteToolBar>,
	showToolbar: Boolean = false,
	elevation: Dp = 5.dp,
	onItemClick: (NoteToolBar) -> Unit
) {
	AnimatedVisibility(
		visible = showToolbar,
		exit = scaleOut(),
		enter = scaleIn()
	) {
		Row(
			modifier = modifier
				.shadow(elevation, RoundedCornerShape(12.dp))
				.background(AioTheme.neutralColor.white),
			horizontalArrangement = Arrangement.SpaceAround
		) {
			toolbarItem.forEach {
				AioIconButton(
					onClick = { onItemClick.invoke(it) }
				) {
					Image(
						modifier = Modifier.padding(horizontal = 20.dp),
						painter = painterResource(id = it.icon),
						contentDescription = ""
					)
				}
			}
		}
	}
}

enum class NoteToolbarItem: NoteToolBar {
	DELETE {
		override val icon: Int = R.drawable.trash_outline
	}
}

enum class ImagePickerToolbarItem : NoteToolBar {
	IMAGE {
		override val icon: Int = R.drawable.photograph_outline
	},
	CAMERA {
		override val icon: Int = R.drawable.camera_outline
	}
}

enum class VideoPickerToolbarItem : NoteToolBar {
	VIDEO {
		override val icon: Int = R.drawable.film_outline
	},
	RECORD {
		override val icon: Int = R.drawable.video_camera_outline
	}
}

interface NoteToolBar {
	@get:DrawableRes
	val icon : Int
}

@Preview
@Composable
private fun PreviewAioNoteToolbar() {
	AioComposeTheme {
		AioNoteToolbar(
			toolbarItem = NoteToolbarItem.values().toList(),
			onItemClick = {
			
			}
		)
	}
}