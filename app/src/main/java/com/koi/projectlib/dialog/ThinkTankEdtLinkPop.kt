package com.koi.projectlib.dialog

import android.annotation.SuppressLint
import android.content.Context
import androidx.databinding.DataBindingUtil
import com.drake.tooltip.toast
import com.koi.projectlib.R
import com.koi.projectlib.databinding.PopThinkTankEdtLinkBinding
import com.lxj.xpopup.core.CenterPopupView

/**
 * by koi
 * 用于富文本编辑，插入链接
 * */
@SuppressLint("ViewConstructor")
class ThinkTankEdtLinkPop(
    context: Context, private val describe: String,
    private val link: String, private val listener: OnThinkTankEdtLinkListerner
) :
    CenterPopupView(context) {
    override fun getImplLayoutId(): Int = R.layout.pop_think_tank_edt_link

    private val binding by lazy {
        DataBindingUtil.bind<PopThinkTankEdtLinkBinding>(popupImplView)
    }

    override fun onCreate() {
        binding?.let { bind ->
            if (describe.isNotEmpty()) {
                bind.edtDescribe.setText(describe)
            }
            if (link.isNotEmpty()) {
                bind.edtLink.setText(link)
            }

            bind.tvConfirm.setOnClickListener {
                val describe = bind.edtDescribe.text.toString()
                val link = bind.edtLink.text.toString()
                if (describe.trim().isNotEmpty() && link.trim().isNotEmpty()) {
                    dismissWith {
                        listener.onConfirm(describe, link)
                    }
                }else{
                    toast("链接说明或链接地址不能为空")
                }
            }
            bind.tvCancel.setOnClickListener {
                dismiss()
            }

        }
    }


    interface OnThinkTankEdtLinkListerner {
        fun onConfirm(describe: String, link: String)
    }
}