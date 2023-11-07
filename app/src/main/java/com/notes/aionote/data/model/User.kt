package com.notes.aionote.data.model

import android.os.Parcelable
import com.notes.aionote.domain.remote_data.FireUserEntity
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
	val id: String = "",
	val name: String? = null,
	val email: String = "",
	val image: String? = null
): Parcelable

fun FireUserEntity.toUser(): User {
	return User(
		id = this.id,
		name = this.name,
		email = this.email,
		image = this.image
	)
}