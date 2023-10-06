package com.notes.aionote.common

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class Dispatcher(val aioDispatcher: AioDispatcher)

enum class AioDispatcher {
	IO,
	Default,
	Main
}