package com.example.myweather.data.apiEntity

import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
data class WeatherEntity(
    @Json(name = "base")
    var base: String,
    @Json(name = "clouds")
    var clouds: Clouds,
    @Json(name = "cod")
    var cod: Int,
    @Json(name = "coord")
    var coord: Coord,
    @Json(name = "dt")
    var dt: Int,
    @Json(name = "id")
    var id: Int,
    @Json(name = "main")
    var main: Main,
    @Json(name = "name")
    var name: String,
    @Json(name = "sys")
    var sys: Sys,
    @Json(name = "timezone")
    var timezone: Int,
    @Json(name = "visibility")
    var visibility: Int,
    @Json(name = "weather")
    var weather: List<Weather>,
    @Json(name = "wind")
    var wind: Wind
)

@Keep
@JsonClass(generateAdapter = true)
data class Clouds(
    @Json(name = "all")
    var all: Int
)

@Keep
@JsonClass(generateAdapter = true)
data class Coord(
    @Json(name = "lat")
    var lat: Double,
    @Json(name = "lon")
    var lon: Double
)

@Keep
@JsonClass(generateAdapter = true)
data class Main(
    @Json(name = "feels_like")
    var feelsLike: Double,
    @Json(name = "grnd_level")
    var grndLevel: Int,
    @Json(name = "humidity")
    var humidity: Int,
    @Json(name = "pressure")
    var pressure: Int,
    @Json(name = "sea_level")
    var seaLevel: Int,
    @Json(name = "temp")
    var temp: Double,
    @Json(name = "temp_max")
    var tempMax: Double,
    @Json(name = "temp_min")
    var tempMin: Double
)

@Keep
@JsonClass(generateAdapter = true)
data class Sys(
    @Json(name = "country")
    var country: String,
    @Json(name = "id")
    var id: Int,
    @Json(name = "sunrise")
    var sunrise: Int,
    @Json(name = "sunset")
    var sunset: Int,
    @Json(name = "type")
    var type: Int
)

@Keep
@JsonClass(generateAdapter = true)
data class Weather(
    @Json(name = "description")
    var description: String,
    @Json(name = "icon")
    var icon: String,
    @Json(name = "id")
    var id: Int,
    @Json(name = "main")
    var main: String
)

@Keep
@JsonClass(generateAdapter = true)
data class Wind(
    @Json(name = "deg")
    var deg: Int,
    @Json(name = "gust")
    var gust: Double,
    @Json(name = "speed")
    var speed: Double
)