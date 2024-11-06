package com.koi.projectlib.util

import android.content.Context
import android.media.MediaMetadataRetriever
import android.util.Log
import io.microshow.rxffmpeg.RxFFmpegCommandList
import java.util.ArrayList

object Utils {
    /**
     * 获取屏幕高度的百分xx 转成px
     * */
    fun getScreenHeightPercentageInPx(context: Context, percentage: Double): Int {
        val displayMetrics = context.resources.displayMetrics
        val screenHeightPx = displayMetrics.heightPixels
        return (screenHeightPx * percentage).toInt()
    }

    //修改视频（压缩）
    fun getBoxScale(inputPath: String, outputPath: String): Array<String?>? {
        val wh = getVideoWidthAndHeight(inputPath)
        Log.e("koi", "startWidthAndHeight ===  ${getVideoWidthAndHeight(inputPath)}")
        val cmdList = RxFFmpegCommandList()
        cmdList.append("-i")
        cmdList.append(inputPath)
        cmdList.append("-vf")
        cmdList.append("scale=${wh[0]}:${wh[1]}")
//        cmdList.append("boxblur=5:1")
        cmdList.append("-preset")
        cmdList.append("superfast")
        cmdList.append(outputPath)
        return cmdList.build()
    }

    //获取视频宽高，判断横竖屏
    private fun getVideoWidthAndHeight(videoPath: String): ArrayList<String> {
        val list = ArrayList<String>()
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(videoPath)
        val width = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)
        val height = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)
        Log.e(
            "koi",
            "ROTATION ===  ${
                retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)
                    ?.toInt()
            }"
        )
        when (retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)
            ?.toInt()) {
            0, 180 -> {
//                 "横屏"
                if (width.toString().toInt() > 1000) {
                    list.add((width.toString().toInt() / 2).toString())
                    list.add((height.toString().toInt() / 2).toString())
                } else {
                    list.add(width.toString())
                    list.add(height.toString())
                }
            }

            90, 270 -> {
//                 "竖屏"
                if (height.toString().toInt() > 1000) {
                    list.add((height.toString().toInt() / 2).toString())
                    list.add((width.toString().toInt() / 2).toString())
                } else {
                    list.add(height.toString())
                    list.add(width.toString())
                }
            }

            else -> {
//                  "默认竖屏"
                if (height.toString().toInt() > 1000) {
                    list.add((height.toString().toInt() / 2).toString())
                    list.add((width.toString().toInt() / 2).toString())
                } else {
                    list.add(height.toString())
                    list.add(width.toString())
                }
            }
        }
        retriever.release()
        return list
    }
}
