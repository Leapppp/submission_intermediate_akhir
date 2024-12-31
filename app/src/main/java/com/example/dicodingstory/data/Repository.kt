package com.example.dicodingstory.data

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.dicodingstory.R
import com.example.dicodingstory.preferences.UserPreference
import com.example.dicodingstory.request.LoginRequest
import com.example.dicodingstory.request.SignUpRequest
import com.example.dicodingstory.response.StoryItem
import com.example.dicodingstory.retrofit.ApiService
import com.example.dicodingstory.ui.story.StoryPagingResource
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class Repository(private val apiService: ApiService, private val userPreferences: UserPreference, private val context: Context
) {
    fun observeUserLoginStatus(): LiveData<Boolean> {
        return userPreferences.getUserToken()
            .map { !it.isNullOrEmpty() }
            .asLiveData()
    }

    suspend fun loginUser(email: String, password: String): StoryResult<Unit> {
        return try {
            val response = apiService.login(LoginRequest(email, password))
            val token = response.loginResult?.token
            if (!token.isNullOrEmpty()) {
                userPreferences.saveUserToken(token)
                StoryResult.Success(Unit)
            } else {
                StoryResult.Error(context.getString(R.string.error_no_token))
            }
        } catch (e: Exception) {
            StoryResult.Error(e.message.toString())
        }
    }

    suspend fun registerUser(name: String, email: String, password: String): StoryResult<Unit> {
        return try {
            apiService.register(SignUpRequest(name, email, password))
            StoryResult.Success(Unit)
        } catch (e: Exception) {
            StoryResult.Error(e.message.toString())
        }
    }

    suspend fun logoutUser(): StoryResult<Unit> {
        return try {
            userPreferences.clearUserToken()
            StoryResult.Success(Unit)
        } catch (e: Exception) {
            StoryResult.Error(e.message.toString())
        }
    }

    suspend fun getUserToken(): String? {
        return userPreferences.getUserToken().firstOrNull()
    }

    suspend fun getStoryDetail(token: String, storyId: String): StoryResult<StoryItem> {
        return try {
            val response = apiService.getStoryDetail("Bearer $token", storyId)
            if (!response.error && response.story != null) {
                StoryResult.Success(response.story)
            } else {
                StoryResult.Error(response.message)
            }
        } catch (e: Exception) {
            StoryResult.Error(e.message.toString())
        }
    }

    suspend fun uploadStory(description: String, imageFile: File, token: String, lat: Float? = null, lon: Float? = null
    ): StoryResult<Unit> {
        return try {
            val requestDescription = description.toRequestBody("text/plain".toMediaTypeOrNull())

            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart = MultipartBody.Part.createFormData("photo", imageFile.name, requestImageFile)

            val requestLat = lat?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())
            val requestLon = lon?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())

            val response = apiService.uploadStory("Bearer $token", requestDescription, imageMultipart, requestLat, requestLon)

            if (!response.error) {
                StoryResult.Success(Unit)
            } else {
                StoryResult.Error(response.message)
            }
        } catch (e: Exception) {
            StoryResult.Error(e.message.toString())
        }
    }

    suspend fun getStoryLocation(token: String): StoryResult<List<StoryItem>> {
        return try {
            val response = apiService.getAllStories("Bearer $token", location = 1)
            if (!response.error) {
                StoryResult.Success(response.listStory!!.filter { it.lat != null && it.lon != null })
            } else {
                StoryResult.Error(response.message)
            }
        } catch (e: Exception) {
            StoryResult.Error(e.message.toString())
        }
    }

    fun getStoryPagingData(token: String): LiveData<PagingData<StoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                StoryPagingResource(apiService, token)
            }
        ).liveData
    }
}