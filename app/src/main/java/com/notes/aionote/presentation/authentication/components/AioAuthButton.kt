package com.notes.aionote.presentation.authentication.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.notes.aionote.R
import com.notes.aionote.ui.component.AioButton
import com.notes.aionote.ui.component.AioLoading
import com.notes.aionote.ui.theme.AioTheme

@Composable
fun AioAuthButton(
	modifier: Modifier = Modifier,
	isLoading: Boolean = false,
	onLoginDefault: () -> Unit,
	onLoginGoogle: () -> Unit,
	onAltChoice: () -> Unit,
	defaultText: String,
	googleText: String,
	altText: String,
) {
	var defaultLoginLoading by remember {
		mutableStateOf(false)
	}
	
	var googleLoginLoading by remember {
		mutableStateOf(false)
	}
	
	LaunchedEffect(isLoading) {
		if(!isLoading) {
			defaultLoginLoading = false
			googleLoginLoading = false
		}
	}
	
	
	Column(
		modifier = modifier,
		verticalArrangement = Arrangement.spacedBy(32.dp),
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		Column(
			verticalArrangement = Arrangement.spacedBy(16.dp),
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			AioButton(
				enableColor = AioTheme.primaryColor.base,
				trailingIcon = {
					if(!defaultLoginLoading)
					Icon(
						tint = AioTheme.neutralColor.white,
						painter = painterResource(id = R.drawable.arrow2_right),
						contentDescription = ""
					)
				},
				onClick = {
					defaultLoginLoading = true
					onLoginDefault.invoke()
				}
			) {
				if (defaultLoginLoading) {
					AioLoading(
						circleSize = 5.dp,
						circleColor = AioTheme.neutralColor.white
					)
				} else {
					Text(
						text = defaultText,
						style = AioTheme.mediumTypography.base.copy(color = AioTheme.neutralColor.white)
					)
				}
			}
			Box(modifier = Modifier, contentAlignment = Alignment.Center) {
				Divider(modifier = Modifier.fillMaxWidth())
				Text(
					text = "Or",
					style = AioTheme.mediumTypography.xs2.copy(color = AioTheme.neutralColor.dark),
					modifier = Modifier
						.background(AioTheme.neutralColor.white)
						.padding(5.dp)
				)
			}
			AioButton(
				borderColor = AioTheme.neutralColor.black,
				enableColor = AioTheme.neutralColor.white,
				onClick = {
					googleLoginLoading = true
					onLoginGoogle.invoke()
				}
			) {
				if (googleLoginLoading) {
					AioLoading(
						circleSize = 5.dp,
						circleColor = AioTheme.primaryColor.base
					)
				} else {
					Row(
						verticalAlignment = Alignment.CenterVertically,
						horizontalArrangement = Arrangement.Center
					) {
						Image(
							modifier = Modifier.size(24.dp),
							painter = painterResource(id = R.drawable.gg_logo),
							contentDescription = ""
						)
						Spacer(modifier = Modifier.width(10.dp))
						Text(text = googleText, style = AioTheme.mediumTypography.base)
					}
				}
			}
		}
		Text(
			modifier = Modifier
				.clip(RoundedCornerShape(8.dp))
				.clickable { onAltChoice.invoke() }
				.padding(8.dp),
			text = altText,
			style = AioTheme.mediumTypography.base.copy(color = AioTheme.primaryColor.base)
		)
	}
}
