package com.example.myweather.ui.activity.seeFiveDayActivity

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myweather.BuildConfig
import com.example.myweather.R
import com.example.myweather.data.apiEntity.WeatherEntity
import com.example.myweather.databinding.ItemWeatherBinding
import com.example.myweather.databinding.ItemWeatherFiveDayBinding
import com.example.myweather.util.Constants
import java.text.DecimalFormat

class SeeFiveDayAdapter : RecyclerView.Adapter<SeeFiveDayAdapter.ViewHolder>() {
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
            ItemWeatherFiveDayBinding.inflate(
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

    inner class ViewHolder(private val binding: ItemWeatherFiveDayBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SimpleDateFormat")
        fun onBinding(data: WeatherEntity) {
            val temple = DecimalFormat("#.#").format((data.main.temp - 273.15))

            binding.tvTime.text = data.dt_txt

            Glide.with(binding.root.context)
                .load("${BuildConfig.BASE_GET_IMAGE}${data.weather[0].icon}.${Constants.pngExtensions}")
                .into(binding.imgWeather)
            binding.tvTemp.text = binding.root.context.getString(R.string.template, temple)
        }
    }
}