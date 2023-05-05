package com.example.myweather.ui.home

import com.example.myweather.data.apiEntity.WeatherEntity
import com.example.myweather.data.apiEntity.WeatherFiveDayEntity
import com.example.myweather.data.liveData.MutableStateLiveData
import com.example.myweather.repository.Repository
import com.example.myweather.base.BaseViewModel
import com.example.myweather.util.flow.collectAsSateLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeFrgViewModel @Inject constructor(
    private val mainRepository: Repository
) : BaseViewModel() {

    val weatherLiveData = MutableStateLiveData<WeatherEntity>()
    val weatherFiveDayLiveData = MutableStateLiveData<WeatherFiveDayEntity>()

    fun getWeather(lat : Double, lon : Double, apiId : String){
        weatherLiveData.postLoading()
        bgScope.launch {
            mainRepository.getWeather(lat, lon, apiId).collectAsSateLiveData(weatherLiveData)
            mainRepository.getWeatherFiveDay(lat, lon, apiId).collectAsSateLiveData(weatherFiveDayLiveData)
        }
    }
}