package com.notes.aionote.domain.use_case.authentication

import android.util.Patterns

class ValidateEmailUseCase {
	operator fun invoke(email: String): ValidationResult {
		if(email.isBlank()) {
			return ValidationResult(
				successful = false,
				errorMessage = "Email can't be blank"
			)
		}
		if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
			return ValidationResult(
				successful = false,
				errorMessage = "Email is not valid"
			)
		}
		return ValidationResult(
			successful = true
		)
	}
}