package ru.ilyasekunov.officeapp.data.datasource.remote

import retrofit2.HttpException
import retrofit2.Response
import ru.ilyasekunov.officeapp.exceptions.HttpForbiddenException

fun <T> Response<T>.toResult(): Result<T> =
    when {
        isSuccessful -> Result.success(body()!!)
        code() == 403 -> Result.failure(HttpForbiddenException())
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

