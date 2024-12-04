package com.koi.projectlib.ui.ac

import android.annotation.SuppressLint
import android.util.Log
import android.view.View
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.PathUtils
import com.drake.engine.base.EngineActivity
import com.drake.net.Post
import com.drake.net.component.Progress
import com.drake.net.interfaces.ProgressListener
import com.drake.net.utils.scopeDialog
import com.drake.statusbar.immersive
import com.drake.tooltip.dialog.BubbleDialog
import com.drake.tooltip.toast
import com.hjq.bar.OnTitleBarListener
import com.hjq.bar.TitleBar
import com.hjq.permissions.Permission
import com.koi.projectlib.Constant
import com.koi.projectlib.R
import com.koi.projectlib.databinding.ActivityTestBinding
import com.koi.projectlib.permission.AppPermissionsUtils
import com.koi.projectlib.permission.GlideEngine
import com.koi.projectlib.permission.MyRxFFmpegSubscriber
import com.koi.projectlib.util.ApiService
import com.koi.projectlib.util.Utils
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.luck.picture.lib.style.PictureSelectorStyle
import com.luck.picture.lib.utils.MediaUtils
import io.microshow.rxffmpeg.RxFFmpegInvoke
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * 平时测试 功能界面
 * */
class TestActivity : EngineActivity<ActivityTestBinding>(R.layout.activity_test) {

    //视频压缩
    private var myRxFFmpegSubscriber: MyRxFFmpegSubscriber? = null
    private val compressDialog by lazy {
        BubbleDialog(this, "视频压缩中").apply {
            setCancelable(false)
        }
    }

    private val uploadFileDialog by lazy {
        BubbleDialog(this, "资源上传中...").apply {
            setCancelable(false)
        }
    }

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

    }

    override fun onClick(v: View) {
        when (v) {
            binding.tvTest -> {
                AppPermissionsUtils.checkPermissions(
                    requireActivity(),
                    Constant.READ_MEDIA_IMAGES_STR,
                    Constant.READ_MEDIA_IMAGES_STR_TIPS,
                    Constant.READ_MEDIA_IMAGES_DENIED_STR,
                    object : AppPermissionsUtils.PermissionsCallback {
                        override fun next() {
                            selectVideo()
                        }

                        override fun cancel() {
                        }
                    },
                    Permission.READ_MEDIA_IMAGES
                )
            }
        }
    }

    /**
     * 选择视频
     */
    @Suppress("DEPRECATION")
    private fun selectVideo() {
        PictureSelector.create(this)
            .openGallery(SelectMimeType.ofVideo())
            .setSelectMaxFileSize(500 * 1024)
            .setMaxSelectNum(1)
            .setImageEngine(GlideEngine.createGlideEngine())
            .setSelectorUIStyle(PictureSelectorStyle())
            .forResult(object : OnResultCallbackListener<LocalMedia> {
                @SuppressLint("SimpleDateFormat")
                override fun onResult(result: java.util.ArrayList<LocalMedia>?) {
                    result ?: return
                    for (media in result) {
                        if (media.width == 0 || media.height == 0) {
                            val videoExtraInfo =
                                MediaUtils.getVideoSize(
                                    this@TestActivity,
                                    media.path
                                )
                            media.width = videoExtraInfo.width
                            media.height = videoExtraInfo.height
                        }
                        val destPath = "${PathUtils.getExternalDcimPath()}/${
                            SimpleDateFormat("yyyyMMdd_HHmmss").format(
                                Date()
                            )
                        }_${media.fileName}"
                        val commands = Utils.getBoxScale(media.realPath, destPath)
                        myRxFFmpegSubscriber = MyRxFFmpegSubscriber(
                            this@TestActivity,
                            compressDialog,
                            object : MyRxFFmpegSubscriber.FFmpegListener {
                                override fun onError() {
                                    toast("压缩视频失败,请重新选择")
                                }

                                override fun onFinish() {
                                    uploadVideo(destPath)
                                }

                            })
                        //开始执行FFmpeg命令
                        RxFFmpegInvoke.getInstance()
                            .runCommandRxJava(commands)
                            .subscribe(myRxFFmpegSubscriber)

                    }
                }

                override fun onCancel() {

                }

            })
    }


    //上传视频
    private fun uploadVideo(path: String) {
        //执行添加到待上传列表
        val fileList = mutableListOf<File>()
        fileList.add(File(path))
        fileList.add(File(path))
        scopeDialog(dialog = uploadFileDialog, cancelable = false) {
            val videoData = Post<String>("http://192.168.100.114:7101/public/file/upload")
            {
//            val videoData = Post<String>("http://192.168.101.24:8095/upload") {
                setQuery("path", "dataDaily")
//                setQuery("path", "shop/banner")
                param("files", fileList)
                addUploadListener(object : ProgressListener() {
                    override fun onProgress(p: Progress) {
                        binding.tvTest.post {
                            uploadFileDialog.updateTitle("资源上传中 ${p.progress()} %")
//                            binding.seek.progress = p.progress()
//                            binding.tvProgress.text = "上传进度: ${p.progress()}% 上传速度: ${p.speedSize()}     " + "\n\n文件大小: ${p.totalSize()}  已上传: ${p.currentSize()}  剩余大小: ${p.remainSize()}" + "\n\n已使用时间: ${p.useTime()}  剩余时间: ${p.remainTime()}"
                        }
                    }
                })
            }.await()
            uploadFileDialog.updateTitle("资源上传中")
            FileUtils.delete(File(path))
        }


          /**
           *  原始网络请求上传方式
           * */
//        // 创建 RequestBody
//        val requestFile = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), fileList[0])
//
//        // 创建 MultipartBody.Part
//        val body = MultipartBody.Part.createFormData("files", fileList[0].name, requestFile)
//
//        // 构建 MultipartBody.Part 列表
//        val fileParts = fileList.map { file ->
//            val requestFile = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
//            MultipartBody.Part.createFormData("files", file.name, requestFile)
//        }
//
//        val okHttpClient = OkHttpClient.Builder()
//            .connectTimeout(60, TimeUnit.SECONDS)
//            .writeTimeout(120, TimeUnit.SECONDS)
//            .readTimeout(120, TimeUnit.SECONDS)
//            .build()
//        // 初始化 Retrofit
//        val retrofit = Retrofit.Builder()
//            .baseUrl("http://192.168.100.114:7101/public/file/") // 替换为你的服务器地址
//            .client(okHttpClient)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//
//        // 创建接口服务实例
//        val service = retrofit.create(ApiService::class.java)
//
//        // 调用接口上传文件
////        val call = service.uploadFile(body, "dataDaily")
//        val call = service.uploadFileList(fileParts, "dataDaily")
//        uploadFileDialog.show()
//        // 异步上传
//        call.enqueue(object : Callback<ResponseBody> {
//            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
//                uploadFileDialog.dismiss()
//                if (response.isSuccessful) {
//                    Log.d("Upload", "File upload successful: ${response.body()?.string()}")
//                } else {
//                    Log.e("Upload", "Upload failed: ${response.errorBody()?.string()}")
//                }
//            }
//
//            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
//                Log.e("Upload", "Upload failed: ${t.message}")
//                uploadFileDialog.dismiss()
//            }
//        })

    }

}