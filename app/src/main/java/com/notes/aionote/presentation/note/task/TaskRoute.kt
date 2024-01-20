package com.notes.aionote.presentation.note.task

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.notes.aionote.R
import com.notes.aionote.formatTimestamp
import com.notes.aionote.presentation.note.components.AioCheckNote
import com.notes.aionote.presentation.note.components.AioDateTimePicker
import com.notes.aionote.ui.component.AioIconButton
import com.notes.aionote.ui.theme.AioComposeTheme
import com.notes.aionote.ui.theme.AioTheme
import com.notes.aionote.yearWithoutSecTimePattern

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskRoute(
	onBackClick: () -> Unit,
	taskViewModel: TaskViewModel = hiltViewModel()
) {
	val taskUiState by taskViewModel.uiState.collectAsStateWithLifecycle()
	TaskScreen(
		modifier = Modifier,
		taskUiState = taskUiState,
		onBackClick = onBackClick,
		onEvent = taskViewModel::onEvent
	)
}

@Composable
fun TaskScreen(
	modifier: Modifier = Modifier,
	taskUiState: TaskUiState,
	onBackClick: () -> Unit,
	onEvent: (TaskEvent) -> Unit
) {
	val lazyListState = rememberLazyListState()
	
	AnimatedVisibility(visible = taskUiState.isShowDialog) {
		AioDateTimePicker(
			onSetDateTime = {
				onEvent(TaskEvent.OnDateTimeChange(it))
				onEvent(TaskEvent.DismissDialog)
			},
			onDismissRequest = {
				onEvent(TaskEvent.DismissDialog)
			}
		)
	}
	
//	DisposableEffect(Unit) {
//		onDispose {
//			onEvent(TaskEvent.SaveNote)
//		}
//	}
	
	Column(
		modifier = modifier
			.padding(18.dp)
			.clip(RoundedCornerShape(12.dp))
			.background(AioTheme.neutralColor.white)
			.padding(horizontal = 18.dp, vertical = 22.dp),
		verticalArrangement = Arrangement.Center,
	) {
		LazyColumn(
			modifier = Modifier.wrapContentSize(),
			state = lazyListState,
			verticalArrangement = Arrangement.spacedBy(8.dp)
		) {
			itemsIndexed(taskUiState.listCheckNote) { index, note ->
				AioCheckNote(
					text = note.content,
					checked = note.checked,
					focusRequester = taskUiState.focusRequester,
					onTextChange = {
						onEvent(TaskEvent.OnContentChange(index, it))
					},
					onDeleteCheckbox = {
						onEvent(TaskEvent.DeleteItem(index))
					},
					onDone = {
						onEvent(TaskEvent.AddCheckNote(index))
					},
					onCheckedChange = {
						onEvent(TaskEvent.OnCheckedChange(index, !note.checked))
					}
				)
			}
		}
		
		Spacer(modifier = Modifier.height(56.dp))
		
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.requiredHeightIn(min = 50.dp),
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.CenterVertically
		) {
			AioIconButton(
				backgroundColor = AioTheme.neutralColor.light,
				contentPaddingValues = PaddingValues(8.dp),
				onClick = { onEvent(TaskEvent.PickDateTime) }
			) {
				Row(verticalAlignment = Alignment.CenterVertically) {
					Icon(
						painter = painterResource(id = R.drawable.clock_outline),
						contentDescription = ""
					)
					Spacer(modifier = Modifier.width(5.dp))
					if (taskUiState.deadline == null) {
						Text(
							text = "Remind Me",
						     style = AioTheme.regularTypography.sm
						)
					} else {
						Text(
							text = taskUiState.deadline.formatTimestamp(yearWithoutSecTimePattern),
							style = AioTheme.mediumTypography.sm.copy(color = AioTheme.primaryColor.base)
						)
					}
				}
			}
			AioIconButton(
				onClick = {
					onEvent(TaskEvent.SaveNote)
					onBackClick.invoke()
				},
				enabled = taskUiState.deadline != null,
				contentPaddingValues = PaddingValues(8.dp)
			) {
				Text(
					text = "Xong",
					style = AioTheme.boldTypography.base
				)
			}
		}
	}
}

@Preview(showSystemUi = true)
@Composable
private fun PreviewTaskScreen() {
	AioComposeTheme {
		TaskScreen(taskUiState = TaskUiState(), onEvent = {}, onBackClick = {})
	}
}
