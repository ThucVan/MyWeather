package com.example.myweather.ui.activity.seeFiveDayActivity

import android.view.LayoutInflater
import com.example.myweather.databinding.ActivitySeeFiveDayBinding
import com.example.myweather.ui.base.BaseActivity

class SeeFiveDayActivity : BaseActivity<ActivitySeeFiveDayBinding>() {
    override fun setUpView() {
    }

    override fun getViewBinding(inflater: LayoutInflater): ActivitySeeFiveDayBinding =
        ActivitySeeFiveDayBinding.inflate(inflater)
}
