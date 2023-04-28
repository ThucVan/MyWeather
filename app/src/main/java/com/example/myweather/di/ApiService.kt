package com.example.myweather.di

import com.example.myweather.data.apiEntity.WeatherEntity
import com.example.myweather.data.apiEntity.WeatherFiveDayEntity
import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("weather?")
    fun getWeather(
        @Query("lat") lat: Double, @Query("lon") lon: Double, @Query("appid") apiId: String
    ): Flow<WeatherEntity>

    @GET("forecast?")
    fun getWeatherFiveDay(
        @Query("lat") lat: Double, @Query("lon") lon: Double, @Query("appid") apiId: String
    ): Flow<WeatherFiveDayEntity>
}