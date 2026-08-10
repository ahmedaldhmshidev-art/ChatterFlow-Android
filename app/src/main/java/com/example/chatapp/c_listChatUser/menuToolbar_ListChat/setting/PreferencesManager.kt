package com.example.chatapp.c_listChatUser.menuToolbar_ListChat.setting

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

class PreferencesManager(
    private val context: Context
) {
    companion object {
        private val LANGUAGE_KEY = stringPreferencesKey("language")
        private val THEME_KEY = stringPreferencesKey("theme")
    }

    // حفض اللغة
    suspend fun saveLanguage(language: String) {
        context.dataStore.edit { preferences ->

            preferences[LANGUAGE_KEY] = language
        }
    }

    //
    val language: Flow<String> =
        context.dataStore.data.map { preferences ->
            preferences[LANGUAGE_KEY] ?: "English"
        }

    suspend fun saveTheme(theme: String) {
        context.dataStore.edit { preferences ->
            preferences[THEME_KEY] = theme
        }
    }

    val theme: Flow<String> =
        context.dataStore.data.map { preferences ->
            preferences[THEME_KEY] ?: "System"
        }

}