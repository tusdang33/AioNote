package com.notes.aionote.presentation.note.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.notes.aionote.R
import com.notes.aionote.data.model.CheckNote
import com.notes.aionote.data.model.Note
import com.notes.aionote.formatTimestamp
import com.notes.aionote.presentation.note.NoteType
import com.notes.aionote.ui.component.AioCornerCard
import com.notes.aionote.ui.component.AioIconButton
import com.notes.aionote.ui.theme.AioComposeTheme
import com.notes.aionote.ui.theme.AioTheme
import com.notes.aionote.yearWithoutSecTimePattern

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AioTaskPreview(
	modifier: Modifier = Modifier,
	note: Note,
	onNoteClick: (String) -> Unit,
	onCheckedChange: (Int, Boolean) -> Unit = { _, _ -> },
	onToolbarItemClick: (NoteContentToolbarItem) -> Unit,
) {
	var showToolbar by remember {
		mutableStateOf(false)
	}
	
	val allCheckedState by remember(note.notes) {
		derivedStateOf {
			note.notes.all { noteContent ->
				(noteContent as? CheckNote)?.checked == true
			}
		}
	}
	
	var expandState by remember {
		mutableStateOf(false)
	}
	
	AioCornerCard(
		modifier = modifier
			.padding(26.dp)
			.combinedClickable(
				onLongClick = {
					showToolbar = !showToolbar
				},
				onClick = {
					if (showToolbar) {
						showToolbar = false
					} else {
						onNoteClick.invoke(note.noteId)
					}
				},
				role = Role.Button,
				interactionSource = MutableInteractionSource(),
				indication = null,
			),
	) {
		if (note.notes.size <= 1) {
			AioCheckNote(
				checked = allCheckedState,
				text = (note.notes.firstOrNull() as CheckNote).content,
				textFieldEnable = false,
				onCheckedChange = {
					onCheckedChange.invoke(0, it)
				}
			)
		} else {
			Column(
				verticalArrangement = Arrangement.SpaceAround,
				horizontalAlignment = Alignment.Start
			) {
				Row(verticalAlignment = Alignment.CenterVertically) {
					AioCheckNote(
						checked = allCheckedState,
						isCheckboxOnly = true,
						textFieldEnable = false,
					)
					
					Spacer(modifier = Modifier.width(10.dp))
					
					Column(modifier = Modifier.weight(1f)) {
						Text(
							text = stringResource(id = R.string.list_task),
							style = AioTheme.mediumTypography.base.copy(color = AioTheme.neutralColor.base)
						)
						Row(verticalAlignment = Alignment.CenterVertically) {
							Text(
								text = note.deadLine?.formatTimestamp(yearWithoutSecTimePattern) ?: "",
								style = AioTheme.regularTypography.sm.copy(color = AioTheme.neutralColor.base)
							)
							Icon(
								modifier = Modifier.size(16.dp),
								painter = painterResource(id = R.drawable.clock_outline),
								contentDescription = "",
								tint = AioTheme.warningColor.base
							)
						}
						
					}
					AnimatedContent(
						transitionSpec = {
							slideInVertically(
								initialOffsetY = { if (expandState) it else -it }
							) with slideOutVertically(
								targetOffsetY = { if (expandState) -it else it }
							)
						},
						targetState = when {
							expandState -> R.drawable.cheveron_up_fill
							else -> R.drawable.cheveron_down_fill
						}
					) {
						AioIconButton(
							contentPaddingValues = PaddingValues(2.dp),
							onClick = { expandState = !expandState }
						) {
							Image(
								alignment = Alignment.CenterEnd,
								painter = painterResource(id = it),
								contentDescription = it.toString()
							)
						}
					}
				}
				
				AnimatedVisibility(visible = expandState) {
					Column(
						modifier = Modifier.padding(horizontal = 22.dp, vertical = 16.dp),
						verticalArrangement = Arrangement.spacedBy(8.dp)
					) {
						note.notes.take(3).forEachIndexed { index, noteContent ->
							AioCheckNote(
								checked = (noteContent as CheckNote).checked,
								text = (noteContent as CheckNote).content,
								textFieldEnable = false,
								onCheckedChange = {
									onCheckedChange.invoke(index, it)
								}
							)
						}
					}
				}
			}
		}
		
		AioNoteToolbar(
			toolbarItem = NoteContentToolbarItem
				.values()
				.toList(),
			showToolbar = showToolbar,
			onItemClick = {
				showToolbar = false
				onToolbarItemClick.invoke(it as NoteContentToolbarItem)
			}
		)
	}
}

@Preview
@Composable
private fun PreviewAioTaskPreview() {
	AioComposeTheme {
		AioTaskPreview(
			note = Note(noteId = "", noteType = NoteType.NORMAL),
			onNoteClick = {},
			onToolbarItemClick = {})
	}
}