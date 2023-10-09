package com.notes.aionote.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.notes.aionote.R
import com.notes.aionote.ui.theme.AioComposeTheme
import com.notes.aionote.ui.theme.AioTheme

@Composable
fun AioIconButton(
	modifier: Modifier = Modifier,
	contentPaddingValues: PaddingValues = ButtonDefaults.TextButtonContentPadding,
	interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
	backgroundColor: Color = Color.Unspecified,
	onClick: () -> Unit,
	enabled: Boolean = true,
	content: @Composable () -> Unit,
) {
	Box(
		modifier = Modifier
			.requiredHeightIn(22.dp)
			.clip(RoundedCornerShape(15.dp))
			.background(backgroundColor)
			.then(modifier)
			.clickable(
				interactionSource = interactionSource,
				indication = rememberRipple(),
				enabled = enabled,
				role = Role.Button,
				onClick = onClick
			)
			.padding(contentPaddingValues),
		contentAlignment = Alignment.Center
	) {
		Row(
			horizontalArrangement = Arrangement.Center,
			verticalAlignment = Alignment.CenterVertically,
		) {
			CompositionLocalProvider(LocalContentColor provides if(enabled)AioTheme.primaryColor.base else AioTheme.neutralColor.base) {
				content()
			}
		}
	}
}

@Preview(showBackground = true)
@Composable
private fun PreviewAioButtonBack() {
	AioComposeTheme {
		Column() {
			AioIconButton(onClick = {}) {Image(
				painter = painterResource(id = R.drawable.arrow_left),
				contentDescription = ""
			)
				Spacer(modifier = Modifier.width(15.dp))
				Text(text = "Back", style = AioTheme.mediumTypography.base)}
			AioIconButton(onClick = {}) {Image(
				painter = painterResource(id = R.drawable.arrow_left),
				contentDescription = ""
			)
				Spacer(modifier = Modifier.width(15.dp))
				Text(text = "Back", style = AioTheme.mediumTypography.base)}
		}
	}
}