package com.example.dicodingstory.ui.detail

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.dicodingstory.R
import com.example.dicodingstory.databinding.ActivityDetailStoryBinding
import com.example.dicodingstory.di.Injection
import com.example.dicodingstory.data.StoryResult

class StoryDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailStoryBinding
    private lateinit var detailViewModel: StoryDetailViewModel

    companion object {
        const val EXTRA_ID = "extra_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repository = Injection.injectRepository(this)
        detailViewModel = ViewModelProvider(this, StoryDetailViewModelFactory(repository))[StoryDetailViewModel::class.java]

        val storyIdentifier = intent.getStringExtra(EXTRA_ID)
        if (storyIdentifier != null) {
            observeStoryDetail()
            detailViewModel.loadStoryDetail(storyIdentifier)
        } else {
            Toast.makeText(this, getString(R.string.error_story_not_found), Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun observeStoryDetail() {
        detailViewModel.storyDetail.observe(this) { result ->
            when (result) {
                is StoryResult.Loading ->  binding.progressBar.visibility = View.VISIBLE
                is StoryResult.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.tvStoryTitle.text = result.data.name
                    binding.tvStoryDescription.text = result.data.description
                    Glide.with(this)
                        .load(result.data.photoUrl)
                        .into(binding.ivStoryImage)
                }
                is StoryResult.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, result.errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
