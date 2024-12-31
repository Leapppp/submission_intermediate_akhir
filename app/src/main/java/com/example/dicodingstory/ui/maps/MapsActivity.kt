package com.example.dicodingstory.ui.maps

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.dicodingstory.R
import com.example.dicodingstory.data.StoryResult
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.dicodingstory.databinding.ActivityMapsBinding
import com.example.dicodingstory.di.Injection

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private lateinit var activityBinding: ActivityMapsBinding
    private lateinit var mapsViewModel: MapsViewModel

    private val locationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                enableUserLocation()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityBinding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(activityBinding.root)

        val repository = Injection.injectRepository(this)
        val viewModelFactory = MapsViewModelFactory(repository)
        mapsViewModel = ViewModelProvider(this, viewModelFactory)[MapsViewModel::class.java]

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        observeLocationData()
    }

    private fun observeLocationData() {
        mapsViewModel.locationStories.observe(this) { result ->
            when (result) {
                is StoryResult.Loading -> {
                }
                is StoryResult.Success -> {
                    result.data.forEach { story ->
                        val coordinates = LatLng(story.lat!!, story.lon!!)
                        googleMap.addMarker(
                            MarkerOptions()
                                .position(coordinates)
                                .title(story.name)
                                .snippet(story.description)
                        )
                    }
                    val firstCoordinates = LatLng(0.143136, 118.7371783)
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstCoordinates, 5f))
                }
                is StoryResult.Error -> {
                    Toast.makeText(this, result.errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap.uiSettings.isZoomControlsEnabled = true
        mapsViewModel.loadStoriesWithLocation()
        enableUserLocation()
    }

    private fun enableUserLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            googleMap.isMyLocationEnabled = true
        } else {
            locationPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
}
