package com.example.myweather.repository

import com.example.myweather.di.ApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Repository @Inject constructor(
    private val apiHelper: ApiService
) {
    //api
    fun getLocationKey(nameCountry : String) = apiHelper.getLocationKey(nameCountry)
    fun getWeather24H(key: String) = apiHelper.getWeather24H(key)
    fun getWeatherDay(key : String) = apiHelper.getWeatherDay(key)
}