package com.example.myweather.ui.map

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.example.myweather.BuildConfig
import com.example.myweather.R
import com.example.myweather.base.BaseFragment
import com.example.myweather.databinding.FragmentMapFrgBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import java.net.MalformedURLException
import java.net.URL

class MapFragment : BaseFragment<FragmentMapFrgBinding>(), OnMapReadyCallback {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var googleMap: GoogleMap

    private val permReqLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val granted = permissions.entries.all {
                it.value
            }
            if (granted) {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        val currentLatLng = LatLng(location.latitude, location.longitude)
                        googleMap.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                currentLatLng,
                                12f
                            )
                        )
                        googleMap.addMarker(MarkerOptions().position(currentLatLng))
                    }
                }
            }
        }

    override fun getViewBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ) = FragmentMapFrgBinding.inflate(inflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        val supportMapFragment =
            childFragmentManager.findFragmentById(R.id.fragment_map) as SupportMapFragment?
        supportMapFragment!!.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        googleMap.isMyLocationEnabled = true

        if (ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permReqLauncher.launch(
                arrayOf(
                    Manifest.permission.READ_CONTACTS, Manifest.permission.ACCESS_FINE_LOCATION
                )
            )
        } else {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
                    googleMap.addMarker(MarkerOptions().position(currentLatLng))
                }
            }
        }

        val tileProvider: TileProvider = object : UrlTileProvider(256, 256) {
            override fun getTileUrl(x: Int, y: Int, zoom: Int): URL? {
                val url =
                    "http://tile.openweathermap.org/map/temp_new/$zoom/$x/$y.png?appid=${BuildConfig.API_KEY}"
                return if (!checkTileExists(x, y, zoom)) {
                    null
                } else try {
                    URL(url)
                } catch (e: MalformedURLException) {
                    throw AssertionError(e)
                }
            }

            private fun checkTileExists(x: Int, y: Int, zoom: Int): Boolean {
                val minZoom = 12
                val maxZoom = 16
                return zoom in minZoom..maxZoom
            }
        }

        googleMap.addTileOverlay(
            TileOverlayOptions().tileProvider(tileProvider)
        )
    }
}