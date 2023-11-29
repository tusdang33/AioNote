package com.notes.aionote.presentation.note.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.notes.aionote.ui.theme.AioComposeTheme
import com.notes.aionote.ui.theme.AioTheme

@Composable
fun AioCheckNote(
	modifier: Modifier = Modifier,
	checked: Boolean = false,
	text: String = "",
	focusRequester: FocusRequester = FocusRequester(),
	textStyle: TextStyle = AioTheme.regularTypography.base,
	checkBoxSize: DpSize = DpSize(20.dp, 20.dp),
	scaleSize: Float = 1f,
	checkBoxEnable: Boolean = true,
	textFieldEnable: Boolean = true,
	isCheckboxOnly: Boolean = false,
	onCheckedChange: (Boolean) -> Unit = {},
	onTextChange: (String) -> Unit = {},
	onDone: () -> Unit = {},
	onDeleteCheckbox: () -> Unit = {},
) {
	
	Row(
		modifier = modifier
			.background(AioTheme.neutralColor.white),
		verticalAlignment = Alignment.CenterVertically
	) {
		Checkbox(
			modifier = Modifier
				.size(checkBoxSize)
				.scale(scaleSize),
			colors = CheckboxDefaults.colors(
				checkedColor = AioTheme.primaryColor.base,
				uncheckedColor = AioTheme.primaryColor.base,
				checkmarkColor = AioTheme.neutralColor.white
			),
			checked = checked,
			enabled = checkBoxEnable,
			onCheckedChange = onCheckedChange
		)
		
		if(!isCheckboxOnly) {
			Spacer(modifier = Modifier.width(5.dp))
			
			BasicTextField(
				modifier = Modifier
					.focusRequester(focusRequester)
					.onKeyEvent {
						Log.e("tudm", "AioCheckNote ${it.key} ",)
						if (it.key.keyCode == 287762808832 && text.isEmpty()) {
							onDeleteCheckbox.invoke()
							true
						} else {
							true
						}
					}
					.height(IntrinsicSize.Min)
					.fillMaxWidth(),
				value = text,
				textStyle = textStyle,
				enabled = textFieldEnable,
				onValueChange = onTextChange,
				keyboardOptions = KeyboardOptions(
					autoCorrect = false,
					keyboardType = KeyboardType.Text,
					imeAction = ImeAction.Done,
				),
				keyboardActions = KeyboardActions(
					onDone = {
						onDone.invoke()
					}
				)
			)
		}
	}
}

@Preview
@Composable
private fun PreviewAioCheckNote() {
	var check by remember {
		mutableStateOf(false)
	}
	
	var text by remember {
		mutableStateOf("")
	}
	AioComposeTheme {
		AioCheckNote(
			text = text,
			checked = check,
			onCheckedChange = {
				check = it
			},
			onTextChange = {
				text = it
			},
			onDeleteCheckbox = {}
		)
	}
}