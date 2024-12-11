package com.koi.projectlib.ui.ac

import android.graphics.Color
import android.view.View
import com.drake.brv.utils.divider
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import com.drake.engine.base.EngineActivity
import com.drake.statusbar.immersive
import com.hjq.bar.OnTitleBarListener
import com.hjq.bar.TitleBar
import com.hjq.shape.view.ShapeTextView
import com.koi.projectlib.R
import com.koi.projectlib.databinding.ActivityFormBinding
import com.koi.projectlib.databinding.ItemFormRowDataBinding
import com.koi.projectlib.model.RowData
import com.koi.projectlib.util.ColumnWidthUtil

class FormActivity : EngineActivity<ActivityFormBinding>(R.layout.activity_form) {
    override fun initData() {
    }

    override fun initView() {
        binding.v = this
        immersive(binding.titleBar, true)
        binding.titleBar.setOnTitleBarListener(object : OnTitleBarListener {
            override fun onLeftClick(titleBar: TitleBar?) {
                finish()
            }
        })


        // 模拟数据
        val data = listOf(
            RowData("品类", "牌号", "厂商", "价格", "交货地", "物流方式", "报价时间"),
            RowData("HaDPE", "7750", "广东石化", "¥1999.00/吨起", "福建f厦门", "自提", "2021-11-21 14:00"),
            RowData(
                "HDsPEssfsfs",
                "7750",
                "广东石化",
                "¥1999.00/吨起",
                "福建厦门",
                "/配送",
                "2021-11-21 14:00"
            ),
            RowData("HDPE", "7750", "广东石化", "¥1999.00/吨起", "福建厦门", "自提/配送", "2021-11-21 14:00"),
            RowData("HDPdE", "7750", "广东石化", "¥1s999.00/吨起", "福建厦门", "自提", "2021-11-21 14:00"),
            RowData("HDPE", "7750", "广东石化", "¥1999.00/吨起", "福建厦门as", "自提", "2021-11-21 14:00"),
            RowData("HghDPE", "7750", "广东石2化", "¥1999.00/吨起", "福建厦门", "/配送", "2021-11-21 14:00"),
            RowData("HDPE", "7750", "广东石化", "¥199s9.00/吨起", "福建厦门", "自提/配送", "2021-11-21 14:00"),
            RowData("HDPE", "7750", "广东石化", "¥1999.00/吨起", "福建厦门", "自提", "2021-11-21 14:00"),
            RowData("HDPE", "7750", "广东石化", "¥1999.00/吨起", "福建f厦门", "自提/配送", "2021-11-21 14:00"),
            RowData("HDPE", "77g50", "广东石化", "¥1s999.00/吨起", "福建厦门", "自提", "2021-11-21 14:00"),
            RowData("HhjDPE", "7750", "广东石化1", "¥1999.00/吨起", "福建厦门", "/配送", "2021-11-21 14:00"),
            RowData("HDPE", "7750", "广东石化", "¥1999.00/吨起", "福建厦门", "自提/配送", "2021-11-21 14:00"),
            RowData("HDPE", "7750", "广东石化", "¥1999.00/吨起", "福建厦门", "自提/配送", "2021-11-21 14:00"),
//            RowData("HDPE", "7750", "广东e石化", "¥1999.00/吨起", "福建厦门", "自提", "2021-11-21 14:00"),
//            RowData("HDPE", "7750", "广东f石化", "¥1f999.00/吨起", "福zx建厦门", "自提", "2021-11-21 14:00"),
//            RowData("HDPsE", "7750", "广东石化", "¥1999.00/吨起", "福建厦门", "配送", "2021-11-21 14:00"),
//            RowData("HDPE", "7750", "广东石化", "¥1999.00/f吨起", "福建厦门", "自提", "2021-11-21 14:00"),
//            RowData("HDPE", "7750", "广d东石化", "¥1999.00/吨起", "福建厦门", "自提/配送", "2021-11-21 14:00"),
//            RowData("HDgPE", "7750", "广东石化", "¥1999f.00/吨起", "福建厦门", "自提", "2021-11-21 14:00"),
//            RowData("HDPE", "7750", "广东石化", "¥1999.00/吨起", "福建厦门", "自提/配送", "2021-11-21 14:00"),
        )
        // 测量每列的最大宽度
        val columnWidths = ColumnWidthUtil.measureMaxWidths(this, data)

        binding.rv.linear().divider {
            setDrawable(R.drawable.shape_divider_c_e4e4e4_dp_1)
//            includeVisible = true
        }.setup {
            addType<RowData>(R.layout.item_form_row_data)
            onCreate {
                val itemBind = getBinding<ItemFormRowDataBinding>()
                itemBind.tvCategory.minWidth = columnWidths[0]
                itemBind.tvBrand.minWidth = columnWidths[1]
                itemBind.tvManufacturer.minWidth = columnWidths[2]
                itemBind.tvPrice.minWidth = columnWidths[3]
                itemBind.tvAddress.minWidth = columnWidths[4]
                itemBind.tvLogistics.minWidth = columnWidths[5]
                itemBind.tvTime.minWidth = columnWidths[6]
            }
            onBind {

                val itemBind = getBinding<ItemFormRowDataBinding>()

                val viewLineList = mutableListOf(
                    itemBind.viewLine,
                    itemBind.viewLine2,
                    itemBind.viewLine3,
                    itemBind.viewLine4,
                    itemBind.viewLine5,
                    itemBind.viewLine6,
                )
                val tvList = mutableListOf(
                    itemBind.tvCategory,
                    itemBind.tvBrand,
                    itemBind.tvManufacturer,
                    itemBind.tvPrice,
                    itemBind.tvAddress,
                    itemBind.tvLogistics,
                    itemBind.tvTime,
                )
                setItemView(viewLineList, tvList, modelPosition != 0)
            }
        }.models = data
    }

    /**
     * 控制第一行与其他行显示样式
     * */
    private fun setItemView(
        viewLineList: MutableList<View>, tvList: MutableList<ShapeTextView>, show: Boolean
    ) {
        viewLineList.map {
            it.setBackgroundColor(Color.parseColor(if (!show) "#f4f4f4" else "#e4e4e4"))
        }
        tvList.map {
            it.shapeDrawableBuilder.setSolidColor(Color.parseColor(if (show) "#00000000" else "#F4F4F4"))
                .intoBackground()
        }
    }

}