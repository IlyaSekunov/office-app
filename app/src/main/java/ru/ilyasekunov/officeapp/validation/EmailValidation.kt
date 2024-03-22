package ru.ilyasekunov.officeapp.validation

import android.util.Patterns

enum class EmailValidationError {
    BLANK, NOT_EMAIL_PATTERN, UNAVAILABLE
}

sealed class EmailValidationResult {
    data object Success : EmailValidationResult()
    data class Failure(val error: EmailValidationError) : EmailValidationResult()
}

fun validateEmail(email: String): EmailValidationResult =
    when {
        email.isBlank() -> EmailValidationResult.Failure(EmailValidationError.BLANK)
        !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> EmailValidationResult.Failure(
            error = EmailValidationError.NOT_EMAIL_PATTERN
        )
        else -> EmailValidationResult.Success
    }