package com.koi.projectlib.ui.ac

import com.drake.engine.base.EngineActivity
import com.drake.statusbar.immersive
import com.hjq.bar.OnTitleBarListener
import com.hjq.bar.TitleBar
import com.koi.projectlib.R
import com.koi.projectlib.databinding.ActivityRadarChartViewBinding

class RadarChartViewActivity : EngineActivity<ActivityRadarChartViewBinding>(R.layout.activity_radar_chart_view) {
    private var radarDatas = mutableListOf<Int>()
    override fun initData() {
    }

    override fun initView() {
        immersive(binding.titleBar, true)
        binding.titleBar.setOnTitleBarListener(object : OnTitleBarListener {
            override fun onLeftClick(titleBar: TitleBar?) {
                finish()
            }
        })

        initRadarChart()
    }

    // 初始化雷达能力图数据
    private fun initRadarChart() {
        radarDatas.clear()
        radarDatas.add(80)
        radarDatas.add(80)
        radarDatas.add(80)
        radarDatas.add(80)
        radarDatas.add(80)

        val radarFunctions = mutableListOf<String>()
        radarFunctions.add("订单履约")
        radarFunctions.add("经营能力")
        radarFunctions.add("资质认证")
        radarFunctions.add("平台担保")
        radarFunctions.add("日常活跃")
        binding.radarView.setData(radarDatas)
        binding.radarView.setFunction(radarFunctions, "A")


    }
}