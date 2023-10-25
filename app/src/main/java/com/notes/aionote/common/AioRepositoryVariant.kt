package com.notes.aionote.common

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.SOURCE)
annotation class RepoType(val aioRepoType: AioRepoType)

enum class AioRepoType {
	LOCAL, REMOTE
}