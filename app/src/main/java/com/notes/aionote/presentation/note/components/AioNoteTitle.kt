package com.notes.aionote.presentation.note.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.notes.aionote.formatTimestamp
import com.notes.aionote.ui.component.AioTextField
import com.notes.aionote.ui.theme.AioComposeTheme
import com.notes.aionote.ui.theme.AioTheme

@Composable
fun AioNoteTitle(
	modifier: Modifier = Modifier,
	text: String,
	currentTime: Long,
	onTextChange: (String) -> Unit
) {
	Column(
		modifier = modifier
			.background(AioTheme.neutralColor.white)
			.padding(bottom = 20.dp),
		verticalArrangement = Arrangement.spacedBy(8.dp)
	) {
		AioTextField(
			modifier = Modifier
				.fillMaxWidth(),
			text = text,
			textStyle = AioTheme.mediumTypography.xl,
			onTextChange = onTextChange,
			placeholder = {
				Text(
					text = "Title",
					style = AioTheme.mediumTypography.xl.copy(color = AioTheme.neutralColor.dark)
				)
			},
			textFieldColors = AioTheme.neutralColor.white
		)
		Text(
			modifier = Modifier
				.fillMaxWidth(),
			text = currentTime.formatTimestamp(),
			style = AioTheme.regularTypography.base.copy(color = AioTheme.neutralColor.dark)
		)
	}
	
}

@Preview
@Composable
private fun PreviewAioNoteTitle() {
	AioComposeTheme {
		AioNoteTitle(text = "", onTextChange = {}, currentTime = 0L)
	}
}

