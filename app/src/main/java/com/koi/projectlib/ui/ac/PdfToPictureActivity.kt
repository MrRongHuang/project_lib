package com.koi.projectlib.ui.ac

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.provider.MediaStore
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import com.drake.engine.base.EngineActivity
import com.drake.statusbar.immersive
import com.drake.tooltip.toast
import com.hjq.bar.OnTitleBarListener
import com.hjq.bar.TitleBar
import com.hjq.permissions.Permission
import com.koi.projectlib.Constant
import com.koi.projectlib.R
import com.koi.projectlib.databinding.ActivityPdfToPictureBinding
import com.koi.projectlib.permission.AppPermissionsUtils
import java.io.File

/**
 * by koi
 * pdf 转 长图功能
 * */
class PdfToPictureActivity :
    EngineActivity<ActivityPdfToPictureBinding>(R.layout.activity_pdf_to_picture) {

    private lateinit var pickPdfLauncher: androidx.activity.result.ActivityResultLauncher<Array<String>>

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
        pickPdfLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            if (uri != null) {
                handlePdfUri(uri)
            } else {
                toast("未选择文件")
            }
        }
    }

    // 打开文件选择器
    private fun openPdfFilePicker() {

        pickPdfLauncher.launch(arrayOf("application/pdf"))
    }

    // 处理选中的 PDF URI
    private fun handlePdfUri(uri: Uri) {
        try {
            val bitmaps = renderPdfToBitmaps(this, uri)
            if (bitmaps.isEmpty()) {
                toast("PDF 渲染失败")
                return
            }

            val longImage = combineBitmapsIntoLongImage(bitmaps)
            val savedUri = saveBitmapToGallery(this, longImage, "pdf_long_image")
            if (savedUri != null) {
                toast("图片已保存到相册")
            } else {
                toast("图片保存失败")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            toast("处理 PDF 失败")
        }
    }

    // 渲染 PDF 为位图列表
    fun renderPdfToBitmaps(context: Context, pdfUri: Uri): List<Bitmap> {
        val contentResolver = context.contentResolver
        val input = contentResolver.openFileDescriptor(pdfUri, "r") ?: return emptyList()

        val pdfRenderer = PdfRenderer(input)
        val bitmaps = mutableListOf<Bitmap>()

//        val scale = 0.5f // 调整比例

        val screenDensity = context.resources.displayMetrics.densityDpi // 获取设备屏幕 DPI
        val targetDpi = 350 // 设置目标 DPI（打印质量）
        val scale = if (screenDensity > targetDpi) {
            targetDpi / screenDensity.toFloat()
        } else {
            1f // 不缩放
        }
        for (i in 0 until pdfRenderer.pageCount) {
            val page = pdfRenderer.openPage(i)
            val bitmap = Bitmap.createBitmap(
                (page.width * scale).toInt(), (page.height * scale).toInt(), Bitmap.Config.ARGB_8888
//                page.width, page.height, Bitmap.Config.ARGB_8888
            )
            bitmap.eraseColor(android.graphics.Color.WHITE) // 清除位图背景为白色
            page.render(
                bitmap,
                null,
                null,
                PdfRenderer.Page.RENDER_MODE_FOR_PRINT
            )
            bitmaps.add(bitmap)
            page.close()
        }
        pdfRenderer.close()
        return bitmaps
    }

    // 保存位图到相册
    private fun saveBitmapToGallery(context: Context, bitmap: Bitmap, fileName: String): Uri? {
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "$fileName.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/PdfToImage")
        }

        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        uri?.let {
            resolver.openOutputStream(it).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            }
        }
        return uri
    }

    // 拼接位图为长图
    private fun combineBitmapsIntoLongImage(bitmaps: List<Bitmap>): Bitmap {
        if (bitmaps.isEmpty()) return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)

        val totalHeight = bitmaps.sumOf { it.height }
        val width = bitmaps.maxOf { it.width }

        val result = Bitmap.createBitmap(width, totalHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)
        var currentHeight = 0

        bitmaps.forEach { bitmap ->
            canvas.drawBitmap(bitmap, 0f, currentHeight.toFloat(), null)
            currentHeight += bitmap.height
        }
        return result
    }


    override fun onClick(v: View) {
        when (v) {
            binding.tvChoosePdfFile -> {
                AppPermissionsUtils.checkPermissions(
                    requireActivity(),
                    Constant.READ_MEDIA_IMAGES_STR,
                    Constant.READ_MEDIA_IMAGES_STR_TIPS,
                    Constant.READ_MEDIA_IMAGES_DENIED_STR,
                    object : AppPermissionsUtils.PermissionsCallback {
                        override fun next() {
                            // 触发选择 PDF 文件
                            openPdfFilePicker()
                        }

                        override fun cancel() {
                        }
                    },
                    Permission.READ_MEDIA_IMAGES
                )
            }
        }
    }
}