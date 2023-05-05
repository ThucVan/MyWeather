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
import com.example.myweather.util.Constants.PREF_LATITUDE
import com.example.myweather.util.Constants.PREF_LONGITUDE
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
    private var arrSeeFiveDay = mutableListOf<WeatherEntity>()
    private lateinit var homeAdapter: HomeAdapter
    private lateinit var seeFiveDayAdapter: SeeFiveDayAdapter

    override fun getViewBinding(inflater: LayoutInflater): ActivitySeeFiveDayBinding =
        ActivitySeeFiveDayBinding.inflate(inflater)

    override fun setUpView() {
        homeAdapter = HomeAdapter()
        seeFiveDayAdapter = SeeFiveDayAdapter()

        binding.buttonBack.setOnClickListener { finish() }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        getWeather()

        binding.rcvWeatherFiveDay.apply {
            adapter = seeFiveDayAdapter
            setHasFixedSize(true)
        }

        binding.rcvWeatherToDay.apply {
            adapter = homeAdapter
            setHasFixedSize(true)
        }

        observer()
    }

    private fun getWeather(){
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
                    SharePrefUtils.getLong(PREF_LATITUDE, 0).toDouble(), SharePrefUtils.getLong(
                        PREF_LONGITUDE, 0
                    ).toDouble(), BuildConfig.API_KEY
                )
            }
        }
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

                            if (arrSeeFiveDay.size == 0) {
                                arrSeeFiveDay.add(weatherFiveDayEntity.list[0])
                            }

                            weatherFiveDayEntity.list.forEach { weatherEntity ->
                                val startDate = weatherEntity.dt_txt?.let { sdf.parse(it) }
                                val newDateString: String = sdf.format(startDate!!)
                                if (newDateString == currentDate) {
                                    arrWeatherDay.add(weatherEntity)
                                }

                                if (startDate != arrSeeFiveDay[arrSeeFiveDay.lastIndex].dt_txt?.let {
                                        sdf.parse(
                                            it
                                        )
                                    }) {
                                    arrSeeFiveDay.add(weatherEntity)
                                }
                            }

                            homeAdapter.setList(arrWeatherDay)
                            seeFiveDayAdapter.setList(arrSeeFiveDay)
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
