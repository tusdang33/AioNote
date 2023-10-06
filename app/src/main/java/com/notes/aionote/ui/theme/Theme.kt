package com.notes.aionote.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun AioComposeTheme(
	content: @Composable () -> Unit
) {
	CompositionLocalProvider(
		LocalAioColor provides AioThemeColor.primaryAioColor,
		LocalAioTypography provides AioThemeTypography.aioRegularTypography,
		content = content
	)
}

object AioTheme {
	val primaryColor: AioColor
		@Composable
		get() = LocalAioColor.current
	
	val regularTypography: AioTypography
		@Composable
		get() = LocalAioTypography.current
	
	val secondaryColor: AioColor
		get() = AioThemeColor.secondaryAioColor
	
	val successColor: AioColor
		get() = AioThemeColor.successAioColor
	
	val warningColor: AioColor
		get() = AioThemeColor.warningAioColor
	
	val neutralColor: AioColor
		get() = AioThemeColor.neutralAioColor
	
	val errorColor: AioColor
		get() = AioThemeColor.errorAioColor
	
	val mediumTypography: AioTypography
		get() = AioThemeTypography.aioMediumTypography
	
	val boldTypography: AioTypography
		get() = AioThemeTypography.aioBoldTypography
}