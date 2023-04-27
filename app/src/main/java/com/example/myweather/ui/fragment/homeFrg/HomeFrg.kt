package com.example.myweather.ui.fragment.homeFrg

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.myweather.data.liveData.StateData
import com.example.myweather.databinding.FragmentHomeFrgBinding
import com.example.myweather.ui.base.BaseFragment

class HomeFrg : BaseFragment<FragmentHomeFrgBinding>() {

    private val homeFrgViewModel : HomeFrgViewModel by activityViewModels()

            override fun getViewBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ) = FragmentHomeFrgBinding.inflate(inflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val locale: String = this.resources.configuration.locale.language

        homeFrgViewModel.getCountryLiveDataWithName(locale)

        observer()
    }

    private fun observer(){
        homeFrgViewModel.weatherLiveDataDay.observe(viewLifecycleOwner){status->
            when(status.getStatus()){
                StateData.DataStatus.LOADING -> {}
                StateData.DataStatus.SUCCESS -> {
                    status.getData().let {
                        Log.e("TAG", "observer: 1day __ ${it}", )
                    }
                }
                StateData.DataStatus.ERROR -> {}
                else -> {}
            }
        }

        homeFrgViewModel.weatherLiveData10Day.observe(viewLifecycleOwner){status->
            when(status.getStatus()){
                StateData.DataStatus.LOADING -> {}
                StateData.DataStatus.SUCCESS -> {
                    status.getData().let {
                        Log.e("TAG", "observer: 10day __ ${it}", )
                    }
                }
                StateData.DataStatus.ERROR -> {}
                else -> {}
            }
        }
    }
}