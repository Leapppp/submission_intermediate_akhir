package com.example.dicodingstory.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "preference")
class UserPreference private constructor(private val dataStore: DataStore<Preferences>) {

    private val usertokenkey = stringPreferencesKey("user_token")

    suspend fun saveUserToken(token: String) {
        dataStore.edit { preferences ->
            preferences[usertokenkey] = token
        }
    }

    fun getUserToken(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[usertokenkey]
        }
    }

    suspend fun clearUserToken() {
        dataStore.edit { preferences ->
            preferences.remove(usertokenkey)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPreference? = null
        fun getInstance(dataStore: DataStore<Preferences>): UserPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}