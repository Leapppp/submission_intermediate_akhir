package com.example.dicodingstory.data

sealed class StoryResult<out T> {
    data class Success<out T>(val data: T) : StoryResult<T>()
    data class Error(val errorMessage: String) : StoryResult<Nothing>()
    data object Loading : StoryResult<Nothing>()
}
