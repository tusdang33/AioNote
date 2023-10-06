package com.notes.aionote.presentation.authentication.sign_up

import android.app.Activity
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.notes.aionote.R
import com.notes.aionote.presentation.authentication.components.AioAuthButton
import com.notes.aionote.presentation.authentication.components.AioAuthHeader
import com.notes.aionote.ui.component.AioActionBar
import com.notes.aionote.ui.component.AioIconButton
import com.notes.aionote.ui.component.AioTextForm
import com.notes.aionote.ui.theme.AioComposeTheme
import com.notes.aionote.ui.theme.AioTheme

@Composable
fun SignUpRoute(
	navigateToSignIn: () -> Unit,
	navigateToHome: () -> Unit,
	snackbarHostState: SnackbarHostState,
	signUpViewModel: SignUpViewModel = hiltViewModel()
) {
	val signUpUiState by signUpViewModel.uiState.collectAsStateWithLifecycle()
	val signUpOneTimeEvent by signUpViewModel.oneTimeEvent.collectAsStateWithLifecycle(initialValue = null)
	SignUpScreen(
		modifier = Modifier
			.background(AioTheme.neutralColor.white)
			.fillMaxWidth()
			.padding(horizontal = 16.dp),
		signUpUiState = signUpUiState,
		onEvent = signUpViewModel::onEvent,
		navigateToSignIn = navigateToSignIn
	)
	
	val launcher = rememberLauncherForActivityResult(
		contract = ActivityResultContracts.StartIntentSenderForResult(),
		onResult = { result ->
			if (result.resultCode == Activity.RESULT_OK) {
				signUpViewModel.onEvent(
					SignUpEvent.OnLoginByGoogle(
						result.data ?: return@rememberLauncherForActivityResult
					)
				)
			}
		}
	)
	
	LaunchedEffect(signUpOneTimeEvent) {
		if (signUpOneTimeEvent is SignUpOneTimeEvent.Fail) {
			(signUpOneTimeEvent as SignUpOneTimeEvent.Fail).errorMessage?.let {
				snackbarHostState.showSnackbar(
					it
				)
			}
		}
	}
	
	when (signUpOneTimeEvent) {
		SignUpOneTimeEvent.Loading -> {
//			AioLoading()
		}
		
		SignUpOneTimeEvent.Success -> {
			navigateToHome.invoke()
		}
		
		is SignUpOneTimeEvent.GetLoginIntent -> {
			launcher.launch(
				IntentSenderRequest.Builder(
					(signUpOneTimeEvent as SignUpOneTimeEvent.GetLoginIntent).intentSender
				).build()
			)
		}
		
		else -> {/*noop*/
		}
	}
}

@Composable
fun SignUpScreen(
	modifier: Modifier = Modifier,
	signUpUiState: SignUpUiState,
	onEvent: (SignUpEvent) -> Unit,
	navigateToSignIn: () -> Unit,
) {
	val scrollState = rememberScrollState()
	
	Column(
		modifier = modifier.verticalScroll(scrollState),
		horizontalAlignment = Alignment.Start,
	) {
		AioActionBar(
			modifier = Modifier.layout { measurable, constraints ->
				val placeable = measurable.measure(constraints.copy(maxWidth = constraints.maxWidth + 40.dp.roundToPx()))
				layout(placeable.width, placeable.height) {
					placeable.place(0, 0)
				}
			},
			leadingIconClick = navigateToSignIn
		) { /* noop */ }
		Spacer(modifier = Modifier.height(32.dp))
		AioAuthHeader(
			title = stringResource(id = R.string.sign_up_title),
			subTitle = stringResource(id = R.string.sign_up_sub_title)
		)
		Spacer(modifier = Modifier.height(32.dp))
		SignUpForm(signUpUiState = signUpUiState, onEvent = onEvent)
		Spacer(modifier = Modifier.height(40.dp))
		AioAuthButton(
			isLoading = signUpUiState.isLoading,
			onLoginDefault = { onEvent.invoke(SignUpEvent.OnSignUpByDefault) },
			onLoginGoogle = { onEvent.invoke(SignUpEvent.OnGetGoogleIntent) },
			onAltChoice = navigateToSignIn,
			defaultText = stringResource(id = R.string.sign_up_title),
			googleText = stringResource(id = R.string.register_with_google),
			altText = stringResource(id = R.string.sign_up_login)
		)
	}
}

@Composable
fun SignUpForm(
	modifier: Modifier = Modifier,
	signUpUiState: SignUpUiState,
	onEvent: (SignUpEvent) -> Unit
) {
	Column(
		modifier = modifier,
		verticalArrangement = Arrangement.spacedBy(32.dp),
		horizontalAlignment = Alignment.Start
	) {
		AioTextForm(
			label = stringResource(id = R.string.full_name),
			value = signUpUiState.fullName, onValueChange = {
				onEvent.invoke(SignUpEvent.OnFullNameChange(it))
			}
		)
		
		AioTextForm(
			errorMessage = signUpUiState.emailError,
			label = stringResource(id = R.string.email),
			value = signUpUiState.email, onValueChange = {
				onEvent.invoke(SignUpEvent.OnEmailChange(it))
			}
		)
		
		AioTextForm(
			errorMessage = signUpUiState.passwordError,
			label = stringResource(id = R.string.password),
			visualTransformation = if (signUpUiState.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
			value = signUpUiState.password, onValueChange = {
				onEvent.invoke(SignUpEvent.OnPasswordChange(it))
			},
			trailingIcon = {
				AioIconButton(
					onClick = {
						onEvent(SignUpEvent.OnPasswordVisibleChange(!signUpUiState.isPasswordVisible))
					}
				) {
					Icon(
						painter = painterResource(
							id = if (signUpUiState.isPasswordVisible)
								R.drawable.eye_on_fill else R.drawable.eye_off_fill
						),
						contentDescription = ""
					)
				}
			}
		)
		
		AioTextForm(
			errorMessage = signUpUiState.retypePasswordError,
			label = stringResource(id = R.string.retype_pass),
			visualTransformation = if (signUpUiState.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
			value = signUpUiState.retypePassword, onValueChange = {
				onEvent.invoke(SignUpEvent.OnRetypePasswordChange(it))
			},
			trailingIcon = {
				AioIconButton(
					onClick = {
						onEvent(SignUpEvent.OnPasswordVisibleChange(!signUpUiState.isPasswordVisible))
					}
				) {
					Icon(
						painter = painterResource(
							id = if (signUpUiState.isPasswordVisible)
								R.drawable.eye_on_fill else R.drawable.eye_off_fill
						),
						contentDescription = ""
					)
				}
			}
		)
	}
}

@Preview(showSystemUi = true)
@Composable
private fun PreviewSignUpScreen() {
	AioComposeTheme {
		SignUpScreen(signUpUiState = SignUpUiState(), onEvent = {}) {
			
		}
	}
}