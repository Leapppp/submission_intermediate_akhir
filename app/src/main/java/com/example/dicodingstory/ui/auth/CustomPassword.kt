package com.example.dicodingstory.ui.auth

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.example.dicodingstory.R

class CustomPassword(context: Context, attrs: AttributeSet?) : AppCompatEditText(context, attrs) {

    init {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validatePasswordLength()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

    private fun validatePasswordLength() {
        val passwordLength = text?.length ?: 0
        error = if (passwordLength < 8) {
            context.getString(R.string.invalid_password)
        } else {
            null
        }
    }

    fun validatePassword(): Boolean {
        return if ((text?.length ?: 0) < 8) {
            error = context.getString(R.string.invalid_password)
            false
        } else {
            true
        }
    }
}