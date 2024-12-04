package com.koi.projectlib

import android.app.Application
import com.drake.brv.utils.BRV
import com.drake.engine.base.Engine
import com.drake.net.NetConfig
import com.drake.net.cookie.PersistentCookieJar
import com.drake.net.interceptor.LogRecordInterceptor
import com.drake.net.okhttp.setConverter
import com.drake.net.okhttp.setDebug
import com.drake.net.okhttp.setDialogFactory
import com.drake.net.okhttp.setRequestInterceptor
import com.drake.tooltip.dialog.BubbleDialog
import com.hjq.bar.TitleBar
import com.koi.projectlib.other.TitleBarStyle
import com.tencent.mmkv.MMKV
import okhttp3.Cache
import java.util.concurrent.TimeUnit

class App : Application(){
    override fun onCreate() {
        super.onCreate()
        MMKV.initialize(this)
        Engine.initialize(this)
        BRV.modelId = BR.m
        BRV.debounceGlobalEnabled = true
        //初始化标题栏样式
        TitleBar.setDefaultStyle(TitleBarStyle())
        //初始化网络请求框架
        NetConfig.initialize("http://192.168.101.24:8096/api/", this) {
            // 超时设置
            connectTimeout(30, TimeUnit.SECONDS)
            readTimeout(30, TimeUnit.SECONDS)
            writeTimeout(30, TimeUnit.SECONDS)

            // 本框架支持Http缓存协议和强制缓存模式
            cache(
                Cache(
                    cacheDir,
                    1024 * 1024 * 128
                )
            ) // 缓存设置, 当超过maxSize最大值会根据最近最少使用算法清除缓存来限制缓存大小

            // LogCat是否输出异常日志, 异常日志可以快速定位网络请求错误
            setDebug(BuildConfig.DEBUG)

            // AndroidStudio OkHttp Profiler 插件输出网络日志
            addInterceptor(LogRecordInterceptor(BuildConfig.DEBUG))
//            addInterceptor(CookieReadInterceptor(this@App))
//            addInterceptor(DataEncryptInterceptor())
//            addInterceptor(OkHttpProfilerInterceptor())

            // 添加持久化Cookie管理
            cookieJar(PersistentCookieJar(this@App))

            // 通知栏监听网络日志
//            if (BuildConfig.DEBUG) {
//                addInterceptor(
//                    ChuckerInterceptor.Builder(this@App)
//                        .collector(ChuckerCollector(this@App))
//                        .maxContentLength(250000L)
//                        .redactHeaders(emptySet())
//                        .alwaysReadResponseBody(false)
//                        .build()
//                )
                // 监听实时刷新率
//                FpsCallBackUtils.startFps()
//            }

            // 添加请求拦截器, 可配置全局/动态参数
//            setRequestInterceptor(MyRequestInterceptor())
//
//            // 数据转换器
//            setConverter(SerializationConverter())

            // 自定义全局加载对话框
            setDialogFactory {
                BubbleDialog(it, "加载中....")
            }
        }
    }
}