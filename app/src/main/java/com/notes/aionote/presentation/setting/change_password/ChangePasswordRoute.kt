package com.notes.aionote.presentation.setting.change_password

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.notes.aionote.ui.component.AioActionBar
import com.notes.aionote.ui.component.AioAlertDialog
import com.notes.aionote.ui.component.AioButton
import com.notes.aionote.ui.component.AioIconButton
import com.notes.aionote.ui.component.AioTextForm
import com.notes.aionote.ui.theme.AioComposeTheme
import com.notes.aionote.ui.theme.AioTheme

@Composable
fun ChangePasswordRoute(
	onBackClick: () -> Unit,
	changePasswordViewModel: ChangePasswordViewModel = hiltViewModel()
) {
	val changePasswordUiState by changePasswordViewModel.uiState.collectAsStateWithLifecycle()
	
	ChangePasswordScreen(
		modifier = Modifier.fillMaxSize(),
		changePasswordUiState = changePasswordUiState,
		onEvent = changePasswordViewModel::onEvent,
		onBackClick = onBackClick
	)
}

@Composable
fun ChangePasswordScreen(
	modifier: Modifier = Modifier,
	changePasswordUiState: ChangePasswordUiState,
	onEvent: (ChangePasswordEvent) -> Unit,
	onBackClick: () -> Unit
) {
	
	AnimatedVisibility(
		visible = changePasswordUiState.isSubmitSuccess != null,
		enter = fadeIn(animationSpec = tween(0)),
		exit = fadeOut(animationSpec = tween(0))
	) {
		AioAlertDialog(
			titleAlertText = when (changePasswordUiState.isSubmitSuccess) {
				true -> stringResource(id = R.string.change_pass_success)
				false -> stringResource(id = R.string.change_pass_fail)
				else -> null
			},
			onDismiss = { onEvent(ChangePasswordEvent.OnDismissDialog) },
			yesText = "OK",
			onYes = { onEvent(ChangePasswordEvent.OnDismissDialog) },
		)
	}
	val scrollState = rememberScrollState()
	Column(
		modifier = modifier
			.verticalScroll(scrollState)
			.background(AioTheme.neutralColor.white)
			.padding(16.dp),
		verticalArrangement = Arrangement.spacedBy(16.dp),
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		AioActionBar(
			modifier = Modifier.layout { measurable, constraints ->
				val placeable = measurable.measure(constraints.copy(maxWidth = constraints.maxWidth + 40.dp.roundToPx()))
				layout(placeable.width, placeable.height) {
					placeable.place(0, 0)
				}
			},
			leadingIconClick = onBackClick
		) {
			Text(text = stringResource(id = R.string.change_pass))
		}
		
		Spacer(modifier = Modifier.height(18.dp))
		
		Box(modifier = Modifier.fillMaxWidth()) {
			Text(
				text = stringResource(id = R.string.current_pass_label),
				style = AioTheme.mediumTypography.xs2.copy(color = AioTheme.primaryColor.base)
			)
		}
		
		
		AioTextForm(
			label = stringResource(id = R.string.current_pass),
			value = changePasswordUiState.currentPass,
			onValueChange = {
				onEvent(ChangePasswordEvent.OnCurrentPassChange(it))
			},
			visualTransformation = if (changePasswordUiState.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
			errorMessage = changePasswordUiState.currentPassErrorMessage,
			trailingIcon = {
				AioIconButton(
					onClick = {
						onEvent(ChangePasswordEvent.OnPasswordVisibleChange(!changePasswordUiState.isPasswordVisible))
					}
				) {
					Icon(
						painter = painterResource(
							id = if (changePasswordUiState.isPasswordVisible)
								R.drawable.eye_on_fill else R.drawable.eye_off_fill
						),
						contentDescription = ""
					)
				}
			}
		)
		
		Spacer(modifier = Modifier.height(16.dp))
		
		Box(modifier = Modifier.fillMaxWidth()) {
			Text(
				text = stringResource(id = R.string.new_pass_label),
				style = AioTheme.mediumTypography.xs2.copy(color = AioTheme.primaryColor.base)
			)
		}
		
		
		AioTextForm(
			label = stringResource(id = R.string.new_pass),
			value = changePasswordUiState.newPass,
			onValueChange = {
				onEvent(ChangePasswordEvent.OnNewPassChange(it))
			},
			visualTransformation = if (changePasswordUiState.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
			errorMessage = changePasswordUiState.newPassErrorMessage,
			trailingIcon = {
				AioIconButton(
					onClick = {
						onEvent(ChangePasswordEvent.OnPasswordVisibleChange(!changePasswordUiState.isPasswordVisible))
					}
				) {
					Icon(
						painter = painterResource(
							id = if (changePasswordUiState.isPasswordVisible)
								R.drawable.eye_on_fill else R.drawable.eye_off_fill
						),
						contentDescription = ""
					)
				}
			}
		)
		
		AioTextForm(
			label = stringResource(id = R.string.re_new_pass),
			value = changePasswordUiState.retypePass,
			onValueChange = {
				onEvent(ChangePasswordEvent.OnRetypePassChange(it))
			},
			visualTransformation = if (changePasswordUiState.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
			errorMessage = changePasswordUiState.retypePassErrorMessage,
			trailingIcon = {
				AioIconButton(
					onClick = {
						onEvent(ChangePasswordEvent.OnPasswordVisibleChange(!changePasswordUiState.isPasswordVisible))
					}
				) {
					Icon(
						painter = painterResource(
							id = if (changePasswordUiState.isPasswordVisible)
								R.drawable.eye_on_fill else R.drawable.eye_off_fill
						),
						contentDescription = ""
					)
				}
			}
		)
		
		Spacer(modifier = Modifier.height(16.dp))
		
		Box(
			modifier = Modifier
				.weight(1f)
				.fillMaxWidth(),
			contentAlignment = Alignment.Center
		) {
			AioButton(
				modifier = Modifier.fillMaxWidth(),
				trailingIcon = {
					Icon(
						painter = painterResource(id = R.drawable.arrow2_right),
						contentDescription = null,
						tint = AioTheme.neutralColor.white
					)
				},
				onClick = {
					onEvent(ChangePasswordEvent.OnSubmit)
				}
			) {
				Text(
					text = stringResource(id = R.string.submit_new_pass),
					style = AioTheme.mediumTypography.base.copy(color = AioTheme.neutralColor.white)
				)
			}
		}
	}
}

@Preview
@Composable
private fun PreviewChangePassWordScreen() {
	AioComposeTheme {
		ChangePasswordScreen(
			changePasswordUiState = ChangePasswordUiState(),
			onBackClick = {},
			onEvent = {})
	}
}