package com.notes.aionote.presentation.note.conflicted_note

import com.notes.aionote.common.fail
import com.notes.aionote.common.success
import com.notes.aionote.domain.local_data.NoteEntity
import com.notes.aionote.domain.remote_data.FireNoteContent
import com.notes.aionote.domain.remote_data.FireNoteEntity
import com.notes.aionote.domain.repository.MediaRepository
import javax.inject.Inject

class ResolveConflictImpl @Inject constructor(
	private val mediaRepository: MediaRepository
): ResolveConflict {
	override suspend fun callback(
		param: List<Pair<NoteEntity, FireNoteEntity>>,
		userId: String
	): List<Pair<NoteEntity?, FireNoteEntity?>> {
		ConflictPromise.listConflictedNote = param.map {
			Pair(
				it.first, it.second.copy(
					notes = mappingURLFireNote(it.second, userId)
				)
			)
		}
		ConflictPromise.resolveConflictScreenState = true
		val result = ConflictPromise.completableDeferred.await().mapIndexed() { index, pairNote ->
			if (pairNote.first == null) {
				Pair(null, param[index].second)
			} else {
				Pair(param[index].first, null)
			}
		}
		return result
	}
	
	private suspend fun mappingURLFireNote(
		fireNote: FireNoteEntity,
		userId: String
	): List<FireNoteContent> {
		return fireNote.notes.map { fNoteContent ->
			if (fNoteContent.mediaPath.isNullOrBlank()) {
				fNoteContent
			} else {
				var path = ""
				mediaRepository.getDownloadUrl(
					fileName = fNoteContent.mediaPath!!,
					userId = userId,
					noteId = fireNote.noteId
				).success {
					path = it ?: ""
				}.fail {
					throw Exception("Get download URL fail")
				}
				fNoteContent.copy(
					mediaPath = path
				)
			}
		}
	}
}