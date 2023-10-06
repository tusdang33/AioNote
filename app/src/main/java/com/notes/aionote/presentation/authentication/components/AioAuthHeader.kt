package com.notes.aionote.presentation.authentication.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.notes.aionote.ui.theme.AioComposeTheme
import com.notes.aionote.ui.theme.AioTheme

@Composable
fun AioAuthHeader(
	modifier: Modifier = Modifier,
	title: String,
	subTitle: String,
	titleStyle: TextStyle = AioTheme.boldTypography.xl2,
	subTitleStyle: TextStyle = AioTheme.regularTypography.base,
) {
	Column(
		modifier = modifier,
		horizontalAlignment = Alignment.Start
	) {
		Text(text = title, style = titleStyle)
		Spacer(modifier = Modifier.height(16.dp))
		Text(
			text = subTitle,
			style = subTitleStyle.copy(color = AioTheme.neutralColor.dark)
		)
	}
}

@Preview
@Composable
private fun PreviewAioAuthHeader() {
	AioComposeTheme {
		AioAuthHeader(title = "Login", subTitle = "Ok lets login")
	}
}