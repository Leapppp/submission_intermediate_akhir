package com.example.dicodingstory.ui.story

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dicodingstory.R
import com.example.dicodingstory.R.id.buttonMaps
import com.example.dicodingstory.databinding.ActivityStoryListBinding
import com.example.dicodingstory.di.Injection
import com.example.dicodingstory.ui.add.StoryUploadActivity
import com.example.dicodingstory.ui.auth.AuthViewModel
import com.example.dicodingstory.ui.auth.AuthViewModelFactory
import com.example.dicodingstory.ui.auth.LoginActivity
import com.example.dicodingstory.ui.maps.MapsActivity

class StoryListActivity : AppCompatActivity() {

    private lateinit var activityBinding: ActivityStoryListBinding
    private lateinit var authenticationViewModel: AuthViewModel
    private lateinit var storyFeedViewModel: StoryFeedViewModel
    private lateinit var storyRecyclerAdapter: StoryRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityBinding = ActivityStoryListBinding.inflate(layoutInflater)
        setContentView(activityBinding.root)

        val repository = Injection.injectRepository(this)
        authenticationViewModel = ViewModelProvider(this, AuthViewModelFactory(repository))[AuthViewModel::class.java]
        storyFeedViewModel = ViewModelProvider(this, StoryFeedViewModelFactory(repository))[StoryFeedViewModel::class.java]

        configureRecyclerView()
        monitorStoryFeed()

        activityBinding.buttonAddStory.setOnClickListener {
            startActivity(Intent(this, StoryUploadActivity::class.java))
        }
    }

    private fun monitorStoryFeed() {
        storyFeedViewModel.storyData.observe(this) { pagingData ->
            storyRecyclerAdapter.submitData(lifecycle, pagingData)
        }

        storyRecyclerAdapter.addLoadStateListener { loadState ->
            activityBinding.progressLoader.isVisible = loadState.source.refresh is LoadState.Loading
            activityBinding.textError.isVisible = loadState.source.refresh is LoadState.Error
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logout -> {
                authenticationViewModel.logoutUser()
                Toast.makeText(this, getString(R.string.logout_success_message), Toast.LENGTH_SHORT).show()
                val loginIntent = Intent(this, LoginActivity::class.java)
                startActivity(loginIntent)
                finish()
                true
            }
            buttonMaps -> {
                startActivity(Intent(this, MapsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun configureRecyclerView() {
        storyRecyclerAdapter = StoryRecyclerAdapter()
        activityBinding.recyclerStories.apply {
            adapter = storyRecyclerAdapter.withLoadStateFooter(
                footer = StoryLoadingAdapter { storyRecyclerAdapter.retry() }
            )
            layoutManager = LinearLayoutManager(this@StoryListActivity)
        }
    }
}