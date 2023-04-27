package com.example.myweather.data.apiEntity
import androidx.annotation.Keep

import com.squareup.moshi.JsonClass

import com.squareup.moshi.Json


@Keep
@JsonClass(generateAdapter = true)
data class WeatherDayEntity(
    @Json(name = "DailyForecasts")
    var dailyForecasts: List<DailyForecast>,
    @Json(name = "Headline")
    var headline: Headline
)

@Keep
@JsonClass(generateAdapter = true)
data class DailyForecast(
    @Json(name = "Date")
    var date: String,
    @Json(name = "Day")
    var day: Day,
    @Json(name = "EpochDate")
    var epochDate: Int,
    @Json(name = "Link")
    var link: String,
    @Json(name = "MobileLink")
    var mobileLink: String,
    @Json(name = "Night")
    var night: Night,
    @Json(name = "Sources")
    var sources: List<String>,
    @Json(name = "Temperature")
    var temperature: Temperature
)

@Keep
@JsonClass(generateAdapter = true)
data class Headline(
    @Json(name = "Category")
    var category: String,
    @Json(name = "EffectiveDate")
    var effectiveDate: String,
    @Json(name = "EffectiveEpochDate")
    var effectiveEpochDate: Int,
    @Json(name = "EndDate")
    var endDate: String,
    @Json(name = "EndEpochDate")
    var endEpochDate: Int,
    @Json(name = "Link")
    var link: String,
    @Json(name = "MobileLink")
    var mobileLink: String,
    @Json(name = "Severity")
    var severity: Int,
    @Json(name = "Text")
    var text: String
)

@Keep
@JsonClass(generateAdapter = true)
data class Day(
    @Json(name = "HasPrecipitation")
    var hasPrecipitation: Boolean,
    @Json(name = "Icon")
    var icon: Int,
    @Json(name = "IconPhrase")
    var iconPhrase: String,
    @Json(name = "PrecipitationIntensity")
    var precipitationIntensity: String,
    @Json(name = "PrecipitationType")
    var precipitationType: String
)

@Keep
@JsonClass(generateAdapter = true)
data class Night(
    @Json(name = "HasPrecipitation")
    var hasPrecipitation: Boolean,
    @Json(name = "Icon")
    var icon: Int,
    @Json(name = "IconPhrase")
    var iconPhrase: String
)

@Keep
@JsonClass(generateAdapter = true)
data class Temperature(
    @Json(name = "Maximum")
    var maximum: Maximum,
    @Json(name = "Minimum")
    var minimum: Minimum
)

@Keep
@JsonClass(generateAdapter = true)
data class Maximum(
    @Json(name = "Unit")
    var unit: String,
    @Json(name = "UnitType")
    var unitType: Int,
    @Json(name = "Value")
    var value: Double
)

@Keep
@JsonClass(generateAdapter = true)
data class Minimum(
    @Json(name = "Unit")
    var unit: String,
    @Json(name = "UnitType")
    var unitType: Int,
    @Json(name = "Value")
    var value: Double
)