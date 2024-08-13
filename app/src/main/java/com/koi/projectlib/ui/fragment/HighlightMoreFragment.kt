package com.koi.projectlib.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.toColorInt
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.koi.highlightpro.HighlightPro
import com.koi.highlightpro.parameter.Constraints
import com.koi.highlightpro.parameter.HighlightParameter
import com.koi.highlightpro.parameter.MarginOffset
import com.koi.highlightpro.shape.CircleShape
import com.koi.highlightpro.shape.OvalShape
import com.koi.highlightpro.shape.RectShape
import com.koi.highlightpro.util.AnimUtil
import com.koi.highlightpro.util.PaintUtils
import com.koi.highlightpro.util.dp
import com.koi.projectlib.R
import com.koi.projectlib.databinding.FragmentHighlightMoreBinding

class HighlightMoreFragment : Fragment() {

    private lateinit var binding: FragmentHighlightMoreBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHighlightMoreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()

    }

    private fun initView() {
        binding.root.postDelayed({
            showHighlightSteps()
        }, 500)
    }

    private fun showHighlightSteps() {
        HighlightPro.with(this)
            .setHighlightParameter {
                HighlightParameter.Builder()
                    .setHighlightViewId(R.id.btn_step_first)
                    .setTipsViewId(R.layout.guide_step_first)
                    .setNextTipsViewId(R.id.highlight_next)
                    .setHighlightShape(RectShape(4f.dp, 4f.dp, 6f))
                    .setHighlightTopPadding(8f.dp)
                    .setHighlightRightPadding(20f.dp)
                    .setHighlightLeftPadding(8f.dp)
//                    .setHighlightBottomPadding(10f.dp)
//                    .setHighlightHorizontalPadding(8f.dp)
                    .setConstraints(Constraints.StartToEndOfHighlight + Constraints.TopToTopOfHighlight)
                    .setMarginOffset(MarginOffset(start = 8.dp))
                    .setTipViewDisplayAnimation(AnimUtil.getScaleAnimation())
                    .setOnlyButtonClick(false)
                    .build()
            }
            .setHighlightParameter {
                HighlightParameter.Builder()
                    .setHighlightViewId(R.id.btn_step_second)
                    .setTipsViewId(R.layout.guide_step_second)
                    .setNextTipsViewId(R.id.highlight_next)
                    .setHighlightShape(CircleShape().apply { setPaint(PaintUtils.getDashPaint()) })
                    .setHighlightHorizontalPadding(20f.dp)
                    .setHighlightVerticalPadding(20f.dp)
                    .setConstraints(Constraints.TopToBottomOfHighlight + Constraints.EndToEndOfHighlight)
                    .setMarginOffset(MarginOffset(top = 8.dp))
                    .setTipViewDisplayAnimation(AnimUtil.getScaleAnimation())
                    .setNextTipsViewId(R.id.highlight_next)
                    .setOnlyButtonClick(true)// 设置只有点击指定按钮才进行下一步，整个遮罩点击下一步将失效，默认为不设置，一旦设置，必须要设置（setNextTipsViewId）指定的按钮 id ，或view
                    .build()
            }
            .setHighlightParameter {
                HighlightParameter.Builder()
                    .setHighlightViewId(R.id.btn_step_third)
                    .setTipsViewId(R.layout.guide_step_third)
                    .setHighlightShape(OvalShape())
                    .setHighlightHorizontalPadding(12f.dp)
                    .setHighlightVerticalPadding(12f.dp)
                    .setConstraints(Constraints.BottomToTopOfHighlight + Constraints.EndToEndOfHighlight)
                    .setMarginOffset(MarginOffset(bottom = 6.dp))
                    .setTipViewDisplayAnimation(AnimUtil.getScaleAnimation())
                    .setNextTipsViewId(R.id.highlight_next)
                    .setOnlyButtonClick(true)
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

    companion object {
        const val TAG = "HYY-HighlightGuideFragment"

        fun create(): HighlightMoreFragment {
            return HighlightMoreFragment().apply {
                arguments = bundleOf(

                )
            }
        }
    }

}