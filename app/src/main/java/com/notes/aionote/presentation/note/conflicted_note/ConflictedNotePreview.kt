package com.notes.aionote.presentation.note.conflicted_note

import AioVideoNote
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.notes.aionote.R
import com.notes.aionote.data.model.CheckNote
import com.notes.aionote.data.model.MediaNote
import com.notes.aionote.data.model.MediaType
import com.notes.aionote.data.model.Note
import com.notes.aionote.data.model.TextNote
import com.notes.aionote.getVideoThumbnail
import com.notes.aionote.presentation.note.NoteType
import com.notes.aionote.presentation.note.components.AioAttachmentNote
import com.notes.aionote.presentation.note.components.AioCheckNote
import com.notes.aionote.presentation.note.components.AioImageNote
import com.notes.aionote.presentation.note.components.AioNoteTitle
import com.notes.aionote.presentation.note.components.AioTextNote
import com.notes.aionote.presentation.note.normal_note.NoteEvent
import com.notes.aionote.ui.component.AioActionBar
import com.notes.aionote.ui.theme.AioComposeTheme
import com.notes.aionote.ui.theme.AioTheme
import java.io.File

@Composable
fun ColumnScope.ConflictedNotePreview(
	note: Note,
	onBackClick: () -> Unit
) {
	val context = LocalContext.current
	
	AioActionBar(
		modifier = Modifier.background(AioTheme.neutralColor.white),
		leadingIconClick = {
			onBackClick.invoke()
		}
	) {
		Text(text = stringResource(id = R.string.conflict_note_preview), style = AioTheme.mediumTypography.sm)
	}
	
	Spacer(modifier = Modifier.height(12.dp))
	AioNoteTitle(
		modifier = Modifier.padding(horizontal = 12.dp),
		text = note.title ?: "",
		isReadOnly = true,
		currentTime = note.createTime,
	)
	
	LazyColumn(
		modifier = Modifier
			.background(AioTheme.neutralColor.white)
			.fillMaxWidth()
			.weight(1f),
		verticalArrangement = Arrangement.spacedBy(8.dp),
		contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 200.dp)
	) {
		items(note.notes) { note ->
			when (note) {
				is TextNote -> {
					AioTextNote(
						text = note.text,
						onTextChange = {},
						onDeleteCheckbox = {},
					)
				}
				
				is CheckNote -> {
					AioCheckNote(
						text = note.content,
						checked = note.checked,
						isReadOnly = true,
					)
				}
				
				is MediaNote -> {
					when (note.mediaType) {
						MediaType.IMAGE -> {
							AioImageNote(
								image = note.mediaPath,
							)
						}
						
						MediaType.VIDEO -> {
							AioVideoNote(videoUrl = note.mediaPath)
						}
						
						MediaType.VOICE -> {
							Box(
								modifier = Modifier.clip(RoundedCornerShape(12.dp)),
								contentAlignment = Alignment.Center
							) {
								AsyncImage(
									model = getVideoThumbnail(
										context = context,
										note.mediaPath.toUri()
									),
									contentDescription = "",
									contentScale = ContentScale.Crop
								)
								Icon(
									modifier = Modifier.size(28.dp),
									painter = painterResource(id = R.drawable.play_fill),
									contentDescription = ""
								)
							}
						}
						
						MediaType.ATTACHMENT -> {
							AioAttachmentNote(
								attachment = File(note.mediaPath.toUri().path ?: ""),
							)
						}
					}
				}
			}
		}
	}
}

@Preview
@Composable
fun PConflictedNotePreview() {
	AioComposeTheme {
		Column() {
			ConflictedNotePreview(
				Note(
					noteId = "65abeaa257d5d1020cf5bbe0",
					notes = listOf(TextNote(text = "asdasdsad")),
					title = "12wv",
					createTime = 1705765532308L,
					noteType = NoteType.NORMAL,
					category = null,
					deadLine = null,
					version = 4
				)
			) {
			
			}
			
		}
	}
}