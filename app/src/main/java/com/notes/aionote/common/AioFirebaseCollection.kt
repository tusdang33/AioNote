package com.notes.aionote.common

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class CollectionRef(val aioFirebaseCollection: AioFirebaseCollection)

enum class AioFirebaseCollection(val ref: String) {
	USER("user")
}