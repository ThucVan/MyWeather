package com.example.myweather.ui.fragment.homeFrg

import com.example.myweather.data.apiEntity.WeatherDayEntity
import com.example.myweather.data.liveData.MutableStateLiveData
import com.example.myweather.repository.Repository
import com.example.myweather.ui.base.BaseViewModel
import com.example.myweather.util.flow.collectAsSateLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeFrgViewModel @Inject constructor(
    private val mainRepository: Repository
) : BaseViewModel() {
    val weatherLiveDataDay = MutableStateLiveData<WeatherDayEntity>()
    val weatherLiveData10Day = MutableStateLiveData<WeatherDayEntity>()

    fun getCountryLiveDataWithName(nameCountry : String){
        bgScope.launch {
            mainRepository.getLocationKey(nameCountry).collect{
                it?.get(1)?.key?.let { it1 -> mainRepository.getWeather24H(it1).collectAsSateLiveData(weatherLiveDataDay) }
                it?.get(1)?.key?.let { it1 -> mainRepository.getWeatherDay(it1).collectAsSateLiveData(weatherLiveData10Day) }
            }
        }
    }
}