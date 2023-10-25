package com.notes.aionote.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

internal val AioPrimaryBaseColor = Color(0xFF6A3EA1)
internal val AioPrimaryDarkColor = Color(0xFF3A2258)
internal val AioPrimaryLightColor = Color(0xFFEFE9F7)
internal val AioPrimaryBackgroundColor = Color(0xFFFAF8FC)

internal val AioSecondaryBaseColor = Color(0xFFDEDC52)
internal val AioSecondaryDarkColor = Color(0xFF565510)
internal val AioSecondaryLightColor = Color(0xFFF7F6D4)

internal val AioSuccessBaseColor = Color(0xFF60D889)
internal val AioSuccessDarkColor = Color(0xFF1F7F40)
internal val AioSuccessLightColor = Color(0xFFDAF6E4)

internal val AioErrorBaseColor = Color(0xFFCE3A54)
internal val AioErrorDarkColor = Color(0xFF5A1623)
internal val AioErrorLightColor = Color(0xFFF7DEE3)

internal val AioWarningBaseColor = Color(0xFFF8C715)
internal val AioWarningDarkColor = Color(0xFF725A03)
internal val AioWarningLightColor = Color(0xFFFDEBAB)

internal val AioNeutralBaseColor = Color(0xFFC8C5CB)
internal val AioNeutralDarkColor = Color(0xFF827D89)
internal val AioNeutralLightColor = Color(0xFFF7F7F7)
internal val AioNeutralBlackColor = Color(0xFF180E25)
internal val AioNeutralWhiteColor = Color(0xFFFFFFFF)

@Immutable
data class AioColor(
	val base: Color,
	val dark: Color,
	val light: Color,
	val background: Color = Color.Unspecified,
	val black: Color = Color.Unspecified,
	val white: Color = Color.Unspecified
)

//Local
val LocalAioColor = staticCompositionLocalOf {
	AioColor(
		base = Color.Unspecified,
		dark = Color.Unspecified,
		light = Color.Unspecified,
		background = Color.Unspecified,
		black = Color.Unspecified,
		white = Color.Unspecified,
	)
}

object AioThemeColor {
	val primaryAioColor = AioColor(
		base = AioPrimaryBaseColor,
		dark = AioPrimaryDarkColor,
		light = AioPrimaryLightColor,
		background = AioPrimaryBackgroundColor,
	)
	
	val secondaryAioColor = AioColor(
		base = AioSecondaryBaseColor,
		dark = AioSecondaryDarkColor,
		light = AioSecondaryLightColor,
	)
	val successAioColor = AioColor(
		base = AioSuccessBaseColor,
		dark = AioSuccessDarkColor,
		light = AioSuccessLightColor,
	)
	val errorAioColor = AioColor(
		base = AioErrorBaseColor,
		dark = AioErrorDarkColor,
		light = AioErrorLightColor,
	)
	val warningAioColor = AioColor(
		base = AioWarningBaseColor,
		dark = AioWarningDarkColor,
		light = AioWarningLightColor,
	)
	val neutralAioColor = AioColor(
		base = AioNeutralBaseColor,
		dark = AioNeutralDarkColor,
		light = AioNeutralLightColor,
		black = AioNeutralBlackColor,
		white = AioNeutralWhiteColor,
	)
}