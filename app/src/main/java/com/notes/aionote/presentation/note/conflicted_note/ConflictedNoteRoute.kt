package com.notes.aionote.presentation.note.conflicted_note

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.notes.aionote.collectInLaunchedEffectWithLifecycle
import com.notes.aionote.ui.theme.AioComposeTheme
import com.notes.aionote.ui.theme.AioTheme

@Composable
fun ConflictedNoteRoute(
	conflictedNoteViewModel: ConflictedNoteViewModel = hiltViewModel()
) {
	val conflictedNoteUiState by conflictedNoteViewModel.uiState.collectAsStateWithLifecycle()
	
	ConflictedNoteScreen(
		modifier = Modifier
			.fillMaxSize()
			.background(AioTheme.neutralColor.base.copy(alpha = 0.5f)),
		conflictedNoteUiState = conflictedNoteUiState,
		onEvent = conflictedNoteViewModel::onEvent
	)
	
	conflictedNoteViewModel.oneTimeEvent.collectInLaunchedEffectWithLifecycle {
	
	}
}

@Composable
fun ConflictedNoteScreen(
	modifier: Modifier = Modifier,
	conflictedNoteUiState: ConflictedNoteUiState,
	onEvent: (ConflictedNoteEvent) -> Unit
) {
	
	val configuration = LocalConfiguration.current
	val screenWidth = configuration.screenWidthDp.dp
	val screenHeight = configuration.screenHeightDp.dp
	
	DisposableEffect(Unit) {
		onEvent(ConflictedNoteEvent.OnFetchData)
		onDispose {
			onEvent(ConflictedNoteEvent.OnDispose)
		}
	}
	
	Box(
		modifier = modifier,
		contentAlignment = Alignment.Center
	) {
		Box(
			modifier = Modifier
				.fillMaxSize()
				.clickable(
					interactionSource = remember { MutableInteractionSource() },
					indication = null,
					onClick = {})
		)
		Column(
			modifier = Modifier
				.width(screenWidth.times(0.9f))
				.height(screenHeight.times(0.8f))
				.clip(RoundedCornerShape(8.dp))
				.background(AioTheme.neutralColor.white)
				.padding(horizontal = 12.dp, vertical = 10.dp)
		) {
			if (conflictedNoteUiState.notePreviewing == null) {
				ConflictedNotePicking(
					listConflictedNote = conflictedNoteUiState.listConflictedNote,
					onAcceptLocal = { onEvent.invoke(ConflictedNoteEvent.OnAcceptLocal) },
					onAcceptRemote = { onEvent.invoke(ConflictedNoteEvent.OnAcceptRemote) },
					onResolve = { onEvent.invoke(ConflictedNoteEvent.OnResolve) },
					onPickNote = { onEvent.invoke(ConflictedNoteEvent.OnPickedNote(it)) },
					onPreviewNote = { onEvent.invoke(ConflictedNoteEvent.OnPreviewNote(it)) },
					enabledResolve = conflictedNoteUiState.enabledResolve
				)
			}
			
			AnimatedVisibility(
				visible = conflictedNoteUiState.notePreviewing != null,
				enter = expandVertically { 0 },
				exit = shrinkHorizontally(animationSpec = tween(durationMillis = 0)) { it }
			) {
				if (conflictedNoteUiState.notePreviewing != null)
					Column {
						ConflictedNotePreview(
							note = conflictedNoteUiState.notePreviewing,
							onBackClick = { onEvent(ConflictedNoteEvent.OnDisposeNote) }
						)
					}
			}
		}
	}
}

@Preview(showSystemUi = true)
@Composable
private fun PreviewConflictedNoteScreen() {
	AioComposeTheme() {
		ConflictedNoteScreen(conflictedNoteUiState = ConflictedNoteUiState()) {}
	}
}