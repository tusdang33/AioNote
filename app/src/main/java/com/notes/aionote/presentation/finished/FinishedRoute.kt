package com.notes.aionote.presentation.finished

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.notes.aionote.R
import com.notes.aionote.collectInLaunchedEffectWithLifecycle
import com.notes.aionote.presentation.note.components.AioTaskPreview
import com.notes.aionote.presentation.note.components.NoteContentToolbarItem
import com.notes.aionote.ui.component.AioActionBar
import com.notes.aionote.ui.theme.AioTheme

@Composable
fun FinishedRoute(
	onBackClick: () -> Unit,
	navigateToTask: (String) -> Unit,
	finishedViewModel: FinishedViewModel = hiltViewModel()
) {
	
	val finishedUiState by finishedViewModel.uiState.collectAsStateWithLifecycle()
	FinishedScreen(
		modifier = Modifier.fillMaxSize(),
		finishedUiState = finishedUiState,
		onEvent = finishedViewModel::onEvent,
		onBackClick = onBackClick
	)
	
	finishedViewModel.oneTimeEvent.collectInLaunchedEffectWithLifecycle { finishedOneTimeEvent ->
		when (finishedOneTimeEvent) {
			is FinishedOneTimeEvent.NavigateToTask -> {
				navigateToTask.invoke(finishedOneTimeEvent.taskId)
			}
		}
	}
}

@Composable
fun FinishedScreen(
	modifier: Modifier = Modifier,
	finishedUiState: FinishedUiState,
	onEvent: (FinishedViewEvent) -> Unit,
	onBackClick: () -> Unit,
) {
	Column(
		modifier = modifier
			.background(AioTheme.neutralColor.white)
			.padding(12.dp),
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
		Column() {
			LazyColumn(
				modifier = Modifier,
				contentPadding = PaddingValues(12.dp),
				verticalArrangement = Arrangement.spacedBy(12.dp),
				horizontalAlignment = Alignment.CenterHorizontally
			) {
				itemsIndexed(finishedUiState.listTask) { index, note ->
					AioTaskPreview(
						note = note,
						isReadOnly = true,
						onNoteClick = { onEvent(FinishedViewEvent.NavigateToEditTask(it)) },
						onToolbarItemClick = { toolbar ->
							when (toolbar) {
								NoteContentToolbarItem.DELETE -> onEvent(
									FinishedViewEvent.DeleteTask(
										index
									)
								)
							}
						},
						onCheckedChange = { checkNoteIndex, checked ->
							onEvent(
								FinishedViewEvent.OnTaskCheckedChange(
									noteIndex = index,
									noteContentIndex = checkNoteIndex,
									checked = checked
								)
							)
						}
					)
				}
			}
		}
	}
}

@Preview
@Composable
private fun PreviewFinishedScreen() {

}