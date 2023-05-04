package com.example.myweather.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import java.text.SimpleDateFormat
import java.util.*

fun convertTime(dt: Long, format: String): String {
    val date = Date(dt * 1000L)
    val dateFormat = SimpleDateFormat(format)
    return dateFormat.format(date)
}

fun viewToBitmap(view: View): Bitmap {
    val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    view.draw(canvas)
    return bitmap
}