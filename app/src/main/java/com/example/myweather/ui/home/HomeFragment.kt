package com.example.myweather.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.view.isGone
import androidx.fragment.app.activityViewModels
import com.example.myweather.BuildConfig
import com.example.myweather.R
import com.example.myweather.base.BaseFragment
import com.example.myweather.data.apiEntity.WeatherEntity
import com.example.myweather.data.liveData.StateData
import com.example.myweather.databinding.FragmentHomeFrgBinding
import com.example.myweather.ui.seeFiveDay.SeeFiveDayActivity
import com.example.myweather.util.Constants.PREF_LATITUDE
import com.example.myweather.util.Constants.PREF_LONGITUDE
import com.example.myweather.util.Constants.percentExtensions
import com.example.myweather.util.SharePrefUtils
import com.example.myweather.util.convertTime
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeFrgBinding>() {
    private val homeFrgViewModel: HomeFrgViewModel by activityViewModels()
    private lateinit var homeAdapter: HomeAdapter
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var arrWeatherDay = mutableListOf<WeatherEntity>()

    private lateinit var lineDataSet: LineDataSet

    private var dataEntry = ArrayList<Entry>()

    private var position = 0

    private val permReqLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val granted = permissions.entries.all {
                it.value
            }
            if (granted) {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        homeFrgViewModel.getWeather(
                            location.latitude, location.longitude, BuildConfig.API_KEY
                        )
                        SharePrefUtils.putLong(PREF_LATITUDE, location.latitude.toLong())
                        SharePrefUtils.putLong(PREF_LONGITUDE, location.longitude.toLong())
                    }
                }
            }else{
                Toast.makeText(
                    requireContext(),
                    getString(R.string.txtPermissionsDeny),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    override fun getViewBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ) = FragmentHomeFrgBinding.inflate(inflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        homeAdapter = HomeAdapter()

        super.onViewCreated(view, savedInstanceState)

        getWeather()

        initView()

        observer()
    }

    private fun initView() {
        binding.rcvWeather.apply {
            adapter = homeAdapter
            setHasFixedSize(true)
        }

        binding.tvNextFiveDay.setOnClickListener {
            val intent = Intent(requireActivity(), SeeFiveDayActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getWeather() {
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
                    homeFrgViewModel.getWeather(
                        location.latitude, location.longitude, BuildConfig.API_KEY
                    )
                    SharePrefUtils.putLong(PREF_LATITUDE, location.latitude.toLong())
                    SharePrefUtils.putLong(PREF_LONGITUDE, location.longitude.toLong())
                }
            }
        }
    }

    @SuppressLint("StringFormatMatches")
    private fun observer() {
        homeFrgViewModel.weatherLiveData.observe(viewLifecycleOwner) { status ->
            when (status.getStatus()) {
                StateData.DataStatus.LOADING -> {}
                StateData.DataStatus.SUCCESS -> {
                    status.getData().let { weatherEntity ->
                        if (weatherEntity != null) {
                            val temple =
                                DecimalFormat("#").format((weatherEntity.main.temp - 273.15))
                            val wind = DecimalFormat("#.#").format((weatherEntity.wind.speed))
                            val humidity =
                                DecimalFormat("#.#").format((weatherEntity.main.humidity))

                            binding.tvTimeToday.text = requireContext().getString(
                                R.string.txtTimeToDay, convertTime(weatherEntity.dt ?: 0, "HH:mm")
                            )

                            binding.tvLocal.text = weatherEntity.name
                            binding.tvStatus.text = weatherEntity.weather[0].description
                            binding.tvTemp.text = requireContext().getString(
                                R.string.txtTemplate, temple
                            )
                            binding.tvPressure.text = requireContext().getString(
                                R.string.txtPressure, weatherEntity.main.pressure
                            )
                            binding.tvWind.text = requireContext().getString(
                                R.string.txtWind, wind
                            )
                            binding.tvHumidity.text = requireContext().getString(
                                R.string.txtHumidity, "$humidity$percentExtensions"
                            )
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
                    dataEntry.clear()
                    status.getData().let { weatherFiveDayEntity ->
                        binding.lottieAnimation.isGone = true

                        if (weatherFiveDayEntity != null) {
                            val calendar = Calendar.getInstance()
                            val sdf = SimpleDateFormat("yyyy-MM-dd")
                            val currentDate: String = sdf.format(calendar.time)

                            weatherFiveDayEntity.list.subList(0, 4).forEach { weatherEntity ->
                                val startDate = weatherEntity.dt_txt?.let { sdf.parse(it) }
                                val newDateString: String = sdf.format(startDate!!)

                                if (newDateString == currentDate) {
                                    position++
                                    arrWeatherDay.add(weatherEntity)

                                    dataEntry.add(
                                        Entry(
                                            position.toFloat(),
                                            weatherEntity.main.temp.toFloat() - 273.15f
                                        )
                                    )
                                }
                            }

                            upDateDataLineChart(dataEntry)

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

    private fun upDateDataLineChart(dataTemp: MutableList<Entry>) {
        lineDataSet = LineDataSet(dataTemp, getString(R.string.txtLabelTemplate))

        val dataSets = ArrayList<ILineDataSet>()
        dataSets.add(lineDataSet)

        val lineData = LineData(dataSets)
        val listXValue = listOf(
            "",
            getString(R.string.txtMorning),
            getString(R.string.txtAfternoon),
            getString(R.string.txtEvening),
            getString(R.string.txtNight),
        )

        val xAxis = binding.lineChart.xAxis
        xAxis.apply {
            disableAxisLineDashedLine()
            disableGridDashedLine()
            position = XAxis.XAxisPosition.BOTTOM
            valueFormatter =
                (com.github.mikephil.charting.formatter.IndexAxisValueFormatter(listXValue))
        }

        binding.lineChart.apply {
            data = lineData
            legend.isEnabled = false
            description.isEnabled = false
            axisLeft.isEnabled = false
            axisRight.isEnabled = false
        }
    }
}