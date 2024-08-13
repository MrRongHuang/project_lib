package com.koi.projectlib

import android.app.Application
import com.drake.engine.base.Engine
import com.tencent.mmkv.MMKV

class App : Application(){
    override fun onCreate() {
        super.onCreate()
        MMKV.initialize(this)
        Engine.initialize(this)
    }
}