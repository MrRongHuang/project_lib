package com.koi.projectlib

import android.graphics.Color
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.databinding.BindingAdapter
import com.hjq.shape.view.ShapeImageView
import com.koi.projectlib.model.RichTextTypeModel

@BindingAdapter("thinkTankHtmlImgRes")
fun thinkTankHtmlImgRes(view: ShapeImageView, model: RichTextTypeModel) {
    view.setImageResource(model.res)
    view.shapeDrawableBuilder
        .setSolidColor(
            when (model.state) {
                1, 2 -> {
                    Color.parseColor("#00000000")
                }
                3 -> {
                    Color.parseColor("#1AFF8500")
                }
                else -> {
                    Color.parseColor("#00000000")
                }
            }
        )
        .setRadius(4f)
        .intoBackground()
}

@BindingAdapter("thinkTankHtmlTvName")
fun thinkTankHtmlTvName(view: TextView, model: RichTextTypeModel) {
    view.text = model.name
    view.setTextColor(
        when (model.state) {
            1 -> {
                Color.parseColor("#DCDEE1")
            }
            2 -> {
                Color.parseColor("#999999")
            }
            3 -> {
                Color.parseColor("#FF8500")
            }
            else -> {
                Color.parseColor("#999999")
            }
        }
    )

}