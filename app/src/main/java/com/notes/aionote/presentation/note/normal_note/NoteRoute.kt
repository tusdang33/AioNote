package com.notes.aionote.presentation.note.normal_note

import AioVideoNote
import android.content.Context
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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.notes.aionote.FileProviderHelper
import com.notes.aionote.R
import com.notes.aionote.collectInLaunchedEffectWithLifecycle
import com.notes.aionote.data.model.CheckNote
import com.notes.aionote.data.model.MediaNote
import com.notes.aionote.data.model.MediaType.ATTACHMENT
import com.notes.aionote.data.model.MediaType.IMAGE
import com.notes.aionote.data.model.MediaType.VIDEO
import com.notes.aionote.data.model.MediaType.VOICE
import com.notes.aionote.data.model.TextNote
import com.notes.aionote.grantReadPermissionToUri
import com.notes.aionote.presentation.note.components.AioAttachmentNote
import com.notes.aionote.presentation.note.components.AioCheckNote
import com.notes.aionote.presentation.note.components.AioImageNote
import com.notes.aionote.presentation.note.components.AioLottieVoice
import com.notes.aionote.presentation.note.components.AioNotePicker
import com.notes.aionote.presentation.note.components.AioNoteTitle
import com.notes.aionote.presentation.note.components.AioTextNote
import com.notes.aionote.presentation.note.components.AioVoiceNote
import com.notes.aionote.presentation.note.components.ImageNoteContentToolbarItem
import com.notes.aionote.presentation.note.components.ImagePickerToolbarItem
import com.notes.aionote.presentation.note.components.NoteContentToolbarItem
import com.notes.aionote.presentation.note.components.NoteOption
import com.notes.aionote.presentation.note.components.VideoPickerToolbarItem
import com.notes.aionote.presentation.note.task.TaskEvent
import com.notes.aionote.ui.component.AioActionBar
import com.notes.aionote.ui.theme.AioComposeTheme
import com.notes.aionote.ui.theme.AioTheme
import com.notes.aionote.viewDocument
import java.io.File

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
	
	val attachmentLauncher = rememberLauncherForActivityResult(
		contract = ActivityResultContracts.OpenDocument()
	) { uri ->
		uri?.let {
			grantReadPermissionToUri(context, it)
			noteViewModel.onEvent(NoteEvent.AddAttachment(it))
		}
	}
	
	NoteScreen(
		modifier = Modifier
			.fillMaxSize()
			.imePadding(),
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
				cameraUri = FileProviderHelper.createUriForImage(context)
				cameraLauncher.launch(cameraUri)
			}
			
			NoteOneTimeEvent.PickRecord -> {
				cameraUri = FileProviderHelper.createUriForVideo(context)
				recordLauncher.launch(cameraUri)
			}
			
			NoteOneTimeEvent.PickAttachment -> {
				attachmentLauncher.launch(arrayOf("*/*"))
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
	context: Context = LocalContext.current,
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
			Text(text = stringResource(id = R.string.new_note))
		}
		
		Spacer(modifier = Modifier.height(12.dp))
		
		AioNoteTitle(
			modifier = Modifier.padding(horizontal = 12.dp),
			text = noteUiState.title ?: "",
			currentTime = noteUiState.currentTime,
			onTextChange = {
				onEvent(NoteEvent.OnTitleChange(it))
			}
		)
		val focusRequester by remember {
			mutableStateOf(FocusRequester())
		}
		
		LazyColumn(
			modifier = Modifier
				.clickable(
					interactionSource = remember { MutableInteractionSource() },
					indication = null,
					enabled = true,
					onClick = {
						try {
							focusRequester.requestFocus()
						} catch (e: Exception) {
							/* ignore focus */
						}
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
							},
							focusRequester = focusRequester
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
							},
							onDone = {
								onEvent(NoteEvent.AddCheckBox)
							},
							onFocus = {
								onEvent(NoteEvent.FocusCheckNote(index))
							},
							onUnFocus = {
								onEvent(NoteEvent.UnFocusCheckNote(index))
							}
						)
					}
					
					is MediaNote -> {
						when (note.mediaType) {
							IMAGE -> {
								AioImageNote(
									image = note.mediaPath,
									zoomed = noteUiState.isImageZoomed
								) { toolbar ->
									when (toolbar) {
										ImageNoteContentToolbarItem.ZOOM -> {
											onEvent(NoteEvent.ChangeImageZoom(!noteUiState.isImageZoomed))
										}
										
										ImageNoteContentToolbarItem.DELETE -> {
											onEvent(NoteEvent.DeleteItem(index))
										}
									}
								}
							}
							
							VIDEO -> {
								AioVideoNote(videoUrl = note.mediaPath) { toolbar ->
									when (toolbar) {
										NoteContentToolbarItem.DELETE -> onEvent(NoteEvent.DeleteItem(index))
									}
								}
							}
							
							VOICE -> {
								AioVoiceNote(
									voiceDuration = note.mediaDuration ?: 0L,
									isPlaying = note.isPlaying,
									onPlayClick = {
										onEvent(NoteEvent.PlayOrStopVoice(index, !note.isPlaying))
									},
									onDeleteClick = {
										onEvent(NoteEvent.PlayOrStopVoice(index, false))
										onEvent(NoteEvent.DeleteItem(index))
									}
								)
							}
							
							ATTACHMENT -> {
								AioAttachmentNote(
									attachment = File(note.mediaPath.toUri().path ?: ""),
									onViewFile = {
										viewDocument(context, note.mediaPath.toUri())
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
			pickers = NoteOption.values().toList(),
			holdingNotePicker = holdingNoteOption,
			onToolbarItemClick = {
				when(it) {
					ImagePickerToolbarItem.IMAGE -> {
						onEvent(NoteEvent.PickImage)
					}
					ImagePickerToolbarItem.CAMERA -> {
						onEvent(NoteEvent.PickCamera)
					}
					VideoPickerToolbarItem.VIDEO -> {
						onEvent(NoteEvent.PickVideo)
					}
					VideoPickerToolbarItem.RECORD -> {
						onEvent(NoteEvent.PickRecord)
					}
				}
			},
			onPickerClick = {
				when (it) {
					NoteOption.VOICE -> {
						if (holdingNoteOption == null) {
							holdingNoteOption = it as? NoteOption
							onEvent(NoteEvent.StartRecord)
						} else {
							holdingNoteOption = null
							onEvent(NoteEvent.StopRecord)
						}
					}
					
					NoteOption.CHECK -> {
						onEvent(NoteEvent.AddCheckBox)
					}
					
					NoteOption.ATTACHMENT -> {
						onEvent(NoteEvent.PickAttachment)
					}
					
					else -> { /*noop*/
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