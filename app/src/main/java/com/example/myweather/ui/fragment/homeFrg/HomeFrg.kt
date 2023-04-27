package com.example.myweather.ui.fragment.homeFrg

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.example.myweather.BuildConfig
import com.example.myweather.R
import com.example.myweather.data.apiEntity.WeatherEntity
import com.example.myweather.data.liveData.StateData
import com.example.myweather.databinding.FragmentHomeFrgBinding
import com.example.myweather.ui.activity.seeFiveDayActivity.SeeFiveDayActivity
import com.example.myweather.ui.base.BaseFragment
import com.example.myweather.util.Constants.percentExtensions
import com.example.myweather.util.Constants.pngExtensions
import com.example.myweather.util.LoaderDialog
import dagger.hilt.android.AndroidEntryPoint
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Calendar

@AndroidEntryPoint
class HomeFrg : BaseFragment<FragmentHomeFrgBinding>() {
    private val homeFrgViewModel: HomeFrgViewModel by activityViewModels()
    private lateinit var adapterHome: AdapterHome

    private var arrWeatherDay = mutableListOf<WeatherEntity>()

    override fun getViewBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ) = FragmentHomeFrgBinding.inflate(inflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapterHome = AdapterHome()

        super.onViewCreated(view, savedInstanceState)

        LoaderDialog.createDialog(requireActivity())

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

        binding.rcvWeather.apply {
            adapter = adapterHome
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
                            LoaderDialog.dismiss()
                            val temple =
                                DecimalFormat("#.#").format((weatherEntity.main.temp - 273.15))
                            val templeMin =
                                DecimalFormat("#.#").format((weatherEntity.main.tempMin - 273.15))
                            val templeMax =
                                DecimalFormat("#.#").format((weatherEntity.main.tempMax - 273.15))
                            val wind = DecimalFormat("#.#").format((weatherEntity.wind.speed))
                            val humidity =
                                DecimalFormat("#.#").format((weatherEntity.main.humidity))

                            binding.tvLocal.text = weatherEntity.name
                            binding.tvStatus.text = weatherEntity.weather[0].description

                            binding.tvTemp.text =
                                requireContext().getString(R.string.template, temple)
                            binding.tvTempMin.text =
                                requireContext().getString(R.string.temMin, templeMin)
                            binding.tvTempMax.text =
                                requireContext().getString(R.string.temMax, templeMax)
                            binding.tvWind.text = requireContext().getString(R.string.wind, wind)
                            binding.tvHumidity.text = requireContext().getString(R.string.humidity, "$humidity$percentExtensions")

                            Glide.with(requireActivity())
                                .load("${BuildConfig.BASE_GET_IMAGE}${weatherEntity.weather[0].icon}.$pngExtensions")
                                .into(binding.imgPreviewWeather)
                        }
                    }
                }

                StateData.DataStatus.ERROR -> {
                    LoaderDialog.dismiss()
                }

                else -> {
                    LoaderDialog.dismiss()
                }
            }
        }

        homeFrgViewModel.weatherDelDayLiveData.observe(viewLifecycleOwner) { status ->
            when (status.getStatus()) {
                StateData.DataStatus.LOADING -> {}
                StateData.DataStatus.SUCCESS -> {
                    LoaderDialog.dismiss()
                    status.getData().let { weatherFiveDayEntity ->
                        if (weatherFiveDayEntity != null) {
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

                            adapterHome.setList(arrWeatherDay)
                        }
                    }
                }

                StateData.DataStatus.ERROR -> {
                    LoaderDialog.dismiss()
                }

                else -> {
                    LoaderDialog.dismiss()
                }
            }
        }
    }
}