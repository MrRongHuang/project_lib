package com.koi.projectlib.permission

import android.content.Context
import com.koi.projectlib.R

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/XXPermissions
 * time   : 2023/01/02
 * desc   : 权限描述转换器
 */
object PermissionDescriptionConvert {
    /**
     * 获取权限描述
     */
    fun getPermissionDescription(context: Context, permissions: List<String?>?): String {
        val stringBuilder = StringBuilder()
        val permissionNames = PermissionNameConvert.permissionsToNames(context, permissions)
        for (permissionName in permissionNames) {
            stringBuilder.append(permissionName)
                .append(context.getString(R.string.common_permission_colon))
                .append(permissionsToDescription(context, permissionName))
                .append("\n")
        }
        return stringBuilder.toString().trim { it <= ' ' }
    }

    /**
     * 将权限名称列表转换成对应权限描述
     */
    fun permissionsToDescription(context: Context?, permissionName: String?): String {
        // 请根据权限名称转换成对应权限说明
        return "用于 xxx 业务"
    }
}