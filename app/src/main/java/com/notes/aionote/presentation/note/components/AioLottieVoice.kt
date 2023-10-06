package com.notes.aionote.presentation.note.components

import androidx.annotation.RawRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.notes.aionote.R
import com.notes.aionote.ui.theme.AioTheme

@Composable
fun AioLottieVoice(
	modifier: Modifier = Modifier,
	@RawRes lottieSource: Int = R.raw.animation_voice,
) {
	val lottieComposition by rememberLottieComposition(
		spec = LottieCompositionSpec.RawRes(
			lottieSource
		)
	)
	
	Row(modifier = modifier.background(AioTheme.neutralColor.white)) {
		repeat(2) {
			LottieAnimation(
				modifier = Modifier.weight(1f).heightIn(max = 60.dp),
				composition = lottieComposition,
				iterations = LottieConstants.IterateForever,
			)
		}
	}
}