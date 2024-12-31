package com.example.dicodingstory.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.dicodingstory.R
import com.example.dicodingstory.data.StoryResult
import com.example.dicodingstory.databinding.ActivitySignupBinding
import com.example.dicodingstory.di.Injection

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val repository = Injection.injectRepository(this)
        authViewModel = ViewModelProvider(
            this,
            AuthViewModelFactory(repository)
        )[AuthViewModel::class.java]

        binding.signupButton.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if (validateInput(name, email, password)) {
                authViewModel.registerUser(name, email, password)
            }
        }

        binding.loginLinkText.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        authViewModel.registerResult.observe(this) { result ->
            when (result) {
                is StoryResult.Loading -> binding.loadingContainer.visibility = View.VISIBLE
                is StoryResult.Success -> {
                    binding.loadingContainer.visibility = View.GONE
                    Toast.makeText(this, getString(R.string.regist_success), Toast.LENGTH_SHORT).show()
                    finish()
                }
                is StoryResult.Error -> {
                    binding.loadingContainer.visibility = View.GONE
                    Toast.makeText(this, result.errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun validateInput(name: String, email: String, password: String): Boolean {
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, getString(R.string.all_field), Toast.LENGTH_SHORT).show()
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, getString(R.string.invalid_email), Toast.LENGTH_SHORT).show()
            return false
        }
        if (!binding.passwordEditText.validatePassword()) {
            return false
        }
        return true
    }
}
