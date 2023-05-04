package com.example.myweather.ui.activity.main.seeFiveDay

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.view.isGone
import com.example.myweather.BuildConfig
import com.example.myweather.data.apiEntity.WeatherEntity
import com.example.myweather.data.liveData.StateData
import com.example.myweather.databinding.ActivitySeeFiveDayBinding
import com.example.myweather.base.BaseActivity
import com.example.myweather.ui.fragment.home.HomeAdapter
import com.example.myweather.ui.fragment.home.HomeFrgViewModel
import com.example.myweather.util.Constants.LATITUDE
import com.example.myweather.util.Constants.LONGITUDE
import com.example.myweather.util.SharePrefUtils
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class SeeFiveDayActivity : BaseActivity<ActivitySeeFiveDayBinding>() {
    private val homeFrgViewModel: HomeFrgViewModel by viewModels()

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var arrWeatherDay = mutableListOf<WeatherEntity>()
    private lateinit var homeAdapter: HomeAdapter
    private lateinit var seeFiveDayAdapter: SeeFiveDayAdapter

    override fun getViewBinding(inflater: LayoutInflater): ActivitySeeFiveDayBinding =
        ActivitySeeFiveDayBinding.inflate(inflater)

    override fun setUpView() {
        homeAdapter = HomeAdapter()
        seeFiveDayAdapter = SeeFiveDayAdapter()

        binding.buttonBack.setOnClickListener { finish() }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                homeFrgViewModel.getWeather(
                    location.latitude, location.longitude, BuildConfig.API_KEY
                )
            } else {
                homeFrgViewModel.getWeather(
                    SharePrefUtils.getLong(LATITUDE, 0).toDouble(), SharePrefUtils.getLong(
                        LONGITUDE, 0
                    ).toDouble(), BuildConfig.API_KEY
                )
            }
        }

        binding.rcvWeatherFiveDay.apply {
            adapter = seeFiveDayAdapter
            setHasFixedSize(true)
        }

        observer()
    }

    @SuppressLint("SimpleDateFormat")
    private fun observer() {
        homeFrgViewModel.weatherFiveDayLiveData.observe(this) { status ->
            when (status.getStatus()) {
                StateData.DataStatus.LOADING -> {}
                StateData.DataStatus.SUCCESS -> {
                    status.getData().let { weatherFiveDayEntity ->
                        binding.lottieAnimation.isGone = true
                        binding.lottieAnimation.pauseAnimation()

                        if (weatherFiveDayEntity != null) {
                            binding.tvLocation.text = weatherFiveDayEntity.city?.name

                            val calendar = Calendar.getInstance()
                            val sdf = SimpleDateFormat("yyyy-MM-dd")
                            val currentDate: String = sdf.format(calendar.time)

                            weatherFiveDayEntity.list.forEach { weatherEntity ->
                                val startDate = weatherEntity.dt_txt?.let { sdf.parse(it) }
                                val newDateString: String = sdf.format(startDate!!)
                                if (newDateString == currentDate) {
                                    arrWeatherDay.add(weatherEntity)
                                }
                            }

                            homeAdapter.setList(arrWeatherDay)
                            seeFiveDayAdapter.setList(weatherFiveDayEntity.list)
                        }
                    }
                }

                StateData.DataStatus.ERROR -> {
                    binding.lottieAnimation.isGone = true
                    binding.lottieAnimation.pauseAnimation()
                }

                else -> {
                    binding.lottieAnimation.isGone = true
                    binding.lottieAnimation.pauseAnimation()
                }
            }
        }
    }
}
