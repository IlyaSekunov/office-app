package ru.ilyasekunov.officeapp.data.datasource.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

enum class TokenType {
    ACCESS, REFRESH
}

class TokenLocalDataSource(
    private val dataStore: DataStore<Preferences>,
    private val ioDispatcher: CoroutineDispatcher
) {
    suspend fun token(type: TokenType): String? =
        withContext(ioDispatcher) {
            when (type) {
                TokenType.ACCESS -> {
                    dataStore.data.first()[PreferencesKeys.ACCESS_TOKEN]
                }

                TokenType.REFRESH -> {
                    dataStore.data.first()[PreferencesKeys.REFRESH_TOKEN]
                }
            }
        }

    suspend fun putToken(type: TokenType, token: String) {
        withContext(ioDispatcher) {
            when (type) {
                TokenType.ACCESS -> {
                    dataStore.edit {
                        it[PreferencesKeys.ACCESS_TOKEN] = token
                    }
                }

                TokenType.REFRESH -> {
                    dataStore.edit {
                        it[PreferencesKeys.REFRESH_TOKEN] = token
                    }
                }
            }
        }
    }

    suspend fun deleteToken(type: TokenType) {
        withContext(ioDispatcher) {
            when (type) {
                TokenType.ACCESS -> {
                    dataStore.edit {
                        it.remove(PreferencesKeys.ACCESS_TOKEN)
                    }
                }

                TokenType.REFRESH -> {
                    dataStore.edit {
                        it.remove(PreferencesKeys.REFRESH_TOKEN)
                    }
                }
            }
        }
    }

    private object PreferencesKeys {
        val ACCESS_TOKEN = stringPreferencesKey("access-token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh-token")
    }
}