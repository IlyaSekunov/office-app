package ru.ilyasekunov.officeapp.validation

private object PasswordValidationDefaults {
    const val PASSWORD_MIN_LENGTH = 8
    val SPEC_SYMBOLS = listOf(
        '!', '@', '$', '%', '^', '#', '&', '*', '(', ')', '[',
        ']', '{', '}', '`', '~', '+', '-', '=', '_', ';', ':', '.'
    )
}

enum class PasswordValidationError {
    BLANK, TOO_SHORT, NO_SPEC_SYMBOLS, NO_CAPITAL_LETTER
}

sealed class PasswordValidationResult {
    data object Success : PasswordValidationResult()
    data class Failure(val error: PasswordValidationError) : PasswordValidationResult()
}

fun validatePassword(password: String): PasswordValidationResult =
    when {
        password.isBlank() -> PasswordValidationResult.Failure(PasswordValidationError.BLANK)
        password.length < PasswordValidationDefaults.PASSWORD_MIN_LENGTH -> {
            PasswordValidationResult.Failure(PasswordValidationError.TOO_SHORT)
        }

        !password.any { it in PasswordValidationDefaults.SPEC_SYMBOLS } -> {
            PasswordValidationResult.Failure(PasswordValidationError.NO_SPEC_SYMBOLS)
        }

        !password.any { it in 'A'..'Z' } -> {
            PasswordValidationResult.Failure(PasswordValidationError.NO_CAPITAL_LETTER)
        }

        else -> PasswordValidationResult.Success
    }