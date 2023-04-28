package com.example.myweather.ui.fragment.homeFrg

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.view.isGone
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.example.myweather.BuildConfig
import com.example.myweather.R
import com.example.myweather.data.apiEntity.WeatherEntity
import com.example.myweather.data.liveData.StateData
import com.example.myweather.databinding.FragmentHomeFrgBinding
import com.example.myweather.ui.activity.seeFiveDayActivity.SeeFiveDayActivity
import com.example.myweather.ui.base.BaseFragment
import com.example.myweather.util.Constants
import com.example.myweather.util.Constants.percentExtensions
import com.example.myweather.util.Constants.pngExtensions
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*


@AndroidEntryPoint
class HomeFrg : BaseFragment<FragmentHomeFrgBinding>() {
    private val homeFrgViewModel: HomeFrgViewModel by activityViewModels()
    private lateinit var homeAdapter: HomeAdapter
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var arrWeatherDay = mutableListOf<WeatherEntity>()

    private var dataTemp = ArrayList<String>()
    private var dataEntry = ArrayList<Entry>()

    private var position = 0

    override fun getViewBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ) = FragmentHomeFrgBinding.inflate(inflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        homeAdapter = HomeAdapter()

        super.onViewCreated(view, savedInstanceState)

        if (ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ), Constants.REQUEST_CODE
            )
        } else {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    homeFrgViewModel.getWeather(
                        location.latitude, location.longitude, BuildConfig.API_KEY
                    )
                }
            }
        }

        binding.rcvWeather.apply {
            adapter = homeAdapter
            setHasFixedSize(true)
        }

        binding.tvNextFiveDay.setOnClickListener {
            val intent = Intent(requireActivity(), SeeFiveDayActivity::class.java)
            startActivity(intent)
        }

        observer()
    }

    @SuppressLint("SimpleDateFormat")
    private fun observer() {
        homeFrgViewModel.weatherLiveData.observe(viewLifecycleOwner) { status ->
            when (status.getStatus()) {
                StateData.DataStatus.LOADING -> {}
                StateData.DataStatus.SUCCESS -> {
                    status.getData().let { weatherEntity ->
                        if (weatherEntity != null) {
                            val temple =
                                DecimalFormat("#.#").format((weatherEntity.main.temp - 273.15))
                            val wind = DecimalFormat("#.#").format((weatherEntity.wind.speed))
                            val humidity =
                                DecimalFormat("#.#").format((weatherEntity.main.humidity))

                            binding.tvLocal.text = weatherEntity.name
                            binding.tvStatus.text = weatherEntity.weather[0].description

                            binding.tvTemp.text = requireContext().getString(
                                R.string.template, temple
                            )
                            binding.tvWind.text = requireContext().getString(
                                R.string.wind, wind
                            )
                            binding.tvHumidity.text = requireContext().getString(
                                R.string.humidity, "$humidity$percentExtensions"
                            )

                            Glide.with(requireActivity())
                                .load("${BuildConfig.BASE_GET_IMAGE}${weatherEntity.weather[0].icon}.$pngExtensions")
                                .into(binding.imgPreviewWeather)
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

        homeFrgViewModel.weatherFiveDayLiveData.observe(viewLifecycleOwner) { status ->
            when (status.getStatus()) {
                StateData.DataStatus.LOADING -> {}
                StateData.DataStatus.SUCCESS -> {
                    position = 0
                    dataTemp.clear()
                    dataEntry.clear()
                    status.getData().let { weatherFiveDayEntity ->
                        binding.lottieAnimation.isGone = true

                        if (weatherFiveDayEntity != null) {
                            val calendar = Calendar.getInstance()
                            val sdf = SimpleDateFormat("yyyy-MM-dd")
                            val currentDate: String = sdf.format(calendar.time)

                            weatherFiveDayEntity.list.forEach { weatherEntity ->
                                val startDate = weatherEntity.dt_txt?.let { sdf.parse(it) }
                                val newDateString: String = sdf.format(startDate!!)

                                if (newDateString == currentDate) {
                                    position++
                                    arrWeatherDay.add(weatherEntity)
                                    dataTemp.add(weatherEntity.dt_txt!!)

                                    dataEntry.add(
                                        Entry(
                                            position.toFloat(),
                                            weatherEntity.main.temp.toFloat() - 273.15f
                                        )
                                    )
                                }
                            }

                            val lineDataSet =
                                LineDataSet(dataEntry, getString(R.string.templateChart))
                            val dataLine = LineData(lineDataSet)

                            binding.lineChart.data = dataLine
                            binding.lineChart.animateXY(3000, 3000)
                            homeAdapter.setList(arrWeatherDay)
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

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String?>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(
                        requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    Toast.makeText(
                        requireContext(), getString(R.string.permissionsDeny), Toast.LENGTH_LONG
                    ).show()
                    return
                }
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        homeFrgViewModel.getWeather(
                            location.latitude, location.longitude, BuildConfig.API_KEY
                        )

                        var countDownTimer: CountDownTimer? = null
                        countDownTimer = object : CountDownTimer(60000, 10) {
                            override fun onTick(millisUntilFinished: Long) {
                                try {
                                    homeFrgViewModel.getWeather(
                                        Constants.LATITUDE_HANOI,
                                        Constants.LONGITUDE_HANOI,
                                        BuildConfig.API_KEY
                                    )
                                    binding.lottieAnimation.pauseAnimation()
                                    binding.lottieAnimation.isGone = true
                                    cancel()
                                } catch (e: Exception) {
                                    binding.lottieAnimation.pauseAnimation()
                                    binding.lottieAnimation.isGone = true
                                    cancel()
                                }
                            }

                            override fun onFinish() {
                                binding.lottieAnimation.pauseAnimation()
                                binding.lottieAnimation.isGone = true
                            }
                        }
                        (countDownTimer as CountDownTimer).start()

                    } else {
                        homeFrgViewModel.getWeather(
                            Constants.LATITUDE_HANOI, Constants.LONGITUDE_HANOI, BuildConfig.API_KEY
                        )
                    }
                }
            } else {
                Toast.makeText(
                    requireContext(), getString(R.string.permissionsDeny), Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}