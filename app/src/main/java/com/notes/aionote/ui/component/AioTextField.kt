package com.notes.aionote.ui.component

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.notes.aionote.ui.theme.AioTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AioTextField(
	modifier: Modifier = Modifier,
	text: String,
	textStyle: TextStyle = AioTheme.regularTypography.base,
	textFieldColors: Color = AioTheme.neutralColor.white,
	onTextChange: (String) -> Unit,
	placeholder: @Composable (() -> Unit)? = null,
) {
	
	BasicTextField(
		modifier = modifier
			.height(IntrinsicSize.Min)
			.fillMaxWidth(),
		value = text,
		textStyle = textStyle,
		onValueChange = onTextChange,
		decorationBox = { innerTextField ->
			TextFieldDefaults.DecorationBox(
				value = text,
				innerTextField = innerTextField,
				enabled = true,
				placeholder = placeholder,
				singleLine = false,
				visualTransformation = VisualTransformation.None,
				interactionSource = remember { MutableInteractionSource() },
				supportingText = null,
				shape = TextFieldDefaults.shape,
				colors = TextFieldDefaults.colors(
					focusedContainerColor = textFieldColors,
					unfocusedContainerColor = textFieldColors,
					focusedIndicatorColor = textFieldColors,
					unfocusedIndicatorColor = textFieldColors
				),
				contentPadding = TextFieldDefaults.contentPaddingWithoutLabel(
					0.dp, 0.dp, 0.dp, 0.dp
				),
			)
		}
	)
}