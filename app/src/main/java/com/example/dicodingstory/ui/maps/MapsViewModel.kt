package com.example.dicodingstory.ui.maps

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dicodingstory.data.Repository
import com.example.dicodingstory.response.StoryItem
import com.example.dicodingstory.data.StoryResult
import kotlinx.coroutines.launch

class MapsViewModel(private val repository: Repository) : ViewModel() {

    private val _locationStories = MutableLiveData<StoryResult<List<StoryItem>>>()
    val locationStories: MutableLiveData<StoryResult<List<StoryItem>>> get() = _locationStories

    fun loadStoriesWithLocation() {
        viewModelScope.launch {
            val userToken = repository.getUserToken()
            if (userToken != null) {
                _locationStories.value = StoryResult.Loading
                _locationStories.value = repository.getStoryLocation(userToken)
            } else {
                _locationStories.value = StoryResult.Error("User token is unavailable.")
            }
        }
    }
}
