package com.notes.aionote.presentation.note.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import com.notes.aionote.ui.component.AioTextField
import com.notes.aionote.ui.theme.AioTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AioTextNote(
	modifier: Modifier = Modifier,
	text: String,
	focusRequester: FocusRequester,
	onTextChange: (String) -> Unit,
	onDeleteCheckbox: () -> Unit,
) {
	AioTextField(
		modifier = modifier
			.onKeyEvent {
				if (it.key.keyCode == 287762808832 && text.isEmpty()) {
					onDeleteCheckbox.invoke()
					true
				} else {
					true
				}
			},
		text = text,
		focusRequester = focusRequester,
		textStyle = AioTheme.regularTypography.base,
		onTextChange = onTextChange,
	)
}