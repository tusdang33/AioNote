package com.notes.aionote.presentation.note.normal_note

import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.notes.aionote.common.AioDispatcher
import com.notes.aionote.common.Dispatcher
import com.notes.aionote.common.RootState
import com.notes.aionote.common.RootViewModel
import com.notes.aionote.common.fail
import com.notes.aionote.common.success
import com.notes.aionote.data.model.CheckNote
import com.notes.aionote.data.model.MediaNote
import com.notes.aionote.data.model.MediaType
import com.notes.aionote.data.model.NoteContent
import com.notes.aionote.data.model.TextNote
import com.notes.aionote.data.model.toNote
import com.notes.aionote.data.model.toNoteContentEntity
import com.notes.aionote.domain.local_data.NoteEntity
import com.notes.aionote.domain.repository.AudioPlayer
import com.notes.aionote.domain.repository.AudioRecorder
import com.notes.aionote.domain.repository.NoteRepository
import com.notes.aionote.presentation.note.NoteType
import dagger.hilt.android.lifecycle.HiltViewModel
import io.realm.kotlin.ext.toRealmList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.mongodb.kbson.ObjectId
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
	private val savedStateHandle: SavedStateHandle,
	private val noteRepository: NoteRepository,
	@Dispatcher(AioDispatcher.IO) private val ioDispatcher: CoroutineDispatcher,
	private val audioPlayer: AudioPlayer,
	private val audioRecorder: AudioRecorder
): RootViewModel<NoteUiState, NoteOneTimeEvent, NoteEvent>() {
	private val currentNoteId: String = checkNotNull(savedStateHandle["noteId"])
	private var oldNote: NoteEntity? = null
	private val coroutineExceptionHandler: CoroutineExceptionHandler =
		CoroutineExceptionHandler { _, throwable ->
			failHandle(throwable.message)
		}
	
	private val _noteUiState = MutableStateFlow(NoteUiState())
	override val uiState: StateFlow<NoteUiState> = _noteUiState.asStateFlow()
	
	init {
		viewModelScope.launch {
			if (currentNoteId != "null") {
				noteRepository.getNoteById(currentNoteId).success {
					val note = it?.toNote(audioRecorder)
					if (note != null) {
						oldNote = it
						_noteUiState.update { uiState ->
							uiState.copy(
								title = note.title,
								currentTime = note.createTime,
								listNote = uiState.listNote.apply {
									clear()
									addAll(note.notes)
								}
							)
						}
					}
				}.fail {
					failHandle(it)
				}
			}
		}
	}
	
	private fun failHandle(errorMessage: String? = null) {
		sendEvent(NoteOneTimeEvent.Fail(errorMessage))
	}
	
	override fun reduceUiStateFromOneTimeEvent(
		uiState: NoteUiState,
		oneTimeEvent: NoteOneTimeEvent
	) {
		_noteUiState.value = uiState
	}
	
	private var tempVoiceUri: Uri? = null
	override fun onEvent(event: NoteEvent) {
		when (event) {
			NoteEvent.SaveNote -> {
				saveNote()
			}
			
			NoteEvent.PickImage -> {
				sendEvent(NoteOneTimeEvent.PickImage)
			}
			
			NoteEvent.PickCamera -> {
				sendEvent(NoteOneTimeEvent.PickCamera)
			}
			
			NoteEvent.PickRecord -> {
				sendEvent(NoteOneTimeEvent.PickRecord)
			}
			
			NoteEvent.PickVideo -> {
				sendEvent(NoteOneTimeEvent.PickVideo)
			}
			
			NoteEvent.PickAttachment -> {
				sendEvent(NoteOneTimeEvent.PickAttachment)
			}
			
			NoteEvent.StartRecord -> {
				audioPlayer.stop()
				audioRecorder.startRecord {
					tempVoiceUri = it
				}
			}
			
			NoteEvent.StopRecord -> {
				audioRecorder.stopRecord()
				addNotes(
					listOf(
						MediaNote(
							mediaType = MediaType.VOICE,
							mediaPath = tempVoiceUri.toString(),
							mediaDuration = run {
								tempVoiceUri?.let { uri ->
									audioRecorder.getAudioDuration(uri).success {
										return@run it
									}.fail {
										failHandle(it)
									}
								}
								null
							}
						)
					)
				)
			}
			
			NoteEvent.AddCheckBox -> {
				addNotes(listOf(CheckNote()), _noteUiState.value.focusedIndex + 1)
			}
			
			is NoteEvent.AddImage -> {
				addNotes(
					listOf(
						MediaNote(
							mediaType = MediaType.IMAGE,
							mediaPath = event.path.toString()
						)
					)
				)
			}
			
			is NoteEvent.AddVideo -> {
				addNotes(
					listOf(
						MediaNote(
							mediaType = MediaType.VIDEO,
							mediaPath = event.path.toString()
						)
					)
				)
			}
			
			is NoteEvent.AddAttachment -> {
				addNotes(
					listOf(
						MediaNote(
							mediaType = MediaType.ATTACHMENT,
							mediaPath = event.path.toString()
						)
					)
				)
			}
			
			is NoteEvent.OnCheckedChange -> {
				val note = _noteUiState.value.listNote.getOrNull(event.index)
				if (note is CheckNote) {
					_noteUiState.update {
						it.copy(
							listNote = it.listNote.apply {
								set(event.index, note.copy(checked = event.checked))
							}
						)
					}
				}
			}
			
			is NoteEvent.OnTextChange -> {
				val note = _noteUiState.value.listNote.getOrNull(event.index)
				if (note is TextNote) {
					_noteUiState.update {
						it.copy(
							listNote = it.listNote.apply {
								set(event.index, note.copy(text = event.text))
							}
						)
					}
				} else if (note is CheckNote) {
					_noteUiState.update {
						it.copy(
							listNote = it.listNote.apply {
								set(event.index, note.copy(content = event.text))
							}
						)
					}
				}
			}
			
			is NoteEvent.DeleteItem -> {
				removeNote(event.index)
			}
			
			is NoteEvent.FocusCheckNote -> {
				_noteUiState.update {
					it.copy(
						focusedIndex = event.index
					)
				}
			}
			
			is NoteEvent.ChangeImageZoom -> {
				_noteUiState.update {
					it.copy(
						isImageZoomed = event.isZoom
					)
				}
			}
			
			is NoteEvent.OnTitleChange -> {
				_noteUiState.update {
					it.copy(
						title = event.text
					)
				}
			}
			
			is NoteEvent.PlayOrStopVoice -> {
				val note = _noteUiState.value.listNote.getOrNull(event.index)
				if (note != null && note is MediaNote) {
					_noteUiState.update {
						it.copy(
							listNote = it.listNote.apply {
								set(event.index, note.copy(isPlaying = event.isPlaying))
							}
						)
					}
					audioPlayer.stop()
					if(event.isPlaying) {
						audioPlayer.playFile(note.mediaPath.toUri())
						audioPlayer.getPlayingAudio().success { mediaPlayer ->
							mediaPlayer?.setOnCompletionListener {
								audioPlayer.stop()
								_noteUiState.update {
									it.copy(
										listNote = it.listNote.apply {
											set(event.index, note.copy(isPlaying = false))
										}
									)
								}
							}
						}.fail {
							failHandle(it)
						}
					}
				}
			}
			
			is NoteEvent.UnFocusCheckNote -> {
				if (_noteUiState.value.focusedIndex != event.index) return
				_noteUiState.update {
					it.copy(
						focusedIndex = -2
					)
				}
			}
		}
	}
	
	private fun saveNote() = viewModelScope.launch(NonCancellable + ioDispatcher) {
		val prepareNote = _noteUiState.value
		if ((prepareNote.listNote.firstOrNull() as? TextNote)?.text?.isBlank() == true &&
			prepareNote.listNote.size == 1 &&
			prepareNote.title.isNullOrEmpty()
		) return@launch
		val noteEntity = NoteEntity().apply {
			if (currentNoteId != "null") {
				noteId = ObjectId.invoke(hexString = currentNoteId)
			}
			notes = prepareNote.listNote.map { it.toNoteContentEntity() }.toRealmList()
			title = prepareNote.title
			createTime = prepareNote.currentTime
			noteType = NoteType.NORMAL.ordinal
			lastModifierTime = System.currentTimeMillis()
		}
		if (!compareNote(oldNote, noteEntity)) return@launch
		if (currentNoteId != "null") {
			noteRepository.updateNote(noteEntity = noteEntity)
		} else {
			noteRepository.insertNote(noteEntity = noteEntity)
		}
		savedStateHandle["noteId"] = null
	}
	
	private fun compareNote(
		oldNote: NoteEntity?,
		newNote: NoteEntity
	): Boolean {
		if (oldNote == null) return true
		if (oldNote.title != newNote.title) return true
		if (oldNote.notes.size != newNote.notes.size) return true
		oldNote.notes.forEachIndexed { index, noteContentEntity ->
			val newCompareNote = newNote.notes[index]
			if (
				noteContentEntity.checked != newCompareNote.checked ||
				noteContentEntity.content != newCompareNote.content ||
				noteContentEntity.noteContentType != newCompareNote.noteContentType ||
				noteContentEntity.mediaPath != newCompareNote.mediaPath
			) {
				return true
			}
		}
		return false
	}
	
	private fun addNotes(
		notes: List<NoteContent>,
		index: Int = -1,
	) {
		_noteUiState.update {
			it.copy(
				listNote = it.listNote.apply {
					notes.forEach { note ->
						if(index != -1 ) {
							add(index ,note)
						} else if (it.listNote.size >= 2 && (it.listNote.last() as? TextNote)?.text == "") {
							add(it.listNote.size - 1, note)
						} else {
							add(note)
						}
					}
				}.apply {
					if (it.listNote.lastOrNull() !is TextNote) {
						add(TextNote())
					}
				}
			)
		}
	}
	
	private fun removeNote(index: Int) {
		if ((_noteUiState.value.listNote.size == 1 &&
					_noteUiState.value.listNote.last() is TextNote) ||
			(_noteUiState.value.listNote[index] is TextNote &&
					index == _noteUiState.value.listNote.size - 1)
		) return
		_noteUiState.update {
			it.copy(
				listNote = it.listNote.apply {
					removeAt(index)
				}
			)
		}
	}
}

data class NoteUiState(
	override val isLoading: Boolean = false,
	override val errorMessage: String? = null,
	val title: String? = null,
	val currentTime: Long = System.currentTimeMillis(),
	val isImageZoomed: Boolean = true,
	val focusedIndex: Int = -2,
	val listNote: SnapshotStateList<NoteContent> = mutableStateListOf(TextNote())
): RootState.ViewUiState

sealed interface NoteOneTimeEvent: RootState.OneTimeEvent<NoteUiState> {
	override fun reduce(uiState: NoteUiState): NoteUiState {
		return when (this) {
			is Fail -> uiState.copy(
				isLoading = false,
				errorMessage = this.errorMessage
			)
			
			is Loading -> uiState.copy(
				isLoading = true,
				errorMessage = ""
			)
			
			is Success -> uiState.copy(
				isLoading = false,
				errorMessage = ""
			)
			
			else -> uiState
		}
	}
	
	object Loading: NoteOneTimeEvent
	object Success: NoteOneTimeEvent
	data class Fail(val errorMessage: String? = null): NoteOneTimeEvent
	object PickImage: NoteOneTimeEvent
	object PickVideo: NoteOneTimeEvent
	object PickAttachment: NoteOneTimeEvent
	object PickCamera: NoteOneTimeEvent
	object PickRecord: NoteOneTimeEvent
}

sealed class NoteEvent: RootState.ViewEvent {
	object SaveNote: NoteEvent()
	object StartRecord: NoteEvent()
	object StopRecord: NoteEvent()
	object PickImage: NoteEvent()
	object PickCamera: NoteEvent()
	object PickRecord: NoteEvent()
	object PickVideo: NoteEvent()
	object PickAttachment: NoteEvent()
	object AddCheckBox: NoteEvent()
	data class OnTextChange(
		val index: Int,
		val text: String
	): NoteEvent()
	
	data class OnCheckedChange(
		val index: Int,
		val checked: Boolean
	): NoteEvent()
	
	data class AddImage(val path: Uri): NoteEvent()
	data class AddVideo(val path: Uri): NoteEvent()
	data class AddAttachment(val path: Uri): NoteEvent()
	data class DeleteItem(val index: Int): NoteEvent()
	data class FocusCheckNote(val index: Int): NoteEvent()
	data class UnFocusCheckNote(val index: Int): NoteEvent()
	data class ChangeImageZoom(val isZoom: Boolean): NoteEvent()
	data class PlayOrStopVoice(
		val index: Int,
		val isPlaying: Boolean
	): NoteEvent()
	
	data class OnTitleChange(val text: String): NoteEvent()
}