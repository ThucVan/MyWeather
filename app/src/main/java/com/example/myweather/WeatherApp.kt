package com.example.myweather

import android.app.Application
import android.content.Context
import com.example.myweather.util.SharePrefUtils
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class WeatherApp : Application() {
    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        SharePrefUtils.init(context)
    }

    companion object{
        lateinit var context : Context
    }
}