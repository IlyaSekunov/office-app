package ru.ilyasekunov.officeapp.data.datasource.remote

enum class HttpCodes(val code: Int) {
    EMAIL_VALID(200),
    EMAIL_NOT_VALID(400),
    UNAUTHORIZED(401),
    INCORRECT_CREDENTIALS(401)
}