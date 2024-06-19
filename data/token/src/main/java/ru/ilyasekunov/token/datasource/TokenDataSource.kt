package ru.ilyasekunov.token.datasource

interface TokenDataSource {
    suspend fun token(type: TokenType): String?
    suspend fun putToken(type: TokenType, token: String)
    suspend fun deleteToken(type: TokenType)
}

enum class TokenType {
    ACCESS, REFRESH
}