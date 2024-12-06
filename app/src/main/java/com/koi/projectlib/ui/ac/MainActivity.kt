package com.koi.projectlib.ui.ac

import android.view.View
import com.drake.engine.base.EngineActivity
import com.drake.serialize.intent.openActivity
import com.drake.statusbar.immersive
import com.drake.tooltip.toast
import com.hjq.bar.OnTitleBarListener
import com.hjq.bar.TitleBar
import com.koi.projectlib.R
import com.koi.projectlib.databinding.ActivityMainBinding

class MainActivity : EngineActivity<ActivityMainBinding>(R.layout.activity_main) {
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
            binding.tvHighLight -> {
                toast("跳转到高亮效果")
                openActivity<HighLightActivity>()
            }

            binding.tvRichText -> {
                toast("跳转到富文本编辑")
                openActivity<RichTextEditActivity>()
            }

            binding.tvSearchList -> {
                openActivity<SearchListActivity>()
            }

            binding.tvImgList -> {
                openActivity<ExpandedImgListActivity>()
            }

            binding.tvRadarChart -> {
                openActivity<RadarChartViewActivity>()
            }

            binding.tvUploadFile -> {
                openActivity<UploadFileActivity>()
            }

            binding.tvPdfToPicture -> {
                openActivity<PdfToPictureActivity>()
            }

            binding.tvTest -> {
                openActivity<TestActivity>()
            }
        }
    }

}