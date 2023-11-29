package com.notes.aionote.domain.repository

import com.notes.aionote.common.Resource

interface MediaRepository {
	suspend fun uploadMedia(
		mediaPath: String,
		userId: String,
		noteId: String
	): Resource<String>
	
	suspend fun downloadMedia(
		fileName: String,
		userId: String,
		noteId: String
	): Resource<String>
}