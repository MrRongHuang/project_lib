package com.koi.projectlib.util

import android.content.Context
import android.util.Log
import android.widget.TextView
import com.drake.engine.utils.dp
import com.koi.projectlib.model.RowData


/**
 * by koi
 * 对比表单数据，每一列中最长view 显示的宽度
 * */
object ColumnWidthUtil {

    fun measureMaxWidths(context: Context, data: List<RowData>): List<Int> {
//        val paint = Paint()
        val textView = TextView(context)

        // 假设有 4 列：品类、牌号、生产厂家、价格
        val columnWidths = MutableList(7) { 0 }
        var maxOfCategory = data[0].category
        var maxOfBrand = data[0].brand
        var maxOfManufacturer = data[0].category
        var maxOfPrice = data[0].price
        var maxOfAddress = data[0].address
        var maxOfLogistics = data[0].logistics
        var maxOfTime = data[0].time

        data.map { map ->
            maxOfCategory = if (getTextWidth(textView, map.category) > getTextWidth(textView, maxOfCategory)) map.category else maxOfCategory
            maxOfBrand = if (getTextWidth(textView, map.brand) > getTextWidth(textView, maxOfBrand)) map.brand else maxOfBrand
            maxOfManufacturer = if (getTextWidth(textView, map.manufacturer) > getTextWidth(textView, maxOfManufacturer)) map.manufacturer else maxOfManufacturer
            maxOfPrice = if (getTextWidth(textView, map.price) > getTextWidth(textView, maxOfPrice)) map.price else maxOfPrice
            maxOfAddress = if (getTextWidth(textView, map.address) > getTextWidth(textView, maxOfAddress)) map.address else maxOfAddress
            maxOfLogistics = if (getTextWidth(textView, map.logistics) > getTextWidth(textView, maxOfLogistics)) map.logistics else maxOfLogistics
            maxOfTime = if (getTextWidth(textView, map.time) > getTextWidth(textView, maxOfTime)) map.time else maxOfTime
        }
        Log.e("getTextWidth", "maxOfCategory==========${maxOfCategory}======${getTextWidth(textView, maxOfCategory)}")
        for (row in data) {
            columnWidths[0] = getTextWidth(textView, maxOfCategory)
            columnWidths[1] = getTextWidth(textView, maxOfBrand)
            columnWidths[2] = getTextWidth(textView, maxOfManufacturer)
            columnWidths[3] = getTextWidth(textView, maxOfPrice)
            columnWidths[4] = getTextWidth(textView, maxOfAddress)
            columnWidths[5] = getTextWidth(textView, maxOfLogistics)
            columnWidths[6] = getTextWidth(textView, maxOfTime)
        }

        // 添加一些 padding，避免文字贴边
        return columnWidths.map { it + 22.dp }
    }

    private fun getTextWidth(textView: TextView, text: String): Int {
        val paint = textView.paint
        Log.e("getTextWidth", "${text}==========${paint.measureText(text).toInt()}")
        return paint.measureText(text).toInt()
    }
}