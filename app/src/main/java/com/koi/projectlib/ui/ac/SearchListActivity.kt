package com.koi.projectlib.ui.ac

import android.view.View
import android.view.ViewTreeObserver
import android.widget.TextView
import android.widget.Toast
import com.drake.engine.base.EngineActivity
import com.drake.statusbar.immersive
import com.drake.tooltip.toast
import com.google.android.flexbox.FlexboxLayout
import com.hjq.bar.OnTitleBarListener
import com.hjq.bar.TitleBar
import com.koi.projectlib.R
import com.koi.projectlib.databinding.ActivitySearchListBinding

class SearchListActivity : EngineActivity<ActivitySearchListBinding>(R.layout.activity_search_list) {

    private var isExpanded = false
    private val maxRows = 3
    private val childMargin = 8

    override fun initView() {
        binding.v = this
        immersive(binding.titleBar, true)
        binding.titleBar.setOnTitleBarListener(object : OnTitleBarListener {
            override fun onLeftClick(titleBar: TitleBar?) {
                finish()
            }
        })
    }

    override fun initData() {

        val strList = arrayListOf<String>(
            "婚姻育儿", "儿", "设计", "影视天天", "大学生活", "散文",
            "婚姻育儿fassa", "儿", "设计", "影视天天ss", "大学生", "散文",
            "婚儿", "儿ffsaaf", "设s计", "影视天", "大学生ssa活", "散asfs文",
            "婚儿", "儿ffsaaf", "设s计", "影视天", "大学生ssa活", "散asfs文",
        )


        // 添加示例项目
        for (i in strList.indices) {
            val item = TextView(this).apply {
                text = strList[i]
//                text = "Item $i"
                setPadding(16, 16, 16, 16)
                setBackgroundResource(android.R.color.holo_blue_light)
                setOnClickListener {
                    Toast.makeText(this@SearchListActivity, "Clicked: $text  $i", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            // 设置每个子项之间的间距
            val layoutParams = FlexboxLayout.LayoutParams(
                FlexboxLayout.LayoutParams.WRAP_CONTENT,
                FlexboxLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                // 设置项目之间的间距（可以调整数值）
                setMargins(childMargin, childMargin, childMargin, childMargin)
            }
            binding.flexboxLayout.addView(item, layoutParams)
//            binding.flexboxLayout.addView(item)
        }
        // 初始显示为折叠状态
//        updateFlexboxLayout()
        // 使用 ViewTreeObserver 确保布局完成后再计算行数
        binding.flexboxLayout.viewTreeObserver.addOnGlobalLayoutListener(
            object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    binding.flexboxLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    updateFlexboxLayout()
                }
            }
        )

        // 按钮点击事件
        binding.buttonToggle.setOnClickListener {
            toast("isExpanded = $isExpanded")
            isExpanded = !isExpanded
            updateFlexboxLayout()
            binding.buttonToggle.text = if (isExpanded) "Show Less" else "Show More"
        }
    }


    private fun updateFlexboxLayout() {
        val visibleItemCount =
            if (isExpanded) binding.flexboxLayout.childCount else calculateVisibleItems()
        for (i in 0 until binding.flexboxLayout.childCount) {
            binding.flexboxLayout.getChildAt(i).visibility =
                if (i < visibleItemCount) View.VISIBLE else View.GONE
        }

        // 控制按钮的显示与隐藏
        binding.buttonToggle.visibility =
            if (calculateRowCount() > maxRows) View.VISIBLE else View.GONE

        // 如果展开，按钮永远可见
        if (isExpanded) {
            binding.buttonToggle.visibility = View.VISIBLE
        }
    }


    private fun calculateVisibleItems(): Int {
        var itemCount = 0
        var currentRow = 0
        var currentWidth = 0

        for (i in 0 until binding.flexboxLayout.childCount) {
            val child = binding.flexboxLayout.getChildAt(i)
            child.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)

            // 计算每行的宽度和行数
            if (currentWidth + child.measuredWidth + (childMargin * 2) > binding.flexboxLayout.width) {
                currentRow++
                currentWidth = child.measuredWidth + (childMargin * 2)
            } else {
                currentWidth += child.measuredWidth + (childMargin * 2)
            }

            if (currentRow >= maxRows) break
            itemCount++
        }
        return itemCount
    }


    // 获取预显示的行数
    private fun calculateRowCount(): Int {
        var rowCount = 1
        var currentWidth = 0

        for (i in 0 until binding.flexboxLayout.childCount) {
            val child = binding.flexboxLayout.getChildAt(i)
            child.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)

            // 如果当前宽度加上子项宽度超过布局宽度，增加行数
            if (currentWidth + child.measuredWidth + (childMargin * 2) > binding.flexboxLayout.width) {
                rowCount++
                currentWidth = child.measuredWidth + (childMargin * 2)
            } else {
                currentWidth += child.measuredWidth + (childMargin * 2)
            }
        }
        return rowCount
    }


}