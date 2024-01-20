package com.notes.aionote.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.notes.aionote.ui.theme.AioTheme

@Composable
fun AioCornerCard(
	modifier: Modifier = Modifier,
	shape: RoundedCornerShape = RoundedCornerShape(8.dp),
	contentPadding: PaddingValues = PaddingValues(0.dp),
	elevationItem: Dp = 3.dp,
	content: @Composable BoxScope.() -> Unit
) {
	Box(
		modifier = Modifier
			.padding(contentPadding)
			.shadow(elevation = elevationItem, shape = shape)
			.background(color = AioTheme.neutralColor.white, shape = shape)
			.then(modifier),
		contentAlignment = Alignment.Center,
	) {
		content()
	}
	
}