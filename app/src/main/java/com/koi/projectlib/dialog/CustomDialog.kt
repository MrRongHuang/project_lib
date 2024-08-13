package com.koi.projectlib.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import androidx.core.graphics.toColorInt
import androidx.fragment.app.DialogFragment
import com.koi.highlightpro.HighlightPro
import com.koi.highlightpro.parameter.Constraints
import com.koi.highlightpro.parameter.HighlightParameter
import com.koi.highlightpro.parameter.MarginOffset
import com.koi.highlightpro.shape.OvalShape
import com.koi.highlightpro.shape.RectShape
import com.koi.highlightpro.util.AnimUtil
import com.koi.highlightpro.util.dp
import com.koi.projectlib.R


/**
 * CustomDialog class
 *
 * @author hsw created on 2021/4/12
 *
 */
class CustomDialog : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.setCanceledOnTouchOutside(false)
        return inflater.inflate(R.layout.dialog_custom, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        showHighlight()
    }

    private fun showHighlight() {
        HighlightPro.with(dialog?.window?.decorView as FrameLayout)
            .setHighlightParameter {
                HighlightParameter.Builder()
                    .setHighlightViewId(R.id.btn_1)
                    .setTipsViewId(R.layout.guide_step_second)
                    .setHighlightShape(OvalShape(6f))
                    .setHighlightHorizontalPadding(12f.dp)
                    .setHighlightVerticalPadding(8f.dp)
                    .setConstraints(Constraints.TopToBottomOfHighlight + Constraints.EndToEndOfHighlight)
                    .setMarginOffset(MarginOffset(top = 8.dp))
                    .setTipViewDisplayAnimation(AnimUtil.getScaleAnimation())
                    .build()
            }
            .setHighlightParameter {
                HighlightParameter.Builder()
                    .setHighlightViewId(R.id.btn_2)
                    .setTipsViewId(R.layout.guide_step_second)
                    .setHighlightShape(RectShape(4f.dp, 4f.dp, 6f))
                    .setHighlightHorizontalPadding(12f.dp)
                    .setHighlightVerticalPadding(8f.dp)
                    .setConstraints(Constraints.TopToBottomOfHighlight + Constraints.EndToEndOfHighlight)
                    .setMarginOffset(MarginOffset(top = 8.dp))
                    .setTipViewDisplayAnimation(AnimUtil.getScaleAnimation())
                    .build()
            }
            .setBackgroundColor("#80000000".toColorInt())
            .setOnShowCallback { index ->
                //do something
            }
            .setOnDismissCallback {
                //do something
            }
            .interceptBackPressed(true)
            .show()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            val width = resources.displayMetrics.widthPixels
            val height = resources.displayMetrics.heightPixels
            setLayout((width*0.83f).toInt(), (height*0.8).toInt())

            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setGravity(Gravity.CENTER)
        }
    }

    companion object {
        const val TAG = "CustomDialog"
    }
}