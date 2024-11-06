package com.koi.projectlib.view

import android.content.Context
import android.util.AttributeSet
import com.hjq.shape.view.ShapeTextView
/**
 * 一个限制XX字数的自定义控件
 * */
class EllipsizeTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ShapeTextView(context, attrs, defStyleAttr) {

    var maxCharacterCount = 10

    override fun setText(text: CharSequence?, type: BufferType?) {
        val newText = text.toString()
        if (newText.length > maxCharacterCount) {
            super.setText(newText.substring(0, maxCharacterCount) + "...", type)
        } else {
            super.setText(newText, type)
        }
    }
}
