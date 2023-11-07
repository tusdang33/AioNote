package com.notes.aionote.presentation.setting.edit_profile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.notes.aionote.R
import com.notes.aionote.collectInLaunchedEffectWithLifecycle
import com.notes.aionote.grantReadPermissionToUri
import com.notes.aionote.presentation.note.normal_note.NoteEvent
import com.notes.aionote.ui.component.AioActionBar
import com.notes.aionote.ui.component.AioAlertDialog
import com.notes.aionote.ui.component.AioButton
import com.notes.aionote.ui.component.AioTextForm
import com.notes.aionote.ui.theme.AioComposeTheme
import com.notes.aionote.ui.theme.AioTheme

@Composable
fun EditProfileRoute(
	onBackClick: () -> Unit,
	editProfileViewModel: EditProfileViewModel = hiltViewModel()
) {
	val editProfileUiState by editProfileViewModel.uiState.collectAsStateWithLifecycle()
	val context = LocalContext.current
	
	EditProfileScreen(
		modifier = Modifier.fillMaxSize(),
		editProfileUiState = editProfileUiState,
		onEvent = editProfileViewModel::onEvent,
		onBackClick = onBackClick
	)
	
	val imageLauncher = rememberLauncherForActivityResult(
		contract = ActivityResultContracts.PickVisualMedia()
	) { uri ->
		uri?.let {
			grantReadPermissionToUri(context, it)
			editProfileViewModel.onEvent(EditProfileEvent.AddImage(it.toString()))
		}
	}
	
	editProfileViewModel.oneTimeEvent.collectInLaunchedEffectWithLifecycle { oneTimeEvent ->
		when (oneTimeEvent) {
			EditProfileOneTimeEvent.Loading -> {}
			EditProfileOneTimeEvent.OnImageChange -> {
				imageLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
			}
			EditProfileOneTimeEvent.Success -> {}
		}
	}
}

@Composable
fun EditProfileScreen(
	modifier: Modifier = Modifier,
	editProfileUiState: EditProfileUiState,
	onEvent: (EditProfileEvent) -> Unit,
	onBackClick: () -> Unit
) {
	AnimatedVisibility(
		visible = editProfileUiState.isSubmitSuccess != null,
		enter = fadeIn(animationSpec = tween(0)),
		exit = fadeOut(animationSpec = tween(0))
	) {
		AioAlertDialog(
			titleAlertText = when (editProfileUiState.isSubmitSuccess) {
				true -> stringResource(id = R.string.edit_success)
				false -> stringResource(id = R.string.edit_fail)
				else -> null
			},
			onDismiss = { onEvent(EditProfileEvent.OnDismissDialog) },
			yesText = "OK",
			onYes = { onEvent(EditProfileEvent.OnDismissDialog) },
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
			Text(text = stringResource(id = R.string.edit_profile))
		}
		
		Image(
			modifier = Modifier
				.size(120.dp)
				.clip(CircleShape),
			painter = if (editProfileUiState.image != null)
				rememberAsyncImagePainter(model = editProfileUiState.image)
			else painterResource(id = R.drawable.man),
			contentDescription = null,
			contentScale = ContentScale.Fit
		
		)
		
		AioButton(
			minHeight = 38.dp,
			borderColor = AioTheme.primaryColor.base,
			enableColor = Color.Transparent,
			onClick = {
				onEvent(EditProfileEvent.OnChangeImage)
			}
		) {
			Row(
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.spacedBy(8.dp)
			) {
				Icon(
					painter = painterResource(id = R.drawable.pencil_alt_outline),
					contentDescription = ""
				)
				Text(text = stringResource(id = R.string.change_image))
			}
		}
		
		Spacer(modifier = Modifier.height(18.dp))
		
		Divider(modifier = Modifier.fillMaxWidth(), color = AioTheme.neutralColor.base)
		
		AioTextForm(
			label = stringResource(id = R.string.full_name),
			value = editProfileUiState.name ?: "", onValueChange = {
				onEvent(EditProfileEvent.OnNameChange(it))
			}
		)
		
		AioTextForm(
			enabled = false,
			label = stringResource(id = R.string.email),
			value = editProfileUiState.email, onValueChange = {
				onEvent(EditProfileEvent.OnEmailChange(it))
			},
			errorMessage = editProfileUiState.emailErrorMessage
		)
		
		Spacer(modifier = Modifier.height(12.dp))
		
		Box(
			modifier = Modifier
				.weight(1f)
				.fillMaxWidth(),
			contentAlignment = Alignment.Center
		) {
			AioButton(
				modifier = Modifier.fillMaxWidth(),
				leadingIcon = {
					Icon(
						painter = painterResource(id = R.drawable.check_outline),
						contentDescription = null,
						tint = AioTheme.neutralColor.white
					)
				},
				onClick = {
					onEvent(EditProfileEvent.OnSubmit)
				}
			) {
				Text(
					text = stringResource(id = R.string.save_change),
					style = AioTheme.mediumTypography.base.copy(color = AioTheme.neutralColor.white)
				)
			}
		}
	}
}

@Preview
@Composable
private fun PreviewEditProfileScreen() {
	AioComposeTheme {
		EditProfileScreen(editProfileUiState = EditProfileUiState(), onBackClick = {}, onEvent = {})
	}
}