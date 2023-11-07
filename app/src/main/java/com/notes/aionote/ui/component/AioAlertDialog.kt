package com.notes.aionote.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.notes.aionote.ui.theme.AioComposeTheme
import com.notes.aionote.ui.theme.AioTheme

@Composable
fun AioAlertDialog(
	onDismiss: () -> Unit,
	modifier: Modifier = Modifier,
	shape: Shape = RoundedCornerShape(16.dp),
	alertSize: Dp = 280.dp,
	titleAlertText: String? = null,
	contentAlertText: String? = null,
	yesText: String = "Yes",
	noText: String = "No",
	onYes: (() -> Unit)? = null,
	onNo: (() -> Unit)? = null
) {
	Dialog(
		onDismissRequest = onDismiss,
		properties = DialogProperties(
			dismissOnBackPress = false,
			dismissOnClickOutside = false
		),
	) {
		
		Column(
			modifier = modifier
				.width(alertSize)
				.wrapContentHeight()
				.clip(shape)
				.background(AioTheme.neutralColor.white)
				.padding(horizontal = 24.dp, vertical = 32.dp),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.spacedBy(8.dp),
		) {
			if (titleAlertText != null) {
				Text(
					modifier = Modifier.fillMaxWidth(),
					text = titleAlertText,
					textAlign = TextAlign.Center,
					style = AioTheme.boldTypography.lg
				)
			}
			if (contentAlertText != null) {
				Text(
					modifier = Modifier.fillMaxWidth(),
					text = contentAlertText,
					textAlign = TextAlign.Center,
					style = AioTheme.regularTypography.base.copy(color = AioTheme.neutralColor.dark)
				)
			}
			
			Spacer(modifier = Modifier.height(16.dp))
			
			Row(
				modifier = Modifier.fillMaxWidth(),
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.SpaceEvenly
			) {
				onYes?.let {
					AioButton(
						minHeight = 38.dp,
						onClick = it
					) {
						Text(
							text = yesText,
							style = AioTheme.mediumTypography.base.copy(color = AioTheme.neutralColor.white)
						)
					}
				}
				
				onNo?.let {
					AioButton(
						enableColor = Color.Transparent,
						borderColor = AioTheme.primaryColor.base,
						minHeight = 38.dp,
						onClick = it
					) {
						Text(
							text = noText,
							style = AioTheme.mediumTypography.base.copy(color = AioTheme.primaryColor.base)
						)
					}
				}
			}
		}
	}
}

@Preview
@Composable
private fun PreviewAioAlertDialog() {
	AioComposeTheme {
		AioAlertDialog(
			titleAlertText = "Title",
			contentAlertText = "Content this is the content",
			onYes = {},
			onNo = {},
			onDismiss = { })
	}
}