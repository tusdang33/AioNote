package com.notes.aionote.presentation.note.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.notes.aionote.R
import com.notes.aionote.ui.component.AioIconButton
import com.notes.aionote.ui.theme.AioComposeTheme
import com.notes.aionote.ui.theme.AioTheme

@Composable
fun AioNotePicker(
	modifier: Modifier = Modifier,
	options: List<NoteOption>,
	holdingNoteOption: NoteOption?,
	onOptionClick: (NoteOption) -> Unit
) {
	Column(modifier = modifier.requiredHeightIn(min = 48.dp)) {
		Divider()
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.background(AioTheme.neutralColor.white),
			horizontalArrangement = Arrangement.SpaceAround
		) {
			options.forEach {
				val holding = holdingNoteOption == it
				AioIconButton(
					modifier = Modifier.weight(1f),
					onClick = {
						onOptionClick.invoke(it)
					}
				) {
					Image(
						painter = painterResource(
							id = if (holding) it.altIcon ?: it.icon else it.icon
						), contentDescription = ""
					)
				}
			}
		}
	}
}

enum class NoteOption(
	@DrawableRes
	val icon: Int,
	@DrawableRes
	val altIcon: Int? = null
) {
	VOICE(
		R.drawable.microphone_outline,
		R.drawable.stop_fill
	),
	CHECK(R.drawable.check_outline),
	IMAGE(R.drawable.photograph_outline),
	CAMERA(R.drawable.camera_outline),
	VIDEO(R.drawable.film_outline),
	RECORD(R.drawable.video_camera_outline),
}

@Preview
@Composable
private fun PreviewAioNotePicker() {
	AioComposeTheme {
		AioNotePicker(
			options = NoteOption.values().toList(),
			onOptionClick = {},
			holdingNoteOption = null
		)
	}
}