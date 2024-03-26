package ru.ilyasekunov.officeapp.data.datasource.remote

import retrofit2.HttpException
import retrofit2.Response
import ru.ilyasekunov.officeapp.exceptions.HttpForbiddenException
import ru.ilyasekunov.officeapp.exceptions.HttpNotFoundException
import ru.ilyasekunov.officeapp.exceptions.IncorrectCredentialsException

fun <T> Response<T>.toResult(): Result<T> =
    when {
        isSuccessful -> Result.success(body()!!)
        code() == HttpCodes.UNAUTHORIZED -> Result.failure(HttpForbiddenException())
        code() == HttpCodes.NOT_FOUND -> Result.failure(HttpNotFoundException())
        else -> Result.failure(HttpException(this))
    }

inline fun <T> handleResponse(
    request: () -> Response<T>
): Result<T> = try {
    request().toResult()
} catch (e: Exception) {
    Result.failure(e)
}

inline fun <T> handleResult(
    request: () -> Result<T>
): Result<T> = try {
    request()
} catch (e: Exception) {
    Result.failure(e)
}

inline fun <T> handleIsEmailValidResponse(
    request: () -> Response<T>
): Result<Boolean> = try {
    val response = request()
    when (response.code()) {
        HttpCodes.EMAIL_VALID -> Result.success(true)
        HttpCodes.EMAIL_NOT_VALID -> Result.success(false)
        else -> Result.failure(HttpException(response))
    }
} catch (e: Exception) {
    Result.failure(e)
}

inline fun <T> handleLoginResponse(
    request: () -> Response<T>
): Result<T> = try {
    val response = request()
    when {
        response.isSuccessful -> Result.success(response.body()!!)
        response.code() == HttpCodes.INCORRECT_CREDENTIALS -> {
            Result.failure(IncorrectCredentialsException())
        }
        else -> Result.failure(HttpException(response))
    }
} catch (e: Exception) {
    Result.failure(e)
}