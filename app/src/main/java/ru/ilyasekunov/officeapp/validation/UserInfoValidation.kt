package ru.ilyasekunov.officeapp.validation

enum class UserInfoValidationError {
    BLANK
}

sealed class UserInfoValidationResult {
    data object Success : UserInfoValidationResult()
    data class Failure(val error: UserInfoValidationError) : UserInfoValidationResult()
}

fun validateUserInfo(userInfo: String): UserInfoValidationResult =
    when {
        userInfo.isBlank() -> UserInfoValidationResult.Failure(UserInfoValidationError.BLANK)
        else -> UserInfoValidationResult.Success
    }