package com.example.myweather.di

import com.example.myweather.BuildConfig
import com.example.myweather.data.apiEntity.CountryEntityItem
import com.example.myweather.data.apiEntity.WeatherDayEntity
import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("lat={lat}&lon={lon}&appid=${BuildConfig.API_KEY}")
    fun getWeather24H(
        @Path("lat") lat: Int,
        @Path("lon") lon: Int
    ) : Flow<WeatherDayEntity>

    @GET("forecasts/v1/hourly/daily/10day/{locationKey}?apikey=${BuildConfig.API_KEY}")
    fun getWeatherDay(
        @Path("locationKey") locationKey: String
    ) : Flow<WeatherDayEntity>

    @GET("locations/v1/cities/search?apikey=${BuildConfig.API_KEY}")
    fun getLocationKey(
        @Query("q") nameCountry : String
    ) : Flow<List<CountryEntityItem>?>
}