package com.notes.aionote.domain.use_case.authentication

data class ValidationResult(
	val successful: Boolean,
	val errorMessage: String? = null
)