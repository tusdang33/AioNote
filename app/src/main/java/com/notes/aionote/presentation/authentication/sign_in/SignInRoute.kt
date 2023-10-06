package com.notes.aionote.presentation.authentication.sign_in

import android.app.Activity.RESULT_OK
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.notes.aionote.R
import com.notes.aionote.collectInLaunchedEffectWithLifecycle
import com.notes.aionote.presentation.authentication.components.AioAuthButton
import com.notes.aionote.presentation.authentication.components.AioAuthHeader
import com.notes.aionote.ui.component.AioIconButton
import com.notes.aionote.ui.component.AioTextForm
import com.notes.aionote.ui.theme.AioComposeTheme
import com.notes.aionote.ui.theme.AioTheme

@Composable
fun SignInRoute(
	navigateToHome: () -> Unit,
	navigateToSignUp: () -> Unit,
	snackbarHostState: SnackbarHostState,
	signInViewModel: SignInViewModel = hiltViewModel()
) {
	val signInUiState by signInViewModel.uiState.collectAsStateWithLifecycle()
	
	SignInScreen(
		modifier = Modifier
			.background(AioTheme.neutralColor.white)
			.fillMaxWidth()
			.padding(horizontal = 16.dp),
		signInUiState = signInUiState,
		onEvent = signInViewModel::onEvent,
		navigateToSignUp = navigateToSignUp
	)
	
	val launcher = rememberLauncherForActivityResult(
		contract = ActivityResultContracts.StartIntentSenderForResult(),
		onResult = { result ->
			if (result.resultCode == RESULT_OK) {
				signInViewModel.onEvent(
					SignInEvent.OnLoginByGoogle(
						result.data ?: return@rememberLauncherForActivityResult
					)
				)
			}
		}
	)
	
	signInViewModel.oneTimeEvent.collectInLaunchedEffectWithLifecycle {signInOneTimeEvent ->
		when (signInOneTimeEvent) {
			is SignInOneTimeEvent.Fail -> {
				signInOneTimeEvent.errorMessage?.let {
					snackbarHostState.showSnackbar(
						it
					)
				}
			}
			
			is SignInOneTimeEvent.Loading -> {
//			AioLoading()
			}
			
			is SignInOneTimeEvent.Success -> {
				navigateToHome.invoke()
			}
			
			is SignInOneTimeEvent.GetLoginIntent -> {
				launcher.launch(
					IntentSenderRequest.Builder(
						signInOneTimeEvent.intentSender
					).build()
				)
			}
		}
	}
}

@Composable
fun SignInScreen(
	modifier: Modifier = Modifier,
	signInUiState: SignInUiState,
	onEvent: (SignInEvent) -> Unit,
	navigateToSignUp: () -> Unit,
) {
	val scrollState = rememberScrollState()
	
	Column(
		modifier = modifier.verticalScroll(scrollState)
	) {
		Spacer(modifier = Modifier.height(110.dp))
		AioAuthHeader(
			title = stringResource(id = R.string.login),
			subTitle = stringResource(id = R.string.sign_in_title)
		)
		Spacer(modifier = Modifier.height(32.dp))
		SignInForm(
			signInUiState = signInUiState,
			onEvent = onEvent
		)
		Spacer(modifier = Modifier.height(20.dp))
		AioAuthButton(
			isLoading = signInUiState.isLoading,
			onLoginDefault = { onEvent.invoke(SignInEvent.OnLoginDefault) },
			onLoginGoogle = { onEvent.invoke(SignInEvent.OnGetGoogleIntent) },
			onAltChoice = navigateToSignUp,
			defaultText = stringResource(id = R.string.login),
			googleText = stringResource(id = R.string.login_with_google),
			altText = stringResource(id = R.string.sign_in_register)
		)
	}
}

@Composable
fun SignInForm(
	modifier: Modifier = Modifier,
	signInUiState: SignInUiState,
	onEvent: (SignInEvent) -> Unit
) {
	Column(
		modifier = modifier,
		verticalArrangement = Arrangement.spacedBy(12.dp),
		horizontalAlignment = Alignment.Start
	) {
		Column(
			verticalArrangement = Arrangement.spacedBy(32.dp),
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			AioTextForm(
				errorMessage = signInUiState.emailError,
				label = stringResource(id = R.string.email),
				value = signInUiState.email, onValueChange = {
					onEvent.invoke(SignInEvent.OnEmailChange(it))
				},
			)
			
			AioTextForm(
				errorMessage = signInUiState.passwordError,
				label = stringResource(id = R.string.password),
				visualTransformation = if (signInUiState.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
				value = signInUiState.password, onValueChange = {
					onEvent.invoke(SignInEvent.OnPasswordChange(it))
				},
				trailingIcon = {
					AioIconButton(
						onClick = {
							onEvent(SignInEvent.OnPasswordVisibleChange(!signInUiState.isPasswordVisible))
						}
					) {
						Icon(
							painter = painterResource(
								id = if (signInUiState.isPasswordVisible)
									R.drawable.eye_on_fill else R.drawable.eye_off_fill
							),
							contentDescription = ""
						)
					}
				}
			)
		}
//		Text(
//			text = stringResource(id = R.string.forgot_pass),
//			style = AioTheme.mediumTypography.base.copy(color = AioTheme.primaryColor.base)
//		)
	}
}

@Preview(showBackground = true)
@Composable
private fun SignInScreen() {
	AioComposeTheme {
		SignInScreen(
			signInUiState = SignInUiState(),
			onEvent = {},
			navigateToSignUp = {}
		)
	}
}

