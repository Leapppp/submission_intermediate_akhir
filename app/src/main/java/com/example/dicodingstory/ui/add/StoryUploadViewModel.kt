package com.example.dicodingstory.ui.add

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dicodingstory.data.Repository
import com.example.dicodingstory.data.StoryResult
import kotlinx.coroutines.launch
import java.io.File

class StoryUploadViewModel(private val repository: Repository, private val savedStateHandle: SavedStateHandle) : ViewModel() {

    val uploadResult = MutableLiveData<StoryResult<Unit>>()

    var imageUri: Uri?
        get() = savedStateHandle.get<Uri>("IMAGE_URI")
        set(value) {
            savedStateHandle["IMAGE_URI"] = value
        }
    var selectedFile: File? = null

    fun uploadStory(description: String, imageFile: File, latitude: Float? = null, longitude: Float? = null) {
        viewModelScope.launch {
            val token = repository.getUserToken() ?: return@launch
            uploadResult.value = StoryResult.Loading
            val result = repository.uploadStory(description, imageFile, token, latitude, longitude)
            uploadResult.value = result
        }
    }
}
