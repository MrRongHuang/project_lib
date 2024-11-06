package com.koi.projectlib.ui.ac

import androidx.recyclerview.widget.GridLayoutManager
import com.drake.engine.base.EngineActivity
import com.drake.statusbar.immersive
import com.hjq.bar.OnTitleBarListener
import com.hjq.bar.TitleBar
import com.koi.projectlib.R
import com.koi.projectlib.adapter.ExpandedImgAdapter
import com.koi.projectlib.databinding.ActivityExpandedImgListBinding

class ExpandedImgListActivity :
    EngineActivity<ActivityExpandedImgListBinding>(R.layout.activity_expanded_img_list) {

    override fun initView() {
        binding.v = this
        immersive(binding.titleBar, true)
        binding.titleBar.setOnTitleBarListener(object : OnTitleBarListener {
            override fun onLeftClick(titleBar: TitleBar?) {
                finish()
            }
        })


        val imageUrls = arrayListOf<String>()
        for (i in 1..12) {
            imageUrls.add("https://b0.bdstatic.com/87c320eed88b200644281ef94049f0e7.jpg@h_1280")
        }
        val adapter = ExpandedImgAdapter(imageUrls)  // imageUrls 是你的图片地址列表
        binding.rv.layoutManager = GridLayoutManager(this, 3)  // 设置为3列网格
        binding.rv.adapter = adapter
    }

    override fun initData() {

    }

}