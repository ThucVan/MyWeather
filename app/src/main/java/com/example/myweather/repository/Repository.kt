package com.example.myweather.repository

import com.example.myweather.di.ApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Repository @Inject constructor(
    private val apiHelper: ApiService
) {
    //api
    fun getWeather(lat : Double, lon : Double, apiId : String) = apiHelper.getWeather(lat , lon, apiId)
}