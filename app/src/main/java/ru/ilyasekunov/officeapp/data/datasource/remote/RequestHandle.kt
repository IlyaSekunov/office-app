package ru.ilyasekunov.officeapp.data.datasource.remote

import retrofit2.HttpException
import retrofit2.Response

fun <T> Response<T>.toResult(): Result<T> =
    if (isSuccessful) {
        Result.success(body()!!)
    } else {
        Result.failure(HttpException(this))
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

