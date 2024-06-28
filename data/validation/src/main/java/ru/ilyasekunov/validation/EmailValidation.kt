package ru.ilyasekunov.validation

import java.util.regex.Pattern

private val EMAIL_ADDRESS = Pattern.compile(
    "[a-zA-Z0-9+._%\\-]{1,256}" +
            "@" +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
            "(" +
            "\\." +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
            ")+"
)

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
        email.notMatchesEmailPattern() -> EmailValidationResult.Failure(EmailValidationError.NOT_EMAIL_PATTERN)
        else -> EmailValidationResult.Success
    }

private fun String.notMatchesEmailPattern() = !EMAIL_ADDRESS.matcher(this).matches()