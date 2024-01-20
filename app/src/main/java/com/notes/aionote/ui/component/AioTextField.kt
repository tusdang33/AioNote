package com.notes.aionote.ui.component

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.notes.aionote.ui.theme.AioTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AioTextField(
	modifier: Modifier = Modifier,
	text: String,
	focusRequester: FocusRequester = FocusRequester(),
	textStyle: TextStyle = AioTheme.regularTypography.base,
	textFieldColors: Color = AioTheme.neutralColor.white,
	onTextChange: (String) -> Unit,
	singleLine : Boolean = false,
	enabled: Boolean = true,
	contentPadding: PaddingValues = PaddingValues(0.dp),
	keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
	keyboardActions: KeyboardActions = KeyboardActions.Default,
	placeholder: @Composable (() -> Unit)? = null,
) {
	
	BasicTextField(
		modifier = modifier
			.focusRequester(focusRequester)
			.height(IntrinsicSize.Min),
		value = text,
		textStyle = textStyle,
		onValueChange = onTextChange,
		keyboardOptions = keyboardOptions,
		keyboardActions = keyboardActions,
		enabled = enabled,
		decorationBox = { innerTextField ->
			TextFieldDefaults.DecorationBox(
				value = text,
				innerTextField = innerTextField,
				enabled = enabled,
				placeholder = placeholder,
				singleLine = singleLine,
				visualTransformation = VisualTransformation.None,
				interactionSource = remember { MutableInteractionSource() },
				supportingText = null,
				shape = TextFieldDefaults.shape,
				colors = TextFieldDefaults.colors(
					focusedContainerColor = textFieldColors,
					unfocusedContainerColor = textFieldColors,
					focusedIndicatorColor = textFieldColors,
					unfocusedIndicatorColor = textFieldColors,
					disabledSupportingTextColor = textFieldColors,
					disabledContainerColor = textFieldColors,
					disabledIndicatorColor = textFieldColors,
					disabledLabelColor = textFieldColors,
				),
				contentPadding = contentPadding,
			)
		}
	)
}