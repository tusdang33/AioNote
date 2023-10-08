package com.notes.aionote.presentation.note.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.notes.aionote.R
import com.notes.aionote.formatTimestamp
import com.notes.aionote.hourTimePattern
import com.notes.aionote.ui.component.AioIconButton
import com.notes.aionote.ui.theme.AioComposeTheme
import com.notes.aionote.ui.theme.AioTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@Composable
fun AioVoiceNote(
	modifier: Modifier = Modifier,
	voiceDuration: Long,
	backgroundColor: Color = AioTheme.primaryColor.light,
	voiceWaveColor: Color = AioTheme.primaryColor.base,
	durationBarColor: Color = AioTheme.primaryColor.base.copy(alpha = 0.5f),
	durationStep: Long = 50L,
	cursorColor: Color = AioTheme.primaryColor.dark,
	textStyle: TextStyle = AioTheme.regularTypography.sm,
	isPlaying: Boolean,
	enabled: Boolean = true,
	onPlayClick: () -> Unit = {},
	onDeleteClick: () -> Unit = {},
) {
	
	val localDensity = LocalDensity.current
	var currentHeight by remember {
		mutableStateOf(Dp.Unspecified)
	}
	var maxVoiceWidth by remember {
		mutableStateOf(Dp.Unspecified)
	}
	
	var voicePosition by remember {
		mutableStateOf(Dp.Unspecified)
	}
	
	LaunchedEffect(isPlaying) {
		
		var currentDuration = 0L
		if (isPlaying) {
			while (isActive) {
				delay(durationStep)
				currentDuration += durationStep
				voicePosition = maxVoiceWidth * (currentDuration / voiceDuration.toFloat())
			}
		} else {
			voicePosition = 0.dp
		}
	}
	
	Row(
		modifier = modifier
			.clip(RoundedCornerShape(12.dp))
			.background(backgroundColor)
			.onGloballyPositioned { coordinate ->
				currentHeight = with(localDensity) {
					coordinate.size.height.toDp()
				}
			},
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
		
		Box(
			modifier = Modifier.weight(1f),
			contentAlignment = Alignment.Center
		) {
			Box(
				modifier = Modifier
					.align(Alignment.CenterStart)
					.height(currentHeight)
					.width(voicePosition)
					.background(durationBarColor),
			) {
				Divider(
					color = cursorColor,
					modifier = Modifier
						.align(Alignment.CenterEnd)
						.height(currentHeight)
						.width(1.dp)
				)
			}
			Icon(
				modifier = Modifier
					.onGloballyPositioned { coordinate ->
						maxVoiceWidth = with(localDensity) {
							coordinate.size.width.toDp()
						}
					},
				painter = painterResource(id = R.drawable.voice_line),
				tint = voiceWaveColor,
				contentDescription = ""
			)
		}
		
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