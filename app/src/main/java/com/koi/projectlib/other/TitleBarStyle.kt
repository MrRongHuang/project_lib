package com.koi.projectlib.other

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.hjq.bar.TitleBarSupport
import com.hjq.bar.style.LightBarStyle
import com.koi.projectlib.R

/**
 * desc: 标题栏初始器
 */
class TitleBarStyle : LightBarStyle() {

    override fun newTitleView(context: Context): TextView {
        return AppCompatTextView(context)
    }

    override fun newLeftView(context: Context): TextView {
        return PressAlphaTextView(context)
    }

    override fun newRightView(context: Context): TextView {
        return PressAlphaTextView(context)
    }

    override fun getTitleColor(context: Context?): ColorStateList {
        return ColorStateList.valueOf(Color.parseColor("#333333"))
    }

    override fun getBackButtonDrawable(context: Context): Drawable? {
        return ContextCompat.getDrawable(context, R.drawable.arrows_left_ic)
    }

    override fun getRightTitleColor(context: Context?): ColorStateList {
        return ColorStateList.valueOf(-0x1)
    }

    override fun getTitleBarBackground(context: Context): Drawable {

        return ColorDrawable(ContextCompat.getColor(context, R.color.white))
    }


    override fun getLeftTitleBackground(context: Context): Drawable? {
        return null
    }

    override fun getRightTitleBackground(context: Context): Drawable? {
        return null
    }

    override fun getChildVerticalPadding(context: Context): Int {
        return context.resources.getDimension(R.dimen.dp_14).toInt()
    }

    override fun getTitleSize(context: Context): Float {
        return context.resources.getDimension(R.dimen.sp_17)
    }

    override fun getLeftTitleSize(context: Context): Float {
        return context.resources.getDimension(R.dimen.sp_13)
    }

    override fun getRightTitleSize(context: Context): Float {
        return context.resources.getDimension(R.dimen.sp_13)
    }

    override fun getTitleIconPadding(context: Context): Int {
        return context.resources.getDimension(R.dimen.dp_2).toInt()
    }

    override fun getLeftIconPadding(context: Context): Int {
        return context.resources.getDimension(R.dimen.dp_2).toInt()
    }

    override fun getRightIconPadding(context: Context): Int {
        return context.resources.getDimension(R.dimen.dp_2).toInt()
    }

    override fun getTitleTypeface(context: Context?, style: Int): Typeface {
        return TitleBarSupport.getTextTypeface(Typeface.BOLD)
    }

}