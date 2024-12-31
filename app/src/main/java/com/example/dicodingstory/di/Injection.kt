package com.example.dicodingstory.di

import android.content.Context
import com.example.dicodingstory.data.Repository
import com.example.dicodingstory.preferences.UserPreference
import com.example.dicodingstory.preferences.dataStore
import com.example.dicodingstory.retrofit.ApiConfig

object Injection {
    fun injectRepository(context: Context): Repository {
        val dataStore = context.dataStore
        val userPreferences = UserPreference.getInstance(dataStore)
        val apiService = ApiConfig.getApiService()
        return Repository(apiService, userPreferences, context)
    }
}