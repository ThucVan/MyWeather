package com.example.myweather.ui.activity.mainActivtiy

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.myweather.BuildConfig
import com.example.myweather.R
import com.example.myweather.databinding.ActivityMainBinding
import com.example.myweather.ui.activity.searchActivity.SearchActivity
import com.example.myweather.ui.base.BaseActivity
import com.example.myweather.ui.fragment.FavoriteFrg
import com.example.myweather.ui.fragment.UserFrg
import com.example.myweather.ui.fragment.homeFrg.HomeFrg
import com.example.myweather.ui.fragment.homeFrg.HomeFrgViewModel
import com.example.myweather.ui.fragment.mapFrg.MapFrg
import com.example.myweather.util.Constants.LATITUDE_HANOI
import com.example.myweather.util.Constants.LONGITUDE_HANOI
import com.example.myweather.util.Constants.REQUEST_CODE
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {
    private lateinit var viewPager2Adapter: ViewPager2Adapter

    override fun getViewBinding(inflater: LayoutInflater) = ActivityMainBinding.inflate(inflater)

    override fun setUpView() {
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {}
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {}
                else -> {
                    Toast.makeText(this, getString(R.string.permissionsDeny), Toast.LENGTH_LONG)
                        .show()
                }
            }
        }

        viewPager2Adapter = ViewPager2Adapter(supportFragmentManager, lifecycle)
        viewPager2Adapter.addFragment(HomeFrg())
        viewPager2Adapter.addFragment(MapFrg())
        viewPager2Adapter.addFragment(FavoriteFrg())
        viewPager2Adapter.addFragment(UserFrg())

        binding.viewPagerMain.apply {
            adapter = viewPager2Adapter
            offscreenPageLimit = 5
            isUserInputEnabled = false
        }

        binding.bottomNavigationView.setOnApplyWindowInsetsListener(null)
        binding.viewPagerMain.apply {
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    updateNavigationBarState(
                        binding.bottomNavigationView.menu.getItem(
                            position
                        ).itemId
                    )

                }
            })
        }

        binding.bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navHome -> {
                    binding.viewPagerMain.currentItem = 0
                }
                R.id.navMap -> {
                    binding.viewPagerMain.currentItem = 1
                }
                R.id.navFavorite -> {
                    binding.viewPagerMain.currentItem = 2
                }
                R.id.navUser -> {
                    binding.viewPagerMain.currentItem = 3
                }
            }
            true
        }

        binding.fabSearch.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }
    }

    private fun updateNavigationBarState(actionId: Int) {
        binding.bottomNavigationView.selectedItemId = actionId
    }
}
