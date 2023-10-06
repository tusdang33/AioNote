package com.notes.aionote.presentation.note.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.notes.aionote.R
import com.notes.aionote.formatTimestamp
import com.notes.aionote.hourTimePattern
import com.notes.aionote.ui.component.AioIconButton
import com.notes.aionote.ui.theme.AioComposeTheme
import com.notes.aionote.ui.theme.AioTheme

@Composable
fun AioVoiceNote(
	modifier: Modifier = Modifier,
	voiceDuration: Long,
	backgroundColor: Color = AioTheme.primaryColor.light,
	voiceWaveColor: Color = AioTheme.primaryColor.dark,
	textStyle: TextStyle = AioTheme.regularTypography.sm,
	isPlaying: Boolean,
	enabled: Boolean = true,
	onPlayClick: () -> Unit = {},
	onDeleteClick: () -> Unit = {},
) {
	Row(
		modifier = modifier
			.clip(RoundedCornerShape(12.dp))
			.background(backgroundColor),
		verticalAlignment = Alignment.CenterVertically
	) {
		if (enabled) {
			AioIconButton(
				modifier = Modifier,
				onClick = onPlayClick
			) {
				Icon(
					modifier = Modifier.size(32.dp),
					painter = painterResource(id = if (isPlaying) R.drawable.pause_fill else R.drawable.play_fill),
					contentDescription = ""
				)
			}
			
		}
		
		Text(
			modifier = Modifier.padding(horizontal = 10.dp),
			text = voiceDuration.formatTimestamp(hourTimePattern),
			style = textStyle
		)
		
		Icon(
			modifier = Modifier.weight(1f),
			painter = painterResource(id = R.drawable.voice_line),
			tint = voiceWaveColor,
			contentDescription = ""
		)
		
		if (enabled) {
			AioIconButton(onClick = onDeleteClick) {
				Icon(
					modifier = Modifier.size(32.dp),
					painter = painterResource(id = R.drawable.trash_fill),
					contentDescription = ""
				)
			}
		}
	}
}

@Preview
@Composable
private fun PreviewAioVoiceNote() {
	AioComposeTheme {
		AioVoiceNote(
			isPlaying = true,
			voiceDuration = 111511L,
			onPlayClick = {}) {}
	}
}