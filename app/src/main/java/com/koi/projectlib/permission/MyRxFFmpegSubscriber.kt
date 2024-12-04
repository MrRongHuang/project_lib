package com.koi.projectlib.permission

import android.content.Context
import android.util.Log
import com.drake.tooltip.dialog.BubbleDialog
import io.microshow.rxffmpeg.RxFFmpegSubscriber
import java.lang.ref.WeakReference

class MyRxFFmpegSubscriber(
    context: Context,
    compressDialog1: BubbleDialog,
    listener1: FFmpegListener
) : RxFFmpegSubscriber() {
    private val mWeakReference: WeakReference<Context>
    private var listener = listener1
    private var compressDialog = compressDialog1
    init {
        mWeakReference = WeakReference(context)

    }
    override fun onError(message: String?) {
        Log.e("koi", "出错了 onError：$message")
        compressDialog.updateTitle("视频压缩中")
        listener.onError()
        compressDialog.dismiss()
    }

    override fun onFinish() {
        compressDialog.updateTitle("视频压缩中")
        listener.onFinish()
        compressDialog.dismiss()

    }

    override fun onProgress(progress: Int, progressTime: Long) {
        Log.e("koi", "progress ===  $progress")
        compressDialog.updateTitle("视频压缩中 $progress %")
        compressDialog.show()
    }

    override fun onCancel() {
        Log.e("koi", "onCancel")
        compressDialog.updateTitle("视频压缩中")
        compressDialog.dismiss()
    }


    interface FFmpegListener {
        fun onError()
        fun onFinish()
    }
}