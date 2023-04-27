package com.example.myweather.util

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import com.example.myweather.R
import com.github.ybq.android.spinkit.SpinKitView
import com.github.ybq.android.spinkit.style.Circle

object LoaderDialog {
    private var processingDialog: AlertDialog? = null
    fun createDialog(activity: Activity) {
        try {
            val layout = LayoutInflater.from(activity).inflate(R.layout.dialog_loading, null)
            processingDialog = AlertDialog.Builder(activity).create()
            processingDialog?.setCanceledOnTouchOutside(false)
            processingDialog?.setView(layout)
            processingDialog?.show()
            processingDialog?.setOnKeyListener { arg0, keyCode, event ->
                activity.finish()
                true
            }
            processingDialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val progressBarLoading =
                processingDialog?.findViewById<SpinKitView>(R.id.progressCircularLoading)
            progressBarLoading?.setIndeterminateDrawable(Circle())

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun dismiss() {
        if (processingDialog != null && processingDialog?.isShowing == true) {
            processingDialog?.dismiss()
            processingDialog = null
        }
    }
}