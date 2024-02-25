package ru.ilyasekunov.officeapp.data.datasource

interface TokenDataSource {
    suspend fun token(): String?
    suspend fun putToken(token: String)
    suspend fun deleteToken()
}