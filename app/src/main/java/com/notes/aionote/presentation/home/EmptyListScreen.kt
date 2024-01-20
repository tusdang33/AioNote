package com.notes.aionote.presentation.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.notes.aionote.R
import com.notes.aionote.ui.theme.AioTheme

@Composable
fun EmptyListScreen() {
	Column(
		modifier = Modifier.fillMaxWidth(),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.spacedBy(18.dp)
	) {
		Image(painter = painterResource(id = R.drawable.empty_human), contentDescription = null)
		Text(
			text = stringResource(id = R.string.start_your_journey),
			style = AioTheme.boldTypography.xl
		)
		Text(
			text = stringResource(id = R.string.start_your_journey_content),
			style = AioTheme.regularTypography.sm,
			textAlign = TextAlign.Center
		)
		Image(painter = painterResource(id = R.drawable.empty_arrow), contentDescription = null)
	}
}

@Preview
@Composable
private fun PreviewEmptyListScreen() {
	EmptyListScreen()
}