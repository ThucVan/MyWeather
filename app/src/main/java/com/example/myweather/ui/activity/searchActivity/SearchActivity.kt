package com.example.myweather.ui.activity.searchActivity

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import com.example.myweather.BuildConfig
import com.example.myweather.R
import com.example.myweather.data.liveData.StateData
import com.example.myweather.databinding.ActivitySearchBinding
import com.example.myweather.ui.base.BaseActivity
import com.example.myweather.ui.fragment.homeFrg.HomeFrgViewModel
import com.example.myweather.util.Constants.LATITUDE_HANOI
import com.example.myweather.util.Constants.LONGITUDE_HANOI
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import java.text.DecimalFormat


@AndroidEntryPoint
class SearchActivity : BaseActivity<ActivitySearchBinding>(), OnMapReadyCallback {
    private val homeFrgViewModel: HomeFrgViewModel by viewModels()
    private lateinit var lastLocation: Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var temple = ""
    private var currentLatLng = LatLng(LATITUDE_HANOI, LONGITUDE_HANOI)
    private lateinit var googleMap: GoogleMap

    override fun getViewBinding(inflater: LayoutInflater) = ActivitySearchBinding.inflate(inflater)

    override fun setUpView() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val supportMapFragment =
            supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        supportMapFragment!!.getMapAsync(this)

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.ediSearch.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                getLocation()
            }
            false
        }
    }

    private fun getLocation() {
        val searchString = binding.ediSearch.text.toString()
        val geocoder = Geocoder(this)
        try {
            val city = geocoder.getFromLocationName(searchString, 1)
            currentLatLng = LatLng(city!![0].latitude, city[0].longitude)

            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
            homeFrgViewModel.getWeather(
                currentLatLng.latitude, currentLatLng.longitude, BuildConfig.API_KEY
            )
            googleMap.addMarker(
                MarkerOptions().position(currentLatLng).title(getString(R.string.template, temple))
            )
        } catch (e: Exception) {
            Toast.makeText(this, getString(R.string.searchErr), Toast.LENGTH_LONG).show()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        observer()
        this.googleMap = googleMap
        googleMap.uiSettings.isZoomControlsEnabled = true

        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        googleMap.isMyLocationEnabled = true

        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            if (location != null) {
                lastLocation = location
                currentLatLng = LatLng(location.latitude, location.longitude)
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
                homeFrgViewModel.getWeather(
                    location.latitude, location.longitude, BuildConfig.API_KEY
                )
            } else {
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
                homeFrgViewModel.getWeather(LATITUDE_HANOI, LONGITUDE_HANOI, BuildConfig.API_KEY)
            }
        }

        googleMap.setOnMapClickListener { latLng ->
            currentLatLng = latLng
            homeFrgViewModel.getWeather(
                currentLatLng.latitude, currentLatLng.longitude, BuildConfig.API_KEY
            )
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
            googleMap.addMarker(
                MarkerOptions().position(currentLatLng).title(getString(R.string.template, temple))
            )
        }
    }

    private fun observer() {
        homeFrgViewModel.weatherLiveData.observe(this) { status ->
            when (status.getStatus()) {
                StateData.DataStatus.LOADING -> {}
                StateData.DataStatus.SUCCESS -> {
                    status.getData().let { weatherFiveDayEntity ->
                        if (weatherFiveDayEntity != null) {
                            temple =
                                DecimalFormat("#.#").format((weatherFiveDayEntity.main.temp - 273.15))

                        }
                    }
                }
                StateData.DataStatus.ERROR -> {}
                else -> {}
            }
        }
    }

}