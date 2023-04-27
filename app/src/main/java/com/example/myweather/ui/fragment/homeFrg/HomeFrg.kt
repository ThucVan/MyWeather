package com.example.myweather.ui.fragment.homeFrg

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.activityViewModels
import com.example.myweather.BuildConfig
import com.example.myweather.data.liveData.StateData
import com.example.myweather.databinding.FragmentHomeFrgBinding
import com.example.myweather.ui.base.BaseFragment
import com.example.myweather.util.LoaderDialog
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import java.text.DecimalFormat

@AndroidEntryPoint
class HomeFrg : BaseFragment<FragmentHomeFrgBinding>() {
    private val homeFrgViewModel : HomeFrgViewModel by activityViewModels()
            override fun getViewBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ) = FragmentHomeFrgBinding.inflate(inflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        LoaderDialog.createDialog(requireActivity())

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }


        observer()
    }

    private fun observer(){
        homeFrgViewModel.weatherLiveData.observe(viewLifecycleOwner){status->
            when(status.getStatus()){
                StateData.DataStatus.LOADING -> {}
                StateData.DataStatus.SUCCESS -> {
                    status.getData().let {weatherEntity ->
                        if (weatherEntity != null){
                            LoaderDialog.dismiss()

                            binding.tvLocal.text = weatherEntity.name
                            binding.tvStatus.text = weatherEntity.weather[0].description
                            val celsius = DecimalFormat("#.#").format( (weatherEntity.main.temp  -  273.15  ))
                            binding.tvTemp.text = "$celsius℃"
                        }
                    }
                }
                StateData.DataStatus.ERROR -> {
                    LoaderDialog.dismiss()
                }
                else -> {
                    LoaderDialog.dismiss()
                }
            }
        }
    }
}