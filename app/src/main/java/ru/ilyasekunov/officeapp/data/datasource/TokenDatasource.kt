package ru.ilyasekunov.officeapp.data.datasource

interface TokenDatasource {
    suspend fun token(): String?
    suspend fun putToken(token: String)
    suspend fun deleteToken()
}