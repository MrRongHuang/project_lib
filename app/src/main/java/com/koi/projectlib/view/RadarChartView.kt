package com.koi.projectlib.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import com.drake.engine.utils.dp
import com.drake.engine.utils.sp
import kotlin.math.cos
import kotlin.math.sin

/**
 * by koi
 * 一个自定义的能力雷达图
 * */

class RadarChartView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthMode = 0
    private var heightMode = 0
    private var widthSize = 0
    private var heightSize = 0
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        widthMode = MeasureSpec.getMode(widthMeasureSpec)
        heightMode = MeasureSpec.getMode(heightMeasureSpec)
        widthSize = MeasureSpec.getSize(widthMeasureSpec)
        heightSize = MeasureSpec.getSize(heightMeasureSpec)
        intiConstant()
        initPaint()
    }

    private var RADIUS = 0f
    private var cX = 0f
    private var cY = 0f
    private val LADDER = 5f //阶梯数
    private val DATA_MAX = 100f //数据最大值

    private val textOffsetDp = 5.dp.toFloat()  // 文字的延伸偏移量，单位 dp
    private val textOffsetPx = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        textOffsetDp,
        resources.displayMetrics
    )

    private fun intiConstant() {
        RADIUS = if (widthSize > heightSize) {
            heightSize / 2f - 65.dp
        } else {
            widthSize / 2f - 70.dp
        }
        cX = widthSize / 2f
        cY = heightSize / 2f
    }

    private var linePaint1: Paint? = null // 内外层圆绘制线
    private var linePaint2: Paint? = null // 分割绘制线
    private var textPaint: Paint? = null
    private var centerTextPaint: Paint? = null
    private var dataPaint: Paint? = null // 各点连接线
    private var dataPaint2: Paint? = null // 连接点 圆心
    private var fillPaint: Paint? = null // 连接点 圆心填充
    private var fillPaintBg: Paint? = null // 创建用于填充透明遮罩的 Paint
    private fun initPaint() {
        linePaint1 = Paint()
        linePaint1!!.color = Color.parseColor("#FFE6CA")
        linePaint1!!.style = Paint.Style.STROKE //不加这个不显示
        linePaint1!!.strokeWidth = 2.dp.toFloat()
        linePaint1!!.isAntiAlias = true //抗锯齿功能
//        val effects: PathEffect = DashPathEffect(floatArrayOf(5f, 10f), 0f) //设置绘制虚线
//        linePaint1!!.pathEffect = effects


        linePaint2 = Paint()
        linePaint2!!.color = Color.parseColor("#FFE6CA")
        linePaint2!!.style = Paint.Style.STROKE //不加这个不显示
        linePaint2!!.strokeWidth = 2.dp.toFloat()
        linePaint2!!.isAntiAlias = true //抗锯齿功能
        val effects: PathEffect = DashPathEffect(floatArrayOf(11f, 11f), 0f) //设置绘制虚线
        linePaint2!!.pathEffect = effects


        textPaint = Paint()
        textPaint!!.color = Color.parseColor("#666666")
        textPaint!!.textSize = 12.sp.toFloat()


        centerTextPaint = Paint()
        centerTextPaint!!.color = Color.parseColor("#FF8500")
        centerTextPaint!!.textSize = 25.sp.toFloat()
        centerTextPaint!!.textAlign = Paint.Align.CENTER // 居中对齐
        centerTextPaint!!.isAntiAlias = true //抗锯齿功能


        dataPaint = Paint()
        dataPaint!!.color = Color.parseColor("#FF8500")
        dataPaint!!.style = Paint.Style.STROKE //不加这个不显示
        dataPaint!!.strokeWidth = 2.dp.toFloat()
        dataPaint!!.isAntiAlias = true //抗锯齿功能
        dataPaint!!.style = Paint.Style.FILL_AND_STROKE //设置绘图模式 扫描 FILL_AND_STROKE
        dataPaint!!.strokeJoin = Paint.Join.ROUND //设置线条闭合模式 MITER


        dataPaint2 = Paint()
        dataPaint2!!.color = Color.parseColor("#FFE6CA")
        dataPaint2!!.style = Paint.Style.STROKE //不加这个不显示
        dataPaint2!!.strokeWidth = 2.dp.toFloat()
        dataPaint2!!.isAntiAlias = true //抗锯齿功能

        fillPaint = Paint()
        fillPaint!!.color = Color.parseColor("#FFFFFF")
        fillPaint!!.style = Paint.Style.FILL // 填充模式
        fillPaint!!.isAntiAlias = true //抗锯齿功能

        fillPaintBg = Paint()
        fillPaintBg!!.color = Color.parseColor("#1AFF8500")
        fillPaintBg!!.style = Paint.Style.FILL // 填充模式
        fillPaintBg!!.isAntiAlias = true //抗锯齿功能


        /*Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable._l9_line_back);
        BitmapShader bitmapShader = new BitmapShader(bm, Shader.TileMode.REPEAT, Shader.TileMode.MIRROR);
        dataPaint.setShader(bitmapShader);*/
    }

    private var dataList: List<Int>? = null
    fun setData(list: List<Int>?) {
        dataList = list
        postInvalidate()
    }

    private var functionsList: List<String>? = null
    private var level = "" //单位

    fun setFunction(list: List<String>?, level: String) {
        functionsList = list
        this.level = level
        postInvalidate()
    }

    private var onceAngle = 0f
    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (functionsList == null || functionsList!!.isEmpty()) return
        if (dataList == null || dataList!!.isEmpty()) return
        if (dataList!!.size != functionsList!!.size) return

        linePaint1!!.color = Color.parseColor("#FFE6CA")
        //绘制最外层圆
        canvas.drawCircle(cX, cY, RADIUS, linePaint1!!)

        //外边框
//        canvas.drawLine(cX+RADIUS,cY-RADIUS,cX+RADIUS,cY+RADIUS,linePaint1!!)
//        canvas.drawLine(cX+RADIUS,cY+RADIUS,cX-RADIUS,cY+RADIUS,linePaint1!!)
//        canvas.drawLine(cX-RADIUS,cY+RADIUS,cX-RADIUS,cY-RADIUS,linePaint1!!)
//        canvas.drawLine(cX-RADIUS,cY-RADIUS,cX+RADIUS,cY-RADIUS,linePaint1!!)

        val functionsSize = functionsList!!.size.toFloat()
        onceAngle = 360 / functionsSize
        for (k in functionsList!!.indices) {
            // 计算角度并进行偏移，确保第一条线垂直于上方
            val angle = k * onceAngle - 90 // 偏移 90°，从顶部开始



            //绘制角度分割线
            // 0度 开始绘制第一条线（右方）
//            val lineEndX = cX + RADIUS * Math.cos(k * onceAngle / 180 * Math.PI).toFloat()
//            val lineEndY = cY + RADIUS * Math.sin(k * onceAngle / 180 * Math.PI).toFloat()

            // 90度 开始绘制第一条线（正上方）
            val lineEndX = cX + RADIUS * cos(Math.toRadians(angle.toDouble())).toFloat()
            val lineEndY = cY + RADIUS * sin(Math.toRadians(angle.toDouble())).toFloat()
            canvas.drawLine(cX, cY, lineEndX, lineEndY, linePaint2!!)

            // 0度 开始绘制第一条线（右方）
//            val textX = cX + (RADIUS + textOffsetPx) * Math.cos(k * onceAngle / 180 * Math.PI).toFloat()
//            val textY = cY + (RADIUS + textOffsetPx) * Math.sin(k * onceAngle / 180 * Math.PI).toFloat()

            // 90度 开始绘制第一条线（正上方）
            val textX =
                cX + (RADIUS + textOffsetPx) * cos(Math.toRadians(angle.toDouble())).toFloat()
            val textY =
                cY + (RADIUS + textOffsetPx) * sin(Math.toRadians(angle.toDouble())).toFloat()

            //绘制功能项名称
            if (lineEndX > cX) {
                textPaint!!.textAlign = Paint.Align.LEFT
                //在圆心右侧
                canvas.drawText(functionsList!![k], textX, textY, textPaint!!)
            } else if (lineEndX < cX) {
                textPaint!!.textAlign = Paint.Align.RIGHT
                //在圆心左侧
                canvas.drawText(functionsList!![k], textX, textY, textPaint!!)
            } else {
                textPaint!!.textAlign = Paint.Align.CENTER
                //在圆心正中
                canvas.drawText(functionsList!![k], textX, textY, textPaint!!)
            }
        }
        val onceLadderRadius = RADIUS / LADDER
        val ladderData = (DATA_MAX / LADDER).toInt()
        run {
            var k = 0
            while (k < LADDER) {
                // 每个圈的颜色
//                if (k == 0) linePaint1!!.color = Color.parseColor("#FF5722")
//                if (k == 1) linePaint1!!.color = Color.parseColor("#FF9800")
//                if (k == 2) linePaint1!!.color = Color.parseColor("#CDDC39")
//                if (k == 3) linePaint1!!.color = Color.parseColor("#4CAF50")
//                if (k == 4) linePaint1!!.color = Color.parseColor("#03A9F4")
//                if (k >= 5) linePaint1!!.color = Color.parseColor("#8F8F8F")


                linePaint1!!.color = Color.parseColor("#FFE6CA")

                //绘制内层圆
                canvas.drawCircle(cX, cY, onceLadderRadius * k, linePaint1!!)

                // 计算文字垂直居中的位置
                val textYOffset = (centerTextPaint!!.descent() + centerTextPaint!!.ascent()) / 2
                // 绘制中心点等级评价文字
                canvas.drawText(level, cX, cY - textYOffset, centerTextPaint!!)

                //绘制刻度
//                val textX = cX + onceLadderRadius * k * cos(0 / 180 * Math.PI).toFloat()
//                val textY = cY + onceLadderRadius * k * sin(0 / 180 * Math.PI).toFloat()
//                if (k.toFloat() == LADDER - 1) {
//                    textPaint!!.color = Color.parseColor("#FF5722")
//                    canvas.drawText(
//                        (ladderData * k).toString() + "(" + level + ")",
//                        textX,
//                        textY,
//                        textPaint!!
//                    )
//                } else {
//                    textPaint!!.color = Color.parseColor("#FF5722")
//                    canvas.drawText((ladderData * k).toString() + "", textX, textY, textPaint!!)
//                }
                k++
            }
        }

        //======================绘制数据=============================================================

        val points = mutableListOf<PointF>()
        val dataStep = RADIUS / DATA_MAX

        // 0度 开始绘制第一条线（右边）
//        val startX = cX + dataStep * dataList!![0] * Math.cos(0 * onceAngle / 180 * Math.PI)
//            .toFloat()
//        val startY = cY + dataStep * dataList!![0] * Math.sin(0 * onceAngle / 180 * Math.PI)
//            .toFloat()

        // 90度 开始绘制第一条线（正上方）
        val startX = cX + dataStep * dataList!![0] * cos(Math.toRadians((0 * onceAngle.toDouble()) - 90))
            .toFloat()
        val startY = cY + dataStep * dataList!![0] * sin(Math.toRadians((0 * onceAngle.toDouble()) - 90))
            .toFloat()
        // 先新增一个起始点
        points.add(PointF(startX, startY))

        for (k in dataList!!.indices) {
            if (k != dataList!!.size - 1) {
                // 0度 开始绘制第一条线（右边）
//                val startX = cX + dataStep * dataList!![k] * Math.cos(k * onceAngle / 180 * Math.PI)
//                    .toFloat()
//                val startY = cY + dataStep * dataList!![k] * Math.sin(k * onceAngle / 180 * Math.PI)
//                    .toFloat()
//                val endX =
//                    cX + dataStep * dataList!![k + 1] * Math.cos((k + 1) * onceAngle / 180 * Math.PI)
//                        .toFloat()
//                val endY =
//                    cY + dataStep * dataList!![k + 1] * Math.sin((k + 1) * onceAngle / 180 * Math.PI)
//                        .toFloat()

                // 90度 开始绘制第一条线（正上方）
                val startX = cX + dataStep * dataList!![k] * cos(Math.toRadians((k * onceAngle.toDouble()) - 90))
                    .toFloat()
                val startY = cY + dataStep * dataList!![k] * sin(Math.toRadians((k * onceAngle.toDouble()) - 90))
                    .toFloat()
                val endX =
                    cX + dataStep * dataList!![k + 1] * cos(Math.toRadians(((k + 1) * onceAngle.toDouble()) - 90))
                        .toFloat()
                val endY =
                    cY + dataStep * dataList!![k + 1] * sin(Math.toRadians(((k + 1) * onceAngle.toDouble()) - 90))
                        .toFloat()

                val path = Path()
                path.moveTo(startX, startY)
                path.lineTo(endX, endY)
                // 画线连接下一个点
                canvas.drawPath(path, dataPaint!!)

                // 先绘制白色实心圆
                canvas.drawCircle(startX, startY, 8f, fillPaint!!)
                // 画一个点
                canvas.drawCircle(startX, startY, 8f, dataPaint2!!)
                // 添加后续添加的绘制点
                points.add(PointF(endX, endY))
            } else {
                //最后一次画回原点
//                val startX = cX + dataStep * dataList!![k] * Math.cos(k * onceAngle / 180 * Math.PI)
//                    .toFloat()
//                val startY = cY + dataStep * dataList!![k] * Math.sin(k * onceAngle / 180 * Math.PI)
//                    .toFloat()
//                val endX = cX + dataStep * dataList!![0] * Math.cos(0 * onceAngle / 180 * Math.PI)
//                    .toFloat()
//                val endY = cY + dataStep * dataList!![0] * Math.sin(0 * onceAngle / 180 * Math.PI)
//                    .toFloat()

                // 90度 开始绘制第一条线（正上方）
                val startX = cX + dataStep * dataList!![k] * cos(Math.toRadians((k * onceAngle.toDouble()) - 90))
                    .toFloat()
                val startY = cY + dataStep * dataList!![k] * sin(Math.toRadians((k * onceAngle.toDouble()) - 90))
                    .toFloat()
                val endX = cX + dataStep * dataList!![0] * cos(Math.toRadians((0 * onceAngle.toDouble()) - 90))
                    .toFloat()
                val endY = cY + dataStep * dataList!![0] * sin(Math.toRadians((0 * onceAngle.toDouble()) - 90))
                    .toFloat()

                val path = Path()
                path.moveTo(startX, startY)
                path.lineTo(endX, endY)
                //画线连接下一个点
                canvas.drawPath(path, dataPaint!!)
                // 先绘制白色实心圆
                canvas.drawCircle(startX, startY, 8f, fillPaint!!)
                //画一个点
                canvas.drawCircle(startX, startY, 8f, dataPaint2!!)

//                points.add(PointF(startX, startY))
            }
        }


        // 创建 Path 并将点连接成多边形
        val path = Path().apply {
            if (points.isNotEmpty()) {
                moveTo(points[0].x, points[0].y)
                for (i in 1 until points.size) {
                    lineTo(points[i].x, points[i].y)
                }
                close() // 闭合路径形成多边形
            }
        }

        // 使用 Path 和 Paint 绘制透明遮罩
        canvas.drawPath(path, fillPaintBg!!)
    }
}