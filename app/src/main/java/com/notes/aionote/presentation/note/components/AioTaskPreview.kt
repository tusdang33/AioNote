package com.notes.aionote.presentation.note.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.notes.aionote.data.model.CheckNote
import com.notes.aionote.data.model.Note
import com.notes.aionote.ui.component.AioCornerCard
import com.notes.aionote.ui.theme.AioComposeTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AioTaskPreview(
	modifier: Modifier = Modifier,
	note: Note,
	onNoteClick: (String) -> Unit,
	onToolbarItemClick: (NoteToolbarItem) -> Unit,
) {
	var showToolbar by remember {
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
		AioCheckNote(
			checked = (note.notes.firstOrNull() as CheckNote).checked,
			text = (note.notes.firstOrNull() as CheckNote).content,
			textFieldEnable = false
		)
		AioNoteToolbar(
			toolbarItem = NoteToolbarItem
				.values()
				.toList(),
			showToolbar = showToolbar,
			onItemClick = {
				showToolbar = false
				onToolbarItemClick.invoke(it as NoteToolbarItem)
			}
		)
	}
}

@Preview
@Composable
private fun PreviewAioTaskPreview() {
	AioComposeTheme {
		AioTaskPreview(
			note = Note(noteId = "", noteType = 0),
			onNoteClick = {},
			onToolbarItemClick = {})
	}
}