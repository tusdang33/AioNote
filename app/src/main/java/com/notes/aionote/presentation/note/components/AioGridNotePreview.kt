package com.notes.aionote.presentation.note.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.notes.aionote.R
import com.notes.aionote.data.model.CheckNote
import com.notes.aionote.data.model.MediaNote
import com.notes.aionote.data.model.MediaType
import com.notes.aionote.data.model.Note
import com.notes.aionote.data.model.TextNote
import com.notes.aionote.formatTimestamp
import com.notes.aionote.getVideoThumbnail
import com.notes.aionote.presentation.note.NoteType
import com.notes.aionote.ui.component.AioCornerCard
import com.notes.aionote.ui.theme.AioComposeTheme
import com.notes.aionote.ui.theme.AioTheme
import com.notes.aionote.yearTimePattern
import java.io.File

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AioGridNotePreview(
	modifier: Modifier = Modifier,
	note: Note,
	maxHeight: Dp = 224.dp,
	titleTextColor: Color = AioTheme.neutralColor.black,
	contentTextColor: Color = AioTheme.neutralColor.dark,
	onNoteClick: (String) -> Unit = {},
	onToolbarItemClick: (NoteToolbarItem) -> Unit = {},
	isPickingNote: Boolean = false,
	onPickNote: () -> Unit = {},
	isNotePicked: Boolean = false,
	isShowTitle: Boolean = true
) {
	var showToolbar by remember {
		mutableStateOf(false)
	}
	val localDensity = LocalDensity.current
	val context = LocalContext.current
	var totalItem by rememberSaveable {
		mutableStateOf(1)
	}
	
	AioCornerCard(
		modifier = modifier
			.padding(12.dp)
			.combinedClickable(
				onLongClick = {
					if (!isPickingNote) {
						showToolbar = !showToolbar
					}
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
		contentPadding = PaddingValues(0.dp)
	) {
		if (isPickingNote) {
			Box(
				modifier = Modifier
					.clip(CircleShape)
					.size(15.dp)
					.border(1.dp, AioTheme.neutralColor.black, CircleShape)
					.padding(2.dp)
					.align(Alignment.TopEnd)
					.clickable {
						onPickNote.invoke()
					}
					.then(modifier),
				contentAlignment = Alignment.Center
			) {
				if (isNotePicked) {
					Box(
						modifier = Modifier
							.clip(CircleShape)
							.background(AioTheme.successColor.base),
						contentAlignment = Alignment.Center
					) {
						Icon(
							painter = painterResource(id = R.drawable.check_outline),
							contentDescription = null,
							tint = AioTheme.neutralColor.white
						)
					}
				}
			}
		}
		
		Column {
			Column(
				modifier = Modifier
					.fillMaxWidth()
					.requiredHeightIn(max = maxHeight)
					.onGloballyPositioned { coordinates ->
						if (with(localDensity) { coordinates.size.height.toDp() } <= maxHeight - (totalItem * 5).dp) {
							++totalItem
						}
					},
				horizontalAlignment = Alignment.Start,
				verticalArrangement = Arrangement.spacedBy(5.dp)
			) {
				if (isShowTitle) {
					Text(
						text = note.title ?: stringResource(id = R.string.untitled),
						style = AioTheme.mediumTypography.lg.copy(color = titleTextColor)
					)
				}
				
				note.notes.take(totalItem).forEach { noteContent ->
					when (noteContent) {
						is TextNote -> {
							Text(
								text = noteContent.text,
								style = AioTheme.regularTypography.sm.copy(color = contentTextColor)
							)
						}
						
						is CheckNote -> {
							AioCheckNote(
								text = noteContent.content,
								checked = noteContent.checked,
								checkBoxSize = DpSize(8.dp, 8.dp),
								scaleSize = 0.5f,
								checkBoxEnable = false,
								textFieldEnable = false,
								textStyle = AioTheme.regularTypography.sm.copy(color = contentTextColor)
							)
						}
						
						is MediaNote -> {
							when (noteContent.mediaType) {
								MediaType.IMAGE -> {
									AsyncImage(
										modifier = Modifier.clip(RoundedCornerShape(12.dp)),
										model = noteContent.mediaPath,
										contentDescription = "",
										contentScale = ContentScale.Crop
									)
								}
								
								MediaType.VIDEO -> {
									Box(
										modifier = Modifier.clip(RoundedCornerShape(12.dp)),
										contentAlignment = Alignment.Center
									) {
										AsyncImage(
											model = getVideoThumbnail(
												context = context,
												noteContent.mediaPath.toUri()
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
								
								MediaType.VOICE -> {
									AioVoiceNote(
										voiceDuration = noteContent.mediaDuration ?: 0L,
										enabled = false,
										textStyle = AioTheme.regularTypography.xs,
										isPlaying = noteContent.isPlaying,
									)
								}
								
								MediaType.ATTACHMENT -> {
									AioAttachmentNote(
										readOnly = true,
										attachment = File(
											noteContent.mediaPath.toUri().path ?: ""
										)
									)
								}
							}
						}
					}
				}
			}
			
			Spacer(modifier = Modifier.height(8.dp))
			
			Text(
				text = note.createTime.formatTimestamp(yearTimePattern),
				style = AioTheme.regularTypography.xs.copy(color = contentTextColor)
			)
		}
		
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
private fun PreviewAioNotePreview() {
	AioComposeTheme {
		AioGridNotePreview(
			note =
			Note(
				noteId = "",
				noteType = NoteType.TASK,
				title = "This is title",
				notes = listOf(
					MediaNote(
						mediaType = MediaType.IMAGE,
						mediaPath = "https://picsum.photos/200"
					),
					TextNote(text = stringResource(id = R.string.lorem)),
					
					)
			),
			onNoteClick = {}
		)
	}
}