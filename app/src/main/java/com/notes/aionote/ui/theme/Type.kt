package com.notes.aionote.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.notes.aionote.R

internal val Inter = FontFamily(
	Font(R.font.inter_regular, FontWeight.Normal),
	Font(R.font.inter_medium, FontWeight.Medium),
	Font(R.font.inter_bold, FontWeight.Bold),
)

@Immutable
data class AioTypography(
	val xs: TextStyle,
	val xs2: TextStyle,
	val sm: TextStyle,
	val base: TextStyle,
	val lg: TextStyle,
	val xl: TextStyle,
	val xl2: TextStyle,
	val xl3: TextStyle,
)

val LocalAioTypography = staticCompositionLocalOf {
	AioTypography(
		xs = TextStyle.Default,
		xs2 = TextStyle.Default,
		sm = TextStyle.Default,
		base = TextStyle.Default,
		lg = TextStyle.Default,
		xl = TextStyle.Default,
		xl2 = TextStyle.Default,
		xl3 = TextStyle.Default,
	)
}

object AioThemeTypography {
	@Suppress("DEPRECATION")
	private fun providesTypography(fontWeight: FontWeight): AioTypography {
		return AioTypography(
			xs = TextStyle(
				fontFamily = Inter,
				fontSize = 10.sp,
				fontWeight = fontWeight,
				platformStyle = PlatformTextStyle(includeFontPadding = false)
			),
			xs2 = TextStyle(
				fontFamily = Inter,
				fontSize = 12.sp,
				fontWeight = fontWeight,
				platformStyle = PlatformTextStyle(includeFontPadding = false)
			),
			sm = TextStyle(
				fontFamily = Inter,
				fontSize = 14.sp,
				fontWeight = fontWeight,
				platformStyle = PlatformTextStyle(includeFontPadding = false)
			),
			base = TextStyle(
				fontFamily = Inter,
				fontSize = 16.sp,
				fontWeight = fontWeight,
				platformStyle = PlatformTextStyle(includeFontPadding = false)
			),
			lg = TextStyle(
				fontFamily = Inter,
				fontSize = 20.sp,
				fontWeight = fontWeight,
				platformStyle = PlatformTextStyle(includeFontPadding = false)
			),
			xl = TextStyle(
				fontFamily = Inter,
				fontSize = 24.sp,
				fontWeight = fontWeight,
				platformStyle = PlatformTextStyle(includeFontPadding = false)
			),
			xl2 = TextStyle(
				fontFamily = Inter,
				fontSize = 32.sp,
				fontWeight = fontWeight,
				platformStyle = PlatformTextStyle(includeFontPadding = false)
			),
			xl3 = TextStyle(
				fontFamily = Inter,
				fontSize = 40.sp,
				fontWeight = fontWeight,
				platformStyle = PlatformTextStyle(includeFontPadding = false)
			),
		)
	}
	
	val aioRegularTypography = providesTypography(FontWeight.Normal)
	val aioMediumTypography = providesTypography(FontWeight.Medium)
	val aioBoldTypography = providesTypography(FontWeight.Bold)
	val aioBlackBoldTypography = providesTypography(FontWeight.Black)
	val aioExtraBoldTypography = providesTypography(FontWeight.ExtraBold)
}