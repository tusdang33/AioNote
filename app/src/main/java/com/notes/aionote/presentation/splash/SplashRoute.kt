package com.notes.aionote.presentation.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.notes.aionote.R
import com.notes.aionote.ui.component.AioButton
import com.notes.aionote.ui.theme.AioComposeTheme
import com.notes.aionote.ui.theme.AioTheme

@Composable
fun SplashRoute(
	navigateToSignIn: () -> Unit,
	navigateToHome: () -> Unit,
	splashViewModel: SplashViewModel = hiltViewModel()
) {
	val splashEvent by splashViewModel.oneTimeEvent.collectAsStateWithLifecycle(initialValue = null)
	
	SideEffect {
		splashViewModel.onEvent(SplashEvent.CheckCurrentUser)
	}
	
	when (splashEvent) {
		is SplashOneTimeEvent.LoginSuccess -> navigateToHome.invoke()
		is SplashOneTimeEvent.LoginFail -> navigateToSignIn.invoke()
		else -> {/*noop*/
		}
	}
	
	SplashScreen(modifier = Modifier.fillMaxSize())
}

@Composable
fun SplashScreen(
	modifier: Modifier = Modifier
) {
	Surface(
		modifier = modifier
			.fillMaxSize()
			.background(AioTheme.primaryColor.base)
			.padding(horizontal = 40.dp)
	) {
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.background(AioTheme.primaryColor.base)
				.padding(top = 40.dp),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.Center
		) {
			Image(
				painter = painterResource(id = R.drawable.splash_ill),
				contentDescription = ""
			)
			Spacer(modifier = Modifier.height(120.dp))
			Text(
				text = stringResource(id = R.string.splash_title),
				style = AioTheme.boldTypography.lg.copy(color = AioTheme.neutralColor.white),
				textAlign = TextAlign.Center
			)
//			AioButton(
//				modifier = Modifier.fillMaxWidth(),
//				enableColor = AioTheme.neutralColor.white,
//				trailingIcon = {
//					Image(
//						painter = painterResource(id = R.drawable.arrow2_right),
//						contentDescription = ""
//					)
//				},
//				onClick = { /*TODO*/ }
//			) {
//				Text(
//					text = stringResource(id = R.string.splash_button),
//					style = AioTheme.mediumTypography.base.copy(color = AioTheme.primaryColor.base)
//				)
//			}
		}
	}
}

@Preview
@Composable
private fun PreviewSplashScreen() {
	AioComposeTheme {
		SplashScreen()
	}
}