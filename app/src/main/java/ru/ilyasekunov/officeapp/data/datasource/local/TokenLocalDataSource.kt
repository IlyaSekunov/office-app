package ru.ilyasekunov.officeapp.data.datasource.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import ru.ilyasekunov.officeapp.data.datasource.TokenDataSource

class TokenLocalDataSource(
    private val dataStore: DataStore<Preferences>,
    private val ioDispatcher: CoroutineDispatcher
) : TokenDataSource {

    override suspend fun token(): String? =
        withContext(ioDispatcher) {
            dataStore.data.first()[PreferencesKeys.USER_TOKEN]
        }

    override suspend fun putToken(token: String) {
        withContext(ioDispatcher) {
            dataStore.edit { userPreferences ->
                userPreferences[PreferencesKeys.USER_TOKEN] = token
            }
        }
    }

    override suspend fun deleteToken() {
        withContext(ioDispatcher) {
            dataStore.edit { userPreferences ->
                userPreferences.clear()
            }
        }
    }

    private object PreferencesKeys {
        val USER_TOKEN = stringPreferencesKey("user-token")
    }
}