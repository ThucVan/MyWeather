package com.example.myweather.data.apiEntity
import androidx.annotation.Keep

import com.squareup.moshi.JsonClass

import com.squareup.moshi.Json

@Keep
@JsonClass(generateAdapter = true)
data class CountryEntityItem(
    var administrativeArea: AdministrativeArea? = null,
    @Json(name = "com.example.myweather.data.apiEntity.Country")
    var country: Country?,
    @Json(name = "DataSets")
    var dataSets: List<String>,
    @Json(name = "EnglishName")
    var englishName: String? ="",
    @Json(name = "com.example.myweather.data.apiEntity.GeoPosition")
    var geoPosition: GeoPosition? = null,
    @Json(name = "IsAlias")
    var isAlias: Boolean? = false,
    @Json(name = "Key")
    var key: String? = "",
    @Json(name = "LocalizedName")
    var localizedName: String? = "",
    @Json(name = "PrimaryPostalCode")
    var primaryPostalCode: String? = "",
    @Json(name = "Rank")
    var rank: Int? = 0,
    @Json(name = "com.example.myweather.data.apiEntity.Region")
    var region: Region? = null,
    @Json(name = "SupplementalAdminAreas")
    var supplementalAdminAreas: List<SupplementalAdminArea>,
    @Json(name = "com.example.myweather.data.apiEntity.TimeZone")
    var timeZone: TimeZone? = null,
    @Json(name = "Type")
    var type: String? = "",
    @Json(name = "Version")
    var version: Int? = 0
)

@Keep
@JsonClass(generateAdapter = true)
data class AdministrativeArea(
    @Json(name = "CountryID")
    var countryID: String? = "",
    @Json(name = "EnglishName")
    var englishName: String? = "",
    @Json(name = "EnglishType")
    var englishType: String? = "",
    @Json(name = "ID")
    var iD: String? = "",
    @Json(name = "Level")
    var level: Int? = 0,
    @Json(name = "LocalizedName")
    var localizedName: String? = "",
    @Json(name = "LocalizedType")
    var localizedType: String? = ""
)

@Keep
@JsonClass(generateAdapter = true)
data class Country(
    @Json(name = "EnglishName")
    var englishName: String,
    @Json(name = "ID")
    var iD: String,
    @Json(name = "LocalizedName")
    var localizedName: String
)

@Keep
@JsonClass(generateAdapter = true)
data class GeoPosition(
    @Json(name = "com.example.myweather.data.apiEntity.Elevation")
    var elevation: Elevation,
    @Json(name = "Latitude")
    var latitude: Double,
    @Json(name = "Longitude")
    var longitude: Double
)

@Keep
@JsonClass(generateAdapter = true)
data class Region(
    @Json(name = "EnglishName")
    var englishName: String,
    @Json(name = "ID")
    var iD: String,
    @Json(name = "LocalizedName")
    var localizedName: String
)

@Keep
@JsonClass(generateAdapter = true)
data class SupplementalAdminArea(
    @Json(name = "EnglishName")
    var englishName: String,
    @Json(name = "Level")
    var level: Int,
    @Json(name = "LocalizedName")
    var localizedName: String
)

@Keep
@JsonClass(generateAdapter = true)
data class TimeZone(
    @Json(name = "Code")
    var code: String,
    @Json(name = "GmtOffset")
    var gmtOffset: Double,
    @Json(name = "IsDaylightSaving")
    var isDaylightSaving: Boolean,
    @Json(name = "Name")
    var name: String,
    @Json(name = "NextOffsetChange")
    var nextOffsetChange: String
)

@Keep
@JsonClass(generateAdapter = true)
data class Elevation(
    @Json(name = "com.example.myweather.data.apiEntity.Imperial")
    var imperial: Imperial,
    @Json(name = "com.example.myweather.data.apiEntity.Metric")
    var metric: Metric
)

@Keep
@JsonClass(generateAdapter = true)
data class Imperial(
    @Json(name = "Unit")
    var unit: String,
    @Json(name = "UnitType")
    var unitType: Int,
    @Json(name = "Value")
    var value: Double
)

@Keep
@JsonClass(generateAdapter = true)
data class Metric(
    @Json(name = "Unit")
    var unit: String,
    @Json(name = "UnitType")
    var unitType: Int,
    @Json(name = "Value")
    var value: Double
)