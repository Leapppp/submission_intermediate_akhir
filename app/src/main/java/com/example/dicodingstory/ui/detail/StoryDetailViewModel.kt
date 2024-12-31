package com.example.dicodingstory.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dicodingstory.data.Repository
import com.example.dicodingstory.response.StoryItem
import com.example.dicodingstory.data.StoryResult
import kotlinx.coroutines.launch

class StoryDetailViewModel(private val repository: Repository) : ViewModel() {

    private val _storyDetail = MutableLiveData<StoryResult<StoryItem>>()
    val storyDetail: LiveData<StoryResult<StoryItem>> = _storyDetail

    fun loadStoryDetail(storyIdentifier: String) {
        viewModelScope.launch {
            _storyDetail.value = StoryResult.Loading
            val token = repository.getUserToken()
            if (!token.isNullOrEmpty()) {
                val result = repository.getStoryDetail(token, storyIdentifier)
                _storyDetail.value = result
            } else {
                _storyDetail.value = StoryResult.Error("Token not found. Please log in again.")
            }
        }
    }
}
