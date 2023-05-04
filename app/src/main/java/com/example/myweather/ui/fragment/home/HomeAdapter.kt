package com.example.myweather.ui.fragment.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myweather.BuildConfig
import com.example.myweather.R
import com.example.myweather.data.apiEntity.WeatherEntity
import com.example.myweather.databinding.ItemWeatherBinding
import com.example.myweather.util.Constants
import com.example.myweather.util.convertTime
import java.text.DecimalFormat
import java.util.*

class HomeAdapter : RecyclerView.Adapter<HomeAdapter.ViewHolder>() {
    var arrWeather = mutableListOf<WeatherEntity>()

    fun setList(newList: List<WeatherEntity>) {
        arrWeather.clear()
        arrWeather.addAll(newList)
        this.notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ViewHolder {
        return ViewHolder(
            ItemWeatherBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        arrWeather.getOrNull(position)?.let { holder.onBinding(it) }
    }

    override fun getItemCount(): Int {
        return arrWeather.size
    }

    inner class ViewHolder(private val binding: ItemWeatherBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SimpleDateFormat")
        fun onBinding(weatherEntity: WeatherEntity) {
            val temple = DecimalFormat("#.#").format((weatherEntity.main.temp - 273.15))

            binding.tvTimeToDay.text = convertTime(weatherEntity.dt ?: 0, "HH:ss")

            Glide.with(binding.root.context)
                .load("${BuildConfig.BASE_GET_IMAGE}${weatherEntity.weather[0].icon}.${Constants.pngExtensions}")
                .into(binding.imvWeather)
            binding.tvTemp.text = binding.root.context.getString(R.string.txtTemplate, temple)
        }
    }
}