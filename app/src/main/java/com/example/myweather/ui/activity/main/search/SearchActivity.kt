package com.example.myweather.ui.activity.main.search

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.location.Geocoder
import android.location.Location
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.myweather.BuildConfig
import com.example.myweather.R
import com.example.myweather.base.BaseActivity
import com.example.myweather.data.liveData.StateData
import com.example.myweather.databinding.ActivitySearchBinding
import com.example.myweather.ui.fragment.home.HomeFrgViewModel
import com.example.myweather.util.Constants
import com.example.myweather.util.Constants.LATITUDE
import com.example.myweather.util.Constants.LONGITUDE
import com.example.myweather.util.SharePrefUtils
import com.example.myweather.util.viewToBitmap
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
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
    private var iconWeather = ""
    private var nameCity = ""
    private val latitude = SharePrefUtils.getLong(LATITUDE, 0)
    private val longitude = SharePrefUtils.getLong(LONGITUDE, 0)
    private var currentLatLng = LatLng(latitude.toDouble(), longitude.toDouble())
    private lateinit var googleMap: GoogleMap

    override fun getViewBinding(inflater: LayoutInflater) = ActivitySearchBinding.inflate(inflater)

    override fun setUpView() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val supportMapFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentMap) as SupportMapFragment?
        supportMapFragment!!.getMapAsync(this)

        binding.buttonBack.setOnClickListener {
            finish()
        }

        binding.editSearch.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                getLocation()
            }
            false
        }
    }

    private fun getLocation() {
        val searchString = binding.editSearch.text.toString()
        val geocoder = Geocoder(this)
        try {
            val city = geocoder.getFromLocationName(searchString, 1)
            currentLatLng = LatLng(city!![0].latitude, city[0].longitude)

            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 20f))
            homeFrgViewModel.getWeather(
                currentLatLng.latitude, currentLatLng.longitude, BuildConfig.API_KEY
            )
        } catch (e: Exception) {
            Toast.makeText(this, getString(R.string.txtSearchErr), Toast.LENGTH_LONG).show()
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
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 20f))
                homeFrgViewModel.getWeather(
                    location.latitude, location.longitude, BuildConfig.API_KEY
                )
            } else {
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 20f))
                homeFrgViewModel.getWeather(
                    latitude.toDouble(), longitude.toDouble(), BuildConfig.API_KEY
                )
            }
        }

        getMakerView()
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
                            iconWeather = weatherFiveDayEntity.weather[0].icon
                            nameCity = weatherFiveDayEntity.name.toString()

                            getMakerView()
                        }
                    }
                }
                StateData.DataStatus.ERROR -> {}
                else -> {}
            }
        }
    }

    @SuppressLint("MissingInflatedId")
    private fun getMakerView() {
        val makerView: View = (getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
            R.layout.maker_view, null
        )
        val tvCity = makerView.findViewById<TextView>(R.id.tvCity)
        val layoutMaker = makerView.findViewById<ConstraintLayout>(R.id.layoutMaker)
        val imvMaker = makerView.findViewById<ImageView>(R.id.imvMaker)
        val tvTemp = makerView.findViewById<TextView>(R.id.tvTemp)
        tvTemp.text = getString(R.string.txtTemplate, temple)
        tvCity.text = nameCity
        Glide.with(this).asBitmap()
            .load("${BuildConfig.BASE_GET_IMAGE}$iconWeather.${Constants.pngExtensions}")
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(
                    resource: Bitmap, transition: Transition<in Bitmap>?
                ) {
                    imvMaker.setImageBitmap(resource)
                    makerView.measure(
                        View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED
                    )
                    makerView.layout(
                        0, 0, makerView.measuredWidth, makerView.measuredHeight
                    )
                    makerView.buildDrawingCache()
                    val bmContent = Bitmap.createScaledBitmap(
                        viewToBitmap(layoutMaker), layoutMaker.width, layoutMaker.height, false
                    )
                    val smallMakerIcon = BitmapDescriptorFactory.fromBitmap(bmContent)

                    googleMap.addMarker(
                        MarkerOptions().position(currentLatLng).icon(smallMakerIcon)
                    )
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 20f))
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(currentLatLng))
                }

                override fun onLoadCleared(placeholder: Drawable?) {

                }
            })
    }
}