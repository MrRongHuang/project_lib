package com.koi.projectlib.model

import androidx.databinding.BaseObservable

data class RichTextTypeModel(
    var res: Int, // 图标文件
    var type: Int, // 按钮类型
    var state: Int, // 按钮状态 1 不可点击  2 未选中 3 选中
    var name: String, // 那妞名称
): BaseObservable() {

}
