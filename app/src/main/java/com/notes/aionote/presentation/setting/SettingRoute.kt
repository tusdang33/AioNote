package com.notes.aionote.presentation.setting

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.notes.aionote.R
import com.notes.aionote.collectInLaunchedEffectWithLifecycle
import com.notes.aionote.ui.component.AioActionBar
import com.notes.aionote.ui.component.AioButton
import com.notes.aionote.ui.theme.AioComposeTheme
import com.notes.aionote.ui.theme.AioTheme

@Composable
fun SettingRoute(
	navigateToSignIn: () -> Unit,
	navigateToEditProfile: (image: String?, name: String?, email: String) -> Unit,
	navigateToChangePassword: (email: String) -> Unit,
	onBackClick: () -> Unit,
	settingViewModel: SettingViewModel = hiltViewModel()
) {
	val settingUiState by settingViewModel.uiState.collectAsStateWithLifecycle()
	
	SettingScreen(
		modifier = Modifier.fillMaxSize(),
		settingUiState = settingUiState,
		onEvent = settingViewModel::onEvent,
		onBackClick = onBackClick
	)
	
	settingViewModel.oneTimeEvent.collectInLaunchedEffectWithLifecycle { settingOneTimeEvent ->
		when (settingOneTimeEvent) {
			is SettingOneTimeEvent.OnChangePassword -> {
				navigateToChangePassword.invoke(settingOneTimeEvent.email)
			}
			
			is SettingOneTimeEvent.OnEditProfile -> {
				navigateToEditProfile(
					settingOneTimeEvent.image,
					settingOneTimeEvent.userName,
					settingOneTimeEvent.userEmail
				)
			}
			
			SettingOneTimeEvent.OnLogout -> {
				navigateToSignIn.invoke()
			}
		}
	}
}

@Composable
fun SettingScreen(
	modifier: Modifier = Modifier,
	settingUiState: SettingUiState,
	onEvent: (SettingEvent) -> Unit,
	onBackClick: () -> Unit
) {
	LaunchedEffect(Unit) {
		onEvent(SettingEvent.OnFetchUserData)
	}
	val scrollState = rememberScrollState()
	
	Column(
		modifier = modifier
			.fillMaxSize()
			.verticalScroll(scrollState)
			.background(AioTheme.neutralColor.white)
			.padding(12.dp),
		verticalArrangement = Arrangement.spacedBy(16.dp)
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
			Text(text = stringResource(id = R.string.setting))
		}
		
		Spacer(modifier = Modifier.height(14.dp))
		
		Row(modifier = Modifier.fillMaxWidth()) {
			Image(
				modifier = Modifier
					.size(64.dp)
					.clip(CircleShape),
				painter = if (settingUiState.userImage != null)
					rememberAsyncImagePainter(model = settingUiState.userImage)
				else painterResource(id = R.drawable.man),
				contentDescription = null,
				contentScale = ContentScale.Crop
			)
			
			Spacer(modifier = Modifier.width(15.dp))
			
			Column(
				modifier = Modifier.height(64.dp),
				verticalArrangement = Arrangement.SpaceAround
			) {
				Text(
					text = settingUiState.userName ?: stringResource(id = R.string.user),
					style = AioTheme.boldTypography.lg
				)
				Row(
					verticalAlignment = Alignment.CenterVertically
				) {
					Icon(
						painter = painterResource(id = R.drawable.mail_outline),
						contentDescription = null,
						tint = AioTheme.neutralColor.dark
					)
					Text(
						text = settingUiState.userEmail,
						style = AioTheme.regularTypography.xs2.copy(color = AioTheme.neutralColor.dark)
					)
				}
			}
		}
		
		Spacer(modifier = Modifier.height(14.dp))
		
		AioButton(
			minHeight = 38.dp,
			modifier = Modifier.fillMaxWidth(),
			enableColor = AioTheme.neutralColor.white,
			borderColor = AioTheme.primaryColor.base,
			onClick = { onEvent(SettingEvent.OnEditProfile) }
		) {
			Row(
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.spacedBy(8.dp)
			) {
				Icon(
					painter = painterResource(id = R.drawable.pencil_alt_outline),
					contentDescription = ""
				)
				Text(
					text = stringResource(id = R.string.edit_profile),
					style = AioTheme.mediumTypography.base
				)
			}
		}
		
		Divider(modifier = Modifier.fillMaxWidth())
		
		Text(
			text = "App setting",
			style = AioTheme.regularTypography.xs
		)
		
		AioButton(
			modifier = Modifier.fillMaxWidth(),
			enableColor = Color.Transparent,
			shape = RoundedCornerShape(0.dp),
			leadingIcon = {
				Icon(
					painter = painterResource(id = R.drawable.lock_closed_outline),
					contentDescription = null
				)
			},
			trailingIcon = {
				Icon(
					painter = painterResource(id = R.drawable.cheveron_right_outline),
					contentDescription = null
				)
			},
			onClick = { onEvent(SettingEvent.OnChangePassword) }
		) {
			Text(
				text = stringResource(id = R.string.change_pass),
				style = AioTheme.mediumTypography.base
			)
		}
		
		AioButton(
			modifier = Modifier.fillMaxWidth(),
			enableColor = Color.Transparent,
			shape = RoundedCornerShape(0.dp),
			leadingIcon = {
				Icon(
					painter = painterResource(id = R.drawable.translate),
					contentDescription = null
				)
			},
			trailingIcon = {
				Text(
					text = settingUiState.fontWeight,
					style = AioTheme.regularTypography.sm.copy(color = AioTheme.neutralColor.base)
				)
			},
			onClick = { onEvent(SettingEvent.OnChangeFontWeight) }
		) {
			Text(
				text = stringResource(id = R.string.change_font_weight),
				style = AioTheme.mediumTypography.base
			)
		}
		
		AioButton(
			modifier = Modifier.fillMaxWidth(),
			enableColor = Color.Transparent,
			shape = RoundedCornerShape(0.dp),
			leadingIcon = {
				Icon(
					painter = painterResource(id = R.drawable.template),
					contentDescription = null
				)
			},
			trailingIcon = {
				Switch(
					checked = settingUiState.listStyle,
					colors = SwitchDefaults.colors(
						checkedTrackColor = AioTheme.primaryColor.base,
						uncheckedBorderColor = AioTheme.neutralColor.black,
						uncheckedThumbColor = AioTheme.neutralColor.white,
						uncheckedTrackColor = AioTheme.neutralColor.white
					),
					onCheckedChange = {
						onEvent(SettingEvent.OnChangeListStyle)
					}, thumbContent = {
						Icon(
							modifier = Modifier.size(16.dp),
							painter = painterResource(id = if(settingUiState.listStyle) R.drawable.grid else R.drawable.row),
							contentDescription = "",
							tint = AioTheme.primaryColor.base
						)
					}
				)
			},
			onClick = { onEvent(SettingEvent.OnChangeFontWeight) }
		) {
			Text(
				text = stringResource(id = R.string.change_list_style),
				style = AioTheme.mediumTypography.base
			)
		}
		
		val infiniteTransition = rememberInfiniteTransition()
		val angle by infiniteTransition.animateFloat(
			initialValue = 360F,
			targetValue = 0F,
			animationSpec = infiniteRepeatable(
				animation = tween(2000, easing = LinearEasing)
			)
		)
		
		AioButton(
			modifier = Modifier.fillMaxWidth(),
			enableColor = Color.Transparent,
			shape = RoundedCornerShape(0.dp),
			leadingIcon = {
				Icon(
					modifier = Modifier.graphicsLayer {
						rotationZ =  if(settingUiState.isSyncing) angle else 0f
					},
					painter = painterResource(id = R.drawable.sync_outline),
					contentDescription = null
				)
			},
			onClick = { onEvent(SettingEvent.OnSync) }
		) {
			Text(
				text = stringResource(id = if(settingUiState.isSyncing) R.string.syncing else R.string.sync ),
				style = AioTheme.mediumTypography.base
			)
		}
		
		Divider(modifier = Modifier.fillMaxWidth())
		
		AioButton(
			modifier = Modifier.fillMaxWidth(),
			enableColor = Color.Transparent,
			shape = RoundedCornerShape(0.dp),
			leadingIcon = {
				Icon(
					painter = painterResource(id = R.drawable.logout_outline),
					contentDescription = null,
					tint = AioTheme.errorColor.base
				)
			},
			onClick = { onEvent(SettingEvent.OnLogout) }
		) {
			Text(
				text = stringResource(id = R.string.logout),
				style = AioTheme.mediumTypography.base.copy(color = AioTheme.errorColor.base)
			)
		}
	}
}

@Preview
@Composable
private fun PreviewSettingScreen() {
	AioComposeTheme {
		SettingScreen(settingUiState = SettingUiState(), onBackClick = {}, onEvent = {})
	}
}