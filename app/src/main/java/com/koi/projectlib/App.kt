package com.koi.projectlib

import android.app.Application
import com.drake.brv.utils.BRV
import com.drake.engine.base.Engine
import com.hjq.bar.TitleBar
import com.koi.projectlib.other.TitleBarStyle
import com.tencent.mmkv.MMKV

class App : Application(){
    override fun onCreate() {
        super.onCreate()
        MMKV.initialize(this)
        Engine.initialize(this)
        BRV.modelId = BR.m
        BRV.debounceGlobalEnabled = true
        //初始化标题栏样式
        TitleBar.setDefaultStyle(TitleBarStyle())
    }
}