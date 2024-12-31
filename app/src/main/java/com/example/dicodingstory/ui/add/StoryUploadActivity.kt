package com.example.dicodingstory.ui.add

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.dicodingstory.R
import com.example.dicodingstory.databinding.ActivityUploadStoryBinding
import com.example.dicodingstory.di.Injection
import com.example.dicodingstory.ui.story.StoryListActivity
import com.example.dicodingstory.data.StoryResult

class StoryUploadActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUploadStoryBinding
    private val viewModel: StoryUploadViewModel by viewModels {
        StoryUploadViewModelFactory(Injection.injectRepository(this), this)
    }

    private val permissionRequestLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (!granted) {
                Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show()
            }
        }

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
            if (uri != null) {
                viewModel.imageUri = uri
                viewModel.selectedFile = convertUriToFile(uri, this)
                binding.imagePreview.setImageURI(viewModel.imageUri)
            } else {
                Toast.makeText(this, R.string.no_image_selected, Toast.LENGTH_SHORT).show()
            }
        }

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                viewModel.selectedFile = viewModel.imageUri?.let { convertUriToFile(it, this) }
                binding.imagePreview.setImageURI(viewModel.imageUri)
            } else {
                viewModel.imageUri = null
                Toast.makeText(this, R.string.camera_failed, Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = getString(R.string.upload_story)

        viewModel.imageUri?.let {
            binding.imagePreview.setImageURI(viewModel.imageUri)
        }

        if (!arePermissionsGranted()) {
            permissionRequestLauncher.launch(REQUIRED_PERMISSION)
        }

        binding.buttonCamera.setOnClickListener {
            viewModel.imageUri = generateImageUri(this)
            cameraLauncher.launch(viewModel.imageUri!!)
        }

        binding.buttonGallery.setOnClickListener {
            galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.buttonUploadStory.setOnClickListener {
            val description = binding.storyDescriptionInput.text.toString()
            val file = viewModel.selectedFile ?: return@setOnClickListener

            if (description.isBlank()) {
                Toast.makeText(this, R.string.description_empty, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val compressedFile = file.compressImage()
            viewModel.uploadStory(description, compressedFile)
        }
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.uploadResult.observe(this) { result ->
            when (result) {
                is StoryResult.Loading -> binding.progressBar.visibility = View.VISIBLE
                is StoryResult.Success -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, R.string.story_uploaded, Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, StoryListActivity::class.java).apply {
                        putExtra("REFRESH_LIST", true)
                    }
                    startActivity(intent)
                    finish()
                }
                is StoryResult.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, result.errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun arePermissionsGranted() = ContextCompat.checkSelfPermission(
        this, REQUIRED_PERMISSION
    ) == PackageManager.PERMISSION_GRANTED

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}
