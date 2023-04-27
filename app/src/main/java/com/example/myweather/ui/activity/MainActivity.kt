package com.example.myweather.ui.activity

import ViewPager2Adapter
import android.Manifest
import android.content.pm.PackageManager
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.myweather.R
import com.example.myweather.databinding.ActivityMainBinding
import com.example.myweather.ui.base.BaseActivity
import com.example.myweather.ui.fragment.FavoriteFrg
import com.example.myweather.ui.fragment.MapFrg
import com.example.myweather.ui.fragment.UserFrg
import com.example.myweather.ui.fragment.homeFrg.HomeFrg
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {
    private lateinit var viewPager2Adapter: ViewPager2Adapter

    override fun getViewBinding(inflater: LayoutInflater) = ActivityMainBinding.inflate(inflater)

    override fun setUpView() {
        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {}
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {}
                else -> {
                    Toast.makeText(this, getString(R.string.permissionsDeny), Toast.LENGTH_LONG).show()
                }
            }
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionRequest.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }

        viewPager2Adapter = ViewPager2Adapter(supportFragmentManager, lifecycle)
        viewPager2Adapter.addFragment(HomeFrg())
        viewPager2Adapter.addFragment(MapFrg())
        viewPager2Adapter.addFragment(FavoriteFrg())
        viewPager2Adapter.addFragment(UserFrg())

        binding.viewPagerMain.adapter = viewPager2Adapter
        binding.viewPagerMain.offscreenPageLimit = 5

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

        }
    }

    private fun updateNavigationBarState(actionId: Int) {
        binding.bottomNavigationView.selectedItemId = actionId
    }
}
