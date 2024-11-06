package com.koi.projectlib.permission

import android.content.Context
import com.drake.tooltip.toast
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.XXPermissions

/**
 * dsc:用于判断App 应用内操作 是否具有所需权限
 */
object AppPermissionsUtils {
    interface PermissionsCallback {
        //执行后续操作
        fun next()

        //取消
        fun cancel()
    }

    fun checkPermissions(
        context: Context,
        permissionStr: String,
        permissionStrTips: String,
        deniedStr: String,
        callback: PermissionsCallback,
        vararg permissions: String
    ) {
        //判断一个或多个权限未全部授予了
        if (!XXPermissions.isGranted(context, permissions)) {
            //需要发起告知弹窗说明
            XXPermissions.with(context)
                .permission(permissions)
                .interceptor(PermissionInterceptor(permissionStr, deniedStr))
                .request(object : OnPermissionCallback {
                    override fun onGranted(
                        permissions: MutableList<String>,
                        allGranted: Boolean

                    ) {
                        if (!allGranted) {
                            toast(permissionStrTips)
                            return
                        }
                        //全部授权成功
                        callback.next()
                    }
                })
        } else {
            //已全部权限给予，执行对应的后续操作
            callback.next()
        }
    }
}