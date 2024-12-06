package com.koi.projectlib.ui.ac

import android.view.View
import com.drake.engine.base.EngineActivity
import com.drake.statusbar.immersive
import com.hjq.bar.OnTitleBarListener
import com.hjq.bar.TitleBar
import com.koi.projectlib.R
import com.koi.projectlib.databinding.ActivityTestBinding

/**
 * 平时测试 功能界面
 * */
class ZTestActivity : EngineActivity<ActivityTestBinding>(R.layout.activity_test) {
    override fun initData() {
    }

    override fun initView() {
        binding.v = this
        immersive(binding.titleBar, true)
        binding.titleBar.setOnTitleBarListener(object : OnTitleBarListener {
            override fun onLeftClick(titleBar: TitleBar?) {
                finish()
            }
        })


    }

    override fun onClick(v: View) {
        when (v) {
        }
    }
}