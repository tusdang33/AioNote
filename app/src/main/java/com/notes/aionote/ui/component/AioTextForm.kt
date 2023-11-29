package com.notes.aionote.ui.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.notes.aionote.R
import com.notes.aionote.ui.theme.AioComposeTheme
import com.notes.aionote.ui.theme.AioTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AioTextForm(
	modifier: Modifier = Modifier,
	value: String,
	onValueChange: (String) -> Unit,
	label: String? = null,
	errorMessage: String? = null,
	enabled: Boolean = true,
	readOnly: Boolean = false,
	shape: Shape = RoundedCornerShape(8.dp),
	textColor: Color = AioTheme.neutralColor.black,
	singleLine: Boolean = false,
	minLines: Int = 1,
	maxLines: Int = Int.MAX_VALUE,
	focusRequester: FocusRequester = FocusRequester(),
	visualTransformation: VisualTransformation = VisualTransformation.None,
	interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
	backgroundColor: Color = AioTheme.neutralColor.white,
	borderColor: Color = AioTheme.primaryColor.base,
	cursorColor: Color = AioTheme.neutralColor.black,
	textStyle: TextStyle = AioTheme.regularTypography.base,
	keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
	keyboardActions: KeyboardActions = KeyboardActions.Default,
	placeholder: @Composable (() -> Unit)? = null,
	leadingIcon: @Composable (() -> Unit)? = null,
	trailingIcon: @Composable (() -> Unit)? = null,
) {
	val style = textStyle.merge(
		TextStyle(
			color = textColor,
		)
	)
	
	val colors = TextFieldDefaults.colors(
		focusedContainerColor = backgroundColor,
		unfocusedContainerColor = backgroundColor,
		disabledContainerColor = backgroundColor,
		cursorColor = cursorColor,
		focusedIndicatorColor = borderColor,
		unfocusedIndicatorColor = AioTheme.neutralColor.base,
		disabledSupportingTextColor = AioTheme.neutralColor.light,
	)
	
	Column(
		modifier = modifier
			.background(backgroundColor)
			.requiredHeightIn(
				min = BasicTextFieldDefault.MinHeight
			),
		verticalArrangement = Arrangement.spacedBy(12.dp)
	) {
		label?.let {
			Text(
				text = it,
				style = AioTheme.mediumTypography.base.copy(color = AioTheme.neutralColor.black)
			)
		}
		
		Row(
			horizontalArrangement = Arrangement.Start,
			verticalAlignment = Alignment.CenterVertically
		) {
			leadingIcon?.invoke()
			OutlinedTextField(
				modifier = Modifier
					.focusRequester(focusRequester)
					.fillMaxWidth(),
				value = value,
				shape = shape,
				onValueChange = onValueChange,
				enabled = enabled,
				readOnly = readOnly,
				textStyle = style,
				placeholder = placeholder,
				leadingIcon = leadingIcon,
				trailingIcon = trailingIcon,
				keyboardActions = keyboardActions,
				keyboardOptions = keyboardOptions,
				singleLine = singleLine,
				maxLines = maxLines,
				minLines = minLines,
				colors = colors,
				interactionSource = interactionSource,
				visualTransformation = visualTransformation
			)
			trailingIcon?.invoke()
		}
		
		errorMessage?.let {
			Row(
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.Start
			) {
				Icon(
					tint = AioTheme.errorColor.base,
					painter = painterResource(id = R.drawable.warning_fill),
					contentDescription = ""
				)
				Spacer(modifier = Modifier.width(5.dp))
				Text(
					text = it,
					style = AioTheme.mediumTypography.base.copy(color = AioTheme.errorColor.base)
				)
			}
			
		}
	}
}

object BasicTextFieldDefault {
	val MinHeight = 54.dp
}

@Preview
@Composable
private fun PreviewAioTextField() {
	var x by remember {
		mutableStateOf("")
	}
	AioComposeTheme {
		AioTextForm(
			modifier = Modifier.padding(15.dp),
			label = "Email String",
			errorMessage = "Email not valid",
			textColor = AioTheme.neutralColor.black,
			value = x,
			onValueChange = { x = it }
		)
	}
}