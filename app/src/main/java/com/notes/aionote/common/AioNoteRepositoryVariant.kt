package com.notes.aionote.common

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class NoteRepoType(val aioNoteRepoType: AioNoteRepoType)

enum class AioNoteRepoType {
	LOCAL, REMOTE
}