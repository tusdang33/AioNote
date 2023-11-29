package com.notes.aionote.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

@Composable
fun AioComposeTheme(
	fontWeight: Int = 0,
	content: @Composable () -> Unit
) {
	CompositionLocalProvider(
		LocalAioColor provides AioThemeColor.primaryAioColor,
		LocalAioTypography provides when (fontWeight) {
			0 -> AioThemeTypography.aioRegularTypography
			1 -> AioThemeTypography.aioMediumTypography
			2 -> AioThemeTypography.aioBoldTypography
			else -> AioThemeTypography.aioRegularTypography
		},
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
		@Composable
		get() = when(LocalAioTypography.current) {
			AioThemeTypography.aioRegularTypography -> AioThemeTypography.aioMediumTypography
			AioThemeTypography.aioMediumTypography -> AioThemeTypography.aioBoldTypography
			AioThemeTypography.aioBoldTypography -> AioThemeTypography.aioExtraBoldTypography
			else -> AioThemeTypography.aioMediumTypography
		}
	
	val boldTypography: AioTypography
		@Composable
		get() = when(LocalAioTypography.current) {
			AioThemeTypography.aioRegularTypography -> AioThemeTypography.aioBoldTypography
			AioThemeTypography.aioMediumTypography -> AioThemeTypography.aioExtraBoldTypography
			AioThemeTypography.aioBoldTypography -> AioThemeTypography.aioBlackBoldTypography
			else -> AioThemeTypography.aioBoldTypography
		}
}