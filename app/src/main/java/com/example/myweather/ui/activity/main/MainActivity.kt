package com.example.myweather.ui.activity.main

import android.Manifest
import android.content.Intent
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.viewpager2.widget.ViewPager2
import com.example.myweather.R
import com.example.myweather.base.BaseActivity
import com.example.myweather.databinding.ActivityMainBinding
import com.example.myweather.ui.activity.main.search.SearchActivity
import com.example.myweather.ui.fragment.FavoriteFragment
import com.example.myweather.ui.fragment.UserFragment
import com.example.myweather.ui.fragment.home.HomeFragment
import com.example.myweather.ui.fragment.map.MapFragment
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
                    Toast.makeText(this, getString(R.string.txtPermissionsDeny), Toast.LENGTH_LONG)
                        .show()
                }
            }
        }

        setUpViewPager()

        setUpNavigation()

        binding.relativeSearch.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }
    }

    private fun setUpViewPager() {
        viewPager2Adapter = ViewPager2Adapter(supportFragmentManager, lifecycle)
        viewPager2Adapter.addFragment(HomeFragment())
        viewPager2Adapter.addFragment(MapFragment())
        viewPager2Adapter.addFragment(FavoriteFragment())
        viewPager2Adapter.addFragment(UserFragment())

        binding.viewPager2.apply {
            adapter = viewPager2Adapter
            offscreenPageLimit = 5
            isUserInputEnabled = false
        }

        binding.viewPager2.apply {
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
    }

    private fun setUpNavigation() {
        binding.bottomNavigationView.setOnApplyWindowInsetsListener(null)


        binding.bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navHome -> {
                    binding.viewPager2.currentItem = 0
                }
                R.id.navMap -> {
                    binding.viewPager2.currentItem = 1
                }
                R.id.navFavorite -> {
                    binding.viewPager2.currentItem = 2
                }
                R.id.navUser -> {
                    binding.viewPager2.currentItem = 3
                }
            }
            true
        }
    }

    private fun updateNavigationBarState(actionId: Int) {
        binding.bottomNavigationView.selectedItemId = actionId
    }
}
