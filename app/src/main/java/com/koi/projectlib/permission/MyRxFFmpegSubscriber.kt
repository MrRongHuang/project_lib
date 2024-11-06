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
        listener.onError()
        compressDialog.dismiss()
    }

    override fun onFinish() {
        listener.onFinish()
        compressDialog.dismiss()

    }

    override fun onProgress(progress: Int, progressTime: Long) {
        Log.e("koi", "progress ===  $progress")
        compressDialog.show()
    }

    override fun onCancel() {
        Log.e("koi", "onCancel")
        compressDialog.dismiss()
    }


    interface FFmpegListener {
        fun onError()
        fun onFinish()
    }
}