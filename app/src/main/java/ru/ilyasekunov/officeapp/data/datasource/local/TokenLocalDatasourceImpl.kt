package ru.ilyasekunov.officeapp.data.datasource.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import ru.ilyasekunov.officeapp.data.datasource.TokenDatasource
import javax.inject.Inject

class TokenLocalDatasourceImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : TokenDatasource {

    override suspend fun token(): String? =
        dataStore.data.first()[PreferencesKeys.USER_TOKEN]

    override suspend fun putToken(token: String) {
        dataStore.edit { userPreferences ->
            userPreferences[PreferencesKeys.USER_TOKEN] = token
        }
    }

    override suspend fun deleteToken() {
        dataStore.edit { userPreferences ->
            userPreferences.clear()
        }
    }

    private object PreferencesKeys {
        val USER_TOKEN = stringPreferencesKey("user-token")
    }
}