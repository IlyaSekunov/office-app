package ru.ilyasekunov.officeapp.data

sealed class ResponseResult<T>(open val value: T? = null, open val message: String? = null) {
    companion object {
        fun <T> success(value: T): ResponseResult<T> = Success(value)
        fun <T> failure(message: String): ResponseResult<T> = Failure(message)
    }

    data class Success<T>(override val value: T) : ResponseResult<T>(value)
    data class Failure<T>(override val message: String) : ResponseResult<T>(message = message)
}