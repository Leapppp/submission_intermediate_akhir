package com.example.dicodingstory.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dicodingstory.data.Repository
import com.example.dicodingstory.data.StoryResult
import kotlinx.coroutines.launch

class AuthViewModel(private val authRepository: Repository) : ViewModel() {

    private val _loginState = MutableLiveData<StoryResult<Unit>>()
    val loginState: LiveData<StoryResult<Unit>> = _loginState

    private val _registerResult = MutableLiveData<StoryResult<Unit>>()
    val registerResult: LiveData<StoryResult<Unit>> = _registerResult

    val isLoggedIn: LiveData<Boolean> = authRepository.observeUserLoginStatus()

    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            _loginState.postValue(StoryResult.Loading)
            val result = authRepository.loginUser(email, password)
            _loginState.postValue(result)
        }
    }

    fun registerUser(name: String, email: String, password: String) {
        viewModelScope.launch {
            _registerResult.postValue(StoryResult.Loading)
            val result = authRepository.registerUser(name, email, password)
            _registerResult.postValue(result)
        }
    }

    fun logoutUser() {
        viewModelScope.launch {
            _loginState.postValue(StoryResult.Loading)
            val result = authRepository.logoutUser()
            _loginState.postValue(result)
        }
    }
}