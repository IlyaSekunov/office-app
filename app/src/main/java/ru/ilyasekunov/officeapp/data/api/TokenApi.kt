package ru.ilyasekunov.officeapp.data.api

interface TokenApi {
    suspend fun token(): String?
    suspend fun putToken(token: String)
    suspend fun deleteToken()
}