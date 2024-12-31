package com.example.dicodingstory.ui.story

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.dicodingstory.data.Repository
import com.example.dicodingstory.response.StoryItem

class StoryFeedViewModel(private val repository: Repository) : ViewModel() {

    val storyData: LiveData<PagingData<StoryItem>> = liveData {
        val token = repository.getUserToken()
        if (!token.isNullOrEmpty()) {
            emitSource(
                repository.getStoryPagingData(token)
                    .cachedIn(viewModelScope)
            )
        } else {
            emit(PagingData.empty())
        }
    }
}