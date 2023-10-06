package com.notes.aionote.presentation.note

import AioVideoNote
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.notes.aionote.FileProviderHelper
import com.notes.aionote.collectInLaunchedEffectWithLifecycle
import com.notes.aionote.data.model.CheckNote
import com.notes.aionote.data.model.MediaNote
import com.notes.aionote.data.model.MediaType.IMAGE
import com.notes.aionote.data.model.MediaType.VIDEO
import com.notes.aionote.data.model.MediaType.VOICE
import com.notes.aionote.data.model.TextNote
import com.notes.aionote.grantReadPermissionToUri
import com.notes.aionote.presentation.note.components.AioCheckNote
import com.notes.aionote.presentation.note.components.AioImageNote
import com.notes.aionote.presentation.note.components.AioLottieVoice
import com.notes.aionote.presentation.note.components.AioNotePicker
import com.notes.aionote.presentation.note.components.AioNoteTitle
import com.notes.aionote.presentation.note.components.AioTextNote
import com.notes.aionote.presentation.note.components.AioVoiceNote
import com.notes.aionote.presentation.note.components.NoteOption
import com.notes.aionote.presentation.note.components.NoteToolbarItem
import com.notes.aionote.ui.component.AioActionBar
import com.notes.aionote.ui.theme.AioComposeTheme
import com.notes.aionote.ui.theme.AioTheme

@Composable
fun NoteRoute(
	onBackClick: () -> Unit,
	noteViewModel: NoteViewModel = hiltViewModel()
) {
	val noteUiState by noteViewModel.uiState.collectAsStateWithLifecycle()
	val context = LocalContext.current
	
	val imageLauncher = rememberLauncherForActivityResult(
		contract = ActivityResultContracts.PickVisualMedia()
	) { uri ->
		uri?.let {
			grantReadPermissionToUri(context, it)
			noteViewModel.onEvent(NoteEvent.AddImage(it))
		}
	}
	
	var cameraUri by remember { mutableStateOf(Uri.EMPTY) }
	
	val cameraLauncher = rememberLauncherForActivityResult(
		contract = ActivityResultContracts.TakePicture()
	) { success ->
		if (success) {
			noteViewModel.onEvent(NoteEvent.AddImage(cameraUri))
		}
	}
	
	val videoLauncher = rememberLauncherForActivityResult(
		contract = ActivityResultContracts.PickVisualMedia()
	) { uri ->
		uri?.let {
			grantReadPermissionToUri(context, it)
			noteViewModel.onEvent(NoteEvent.AddVideo(it))
		}
	}
	
	val recordLauncher = rememberLauncherForActivityResult(
		contract = ActivityResultContracts.CaptureVideo()
	) { success ->
		if (success) {
			noteViewModel.onEvent(NoteEvent.AddVideo(cameraUri))
		}
	}
	NoteScreen(
		modifier = Modifier.fillMaxSize(),
		noteUiState = noteUiState,
		onBackClick = onBackClick,
		onEvent = noteViewModel::onEvent
	)
	
	noteViewModel.oneTimeEvent.collectInLaunchedEffectWithLifecycle { noteOneTimeEvent ->
		when (noteOneTimeEvent) {
			NoteOneTimeEvent.PickImage -> {
				imageLauncher.launch(
					PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
				)
			}
			
			NoteOneTimeEvent.PickVideo -> {
				videoLauncher.launch(
					PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
				)
			}
			
			NoteOneTimeEvent.PickCamera -> {
				cameraUri = FileProviderHelper.createUriForMedia(context)
				cameraLauncher.launch(cameraUri)
			}
			
			NoteOneTimeEvent.PickRecord -> {
				cameraUri = FileProviderHelper.createUriForMedia(context)
				recordLauncher.launch(cameraUri)
			}
			
			else -> {/*noop*/
			}
		}
	}
}

@Composable
fun NoteScreen(
	modifier: Modifier = Modifier,
	onBackClick: () -> Unit,
	noteUiState: NoteUiState,
	onEvent: (NoteEvent) -> Unit,
) {
	val lazyState = rememberLazyListState()
	var holdingNoteOption: NoteOption? by remember {
		mutableStateOf(null)
	}
	
	Column(modifier = modifier) {
		AioActionBar(
			modifier = Modifier.background(AioTheme.neutralColor.white),
			leadingIconClick = {
				onEvent(NoteEvent.SaveNote)
				onBackClick.invoke()
			}
		) {
			Text(text = "New Note")
		}
		
		AioNoteTitle(
			modifier = Modifier.padding(horizontal = 20.dp),
			text = noteUiState.title ?: "",
			currentTime = noteUiState.currentTime,
			onTextChange = {
				onEvent(NoteEvent.OnTitleChange(it))
			}
		)
		
		LazyColumn(
			modifier = Modifier
				.clickable(
					interactionSource = remember { MutableInteractionSource() },
					indication = null,
					enabled = true,
					onClick = {
						Log.e("tudm", "NoteScreen clcikc ")
					}
				)
				.background(AioTheme.neutralColor.white)
				.fillMaxWidth()
				.weight(1f),
			state = lazyState,
			verticalArrangement = Arrangement.spacedBy(8.dp),
			contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 200.dp)
		) {
			itemsIndexed(noteUiState.listNote) { index, note ->
				when (note) {
					is TextNote -> {
						AioTextNote(
							text = note.text,
							onTextChange = {
								onEvent(NoteEvent.OnTextChange(index, it))
							},
							onDeleteCheckbox = {
								onEvent(NoteEvent.DeleteItem(index))
							}
						)
					}
					
					is CheckNote -> {
						AioCheckNote(
							text = note.content,
							checked = note.checked,
							onCheckedChange = {
								onEvent(NoteEvent.OnCheckedChange(index, it))
							},
							onTextChange = {
								onEvent(NoteEvent.OnTextChange(index, it))
							},
							onDeleteCheckbox = {
								onEvent(NoteEvent.DeleteItem(index))
							}
						)
					}
					
					is MediaNote -> {
						when (note.mediaType) {
							IMAGE -> {
								AioImageNote(image = note.mediaPath) { toolbar ->
									when (toolbar) {
										NoteToolbarItem.DELETE -> onEvent(NoteEvent.DeleteItem(index))
									}
								}
							}
							
							VIDEO -> {
								AioVideoNote(videoUrl = note.mediaPath) { toolbar ->
									when (toolbar) {
										NoteToolbarItem.DELETE -> onEvent(NoteEvent.DeleteItem(index))
									}
								}
							}
							
							VOICE -> {
								AioVoiceNote(
									voiceDuration = note.mediaDuration ?: 0L,
									isPlaying = note.isPlaying,
									onPlayClick = {
										onEvent(NoteEvent.PlayItem(index, !note.isPlaying))
									},
									onDeleteClick = {
										onEvent(NoteEvent.DeleteItem(index))
									}
								)
							}
						}
					}
				}
			}
		}
		
		AnimatedVisibility(visible = holdingNoteOption == NoteOption.VOICE) {
			AioLottieVoice()
		}
		
		
		AioNotePicker(
			options = NoteOption.values().toList(),
			holdingNoteOption = holdingNoteOption,
			onOptionClick = {
				when (it) {
					NoteOption.IMAGE -> {
						onEvent(NoteEvent.PickImage)
					}
					
					NoteOption.VIDEO -> {
						onEvent(NoteEvent.PickVideo)
					}
					
					NoteOption.VOICE -> {
						if (holdingNoteOption == null) {
							holdingNoteOption = it
							onEvent(NoteEvent.StartRecord)
						} else {
							holdingNoteOption = null
							onEvent(NoteEvent.StopRecord)
						}
					}
					
					NoteOption.CHECK -> {
						onEvent(NoteEvent.AddCheckBox)
					}
					
					NoteOption.CAMERA -> {
						onEvent(NoteEvent.PickCamera)
					}
					
					NoteOption.RECORD -> {
						onEvent(NoteEvent.PickRecord)
					}
				}
			}
		)
	}
}

@Preview(showSystemUi = true)
@Composable
private fun PreviewNoteScreen() {
	AioComposeTheme {
		NoteScreen(
			onBackClick = {},
			noteUiState = NoteUiState()
		) {
			
		}
	}
}