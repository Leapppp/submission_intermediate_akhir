package com.example.dicodingstory.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.dicodingstory.R
import com.example.dicodingstory.databinding.ActivityLoginBinding
import com.example.dicodingstory.di.Injection
import com.example.dicodingstory.data.StoryResult
import com.example.dicodingstory.ui.story.StoryListActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        val repository = Injection.injectRepository(this)
        authViewModel = ViewModelProvider(this, AuthViewModelFactory(repository))[AuthViewModel::class.java]

        authViewModel.isLoggedIn.observe(this) { isLoggedIn ->
            if (isLoggedIn) {
                val intent = Intent(this, StoryListActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if (validateEmailPassword(email, password)) {
                authViewModel.loginUser(email, password)
            }
        }

        binding.registerLinkText.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
        observeViewModel()
    }

    private fun observeViewModel() {
        authViewModel.loginState.observe(this) { state ->
            when (state) {
                is StoryResult.Loading -> {
                    binding.loadingContainer.visibility = View.VISIBLE
                }
                is StoryResult.Success -> {
                    binding.loadingContainer.visibility = View.GONE
                    Toast.makeText(this, getString(R.string.login_success), Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, StoryListActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                is StoryResult.Error -> {
                    binding.loadingContainer.visibility = View.GONE
                    Toast.makeText(this, state.errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun validateEmailPassword(email: String, password: String): Boolean {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, getString(R.string.email_password_empty), Toast.LENGTH_SHORT).show()
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailEditText.error = getString(R.string.invalid_email_format)
            return false
        }
        if (!binding.passwordEditText.validatePassword()) {
            return false
        }
        return true
    }
}
