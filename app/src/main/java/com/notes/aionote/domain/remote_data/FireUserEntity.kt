package com.notes.aionote.domain.remote_data

import android.os.Parcelable
import com.notes.aionote.data.model.User
import kotlinx.parcelize.Parcelize

@Parcelize
data class FireUserEntity(
	val id: String = "",
	val name: String? = null,
	val email: String = "",
	val image: String? = null,
	val noteContentRef: String = ""
): Parcelable