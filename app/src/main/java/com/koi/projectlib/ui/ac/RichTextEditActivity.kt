package com.koi.projectlib.ui.ac

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.WindowManager
import android.webkit.*
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.PathUtils
import com.drake.brv.utils.bindingAdapter
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import com.drake.engine.base.EngineActivity
import com.drake.net.utils.TipUtils.toast
import com.drake.statusbar.immersive
import com.drake.tooltip.dialog.BubbleDialog
import com.google.gson.Gson
import com.hjq.permissions.Permission
import com.koi.projectlib.Constant
import com.koi.projectlib.R
import com.koi.projectlib.databinding.ActivityRichTextEditBinding
import com.koi.projectlib.dialog.ThinkTankEdtLinkPop
import com.koi.projectlib.model.EditLinkModel
import com.koi.projectlib.model.RichTextTypeModel
import com.koi.projectlib.model.UndoRedoModel
import com.koi.projectlib.permission.AppPermissionsUtils
import com.koi.projectlib.permission.GlideEngine
import com.koi.projectlib.permission.MyRxFFmpegSubscriber
import com.koi.projectlib.util.AppFileUtils
import com.koi.projectlib.util.Utils
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.luck.picture.lib.style.PictureSelectorStyle
import com.luck.picture.lib.utils.MediaUtils
import com.luck.picture.lib.utils.SandboxTransformUtils
import com.lxj.xpopup.XPopup
import io.microshow.rxffmpeg.RxFFmpegInvoke
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

@Suppress("DEPRECATION")
class RichTextEditActivity :
    EngineActivity<ActivityRichTextEditBinding>(R.layout.activity_rich_text_edit) {
    // 是否能发布内容
    private var canSend = false

    // 是否填写标题
    private var hasTitleContent = false

    // 是否填写内容
    private var hasContent = false


    // 编辑链接按钮是否高亮
    private var edtLinkState = false

    //拍照功能需要
    private var imageUri: Uri? = null
    private var photoFile: File? = null
    private var videoUri: Uri? = null
    private var videoFile: File? = null

    //视频压缩
    private var myRxFFmpegSubscriber: MyRxFFmpegSubscriber? = null
    private val compressDialog by lazy {
        BubbleDialog(this, "视频压缩中").apply {
            setCancelable(false)
        }
    }

    override fun initData() {

    }

    @SuppressLint("ClickableViewAccessibility", "SetJavaScriptEnabled")
    override fun initView() {
        binding.v = this
        immersive(binding.llTitle, true)
        binding.tvTitle.text = title
        // 监听输入的标题字数
        binding.edtThinkTank.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            @SuppressLint("SetTextI18n")
            override fun afterTextChanged(s: Editable) {
                binding.tvTitleNum.text = s.length.toString() + "/30"
                hasTitleContent = s.toString().trim().isNotEmpty()
                checkCanSend()
            }
        })
        // 控制原生编辑器焦点
        binding.edtThinkTank.setOnFocusChangeListener { v, hasFocus ->
            // 禁用 WebView 的 JavaScript 交互
            if (hasFocus) {
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
            }

        }
        // 设置 WebView 的点击监听器
        binding.webContent.setOnTouchListener { _, _ ->
            // 启用 adjustResize
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
            binding.webContent.settings.javaScriptEnabled = true
            false
        }

        initWeb(binding.webContent)
        getRichTextData()
    }

    /**
     * 设置编辑器内容
     * */
    private fun getRichTextData() {
        val list = mutableListOf<RichTextTypeModel>().apply {
            add(RichTextTypeModel(R.drawable.icon_think_tank_revoke_inoperable, 1, 1, "撤销"))
            add(RichTextTypeModel(R.drawable.icon_think_tank_redo_unsel, 2, 2, "重做"))
            add(RichTextTypeModel(R.drawable.icon_think_tank_h1_unsel, 3, 2, "标题"))
            add(RichTextTypeModel(R.drawable.icon_think_tank_bold_unsel, 4, 2, "加粗"))
            add(RichTextTypeModel(R.drawable.icon_think_tank_italic_unsel, 5, 2, "斜体"))
            add(RichTextTypeModel(R.drawable.icon_think_tank_line_unsel, 6, 2, "下划"))
            add(RichTextTypeModel(R.drawable.icon_think_tank_list_unsel, 7, 2, "有序"))
            add(RichTextTypeModel(R.drawable.icon_think_tank_nolist_unsel, 8, 2, "无序"))
            add(RichTextTypeModel(R.drawable.icon_think_tank_link_unsel, 9, 2, "链接"))
            add(RichTextTypeModel(R.drawable.icon_think_tank_img_unsel, 10, 2, "图片"))
            add(RichTextTypeModel(R.drawable.icon_think_tank_video_unsel, 11, 2, "视频"))
        }
        binding.rvEdit.linear(RecyclerView.HORIZONTAL)
            .setup {
                addType<RichTextTypeModel>(R.layout.item_rich_text_type)
                onClick(R.id.item) {
                    val model = getModel<RichTextTypeModel>(modelPosition)
                    when (model.name) {
                        "撤销" -> {
                            toast("撤销")
                            if (model.state != 1) {
                                binding.webContent.evaluateJavascript("javascript:undo();", null)
                            }
                        }
                        "重做" -> {
                            toast("重做")
                            if (model.state != 1) {
                                binding.webContent.evaluateJavascript("javascript:redo();", null)
                            }
                        }
                        "标题" -> {
                            toast("标题")
                            binding.webContent.evaluateJavascript(
                                "javascript:toggleHeadingParagraph();",
                                null
                            )
                        }
                        "加粗" -> {
                            toast("加粗")
                            binding.webContent.evaluateJavascript("javascript:toggleBold();", null)

                        }
                        "斜体" -> {
                            toast("斜体")
                            binding.webContent.evaluateJavascript(
                                "javascript:toggleItalic();",
                                null
                            )
                        }
                        "下划" -> {
                            toast("下划")
                            binding.webContent.evaluateJavascript(
                                "javascript:toggleUnderline();",
                                null
                            )
                        }
                        "有序" -> {
                            toast("有序")
                            binding.webContent.evaluateJavascript(
                                "javascript:toggleOrderedList();",
                                null
                            )
                        }
                        "无序" -> {
                            toast("无序")
                            binding.webContent.evaluateJavascript(
                                "javascript:toggleUnorderedList();",
                                null
                            )
                        }
                        "链接" -> {
                            toast("链接")
                            binding.webContent.evaluateJavascript("javascript:blur();", null)
                            if (edtLinkState) {
                                // 进入修改链接
                                binding.webContent.evaluateJavascript(
                                    "javascript:getLinkInfo();",
                                    null
                                )
                            } else {
                                // 新增链接插入
                                binding.webContent.evaluateJavascript("javascript:getSelectedText();") { value ->
                                    // value 是从 JavaScript 返回的结果，它是一个 JSON 字符串形式
                                    val selectedText =
                                        value?.replace("\"", "").toString()  // 移除多余的引号
                                    if (selectedText.isEmpty() || selectedText == "null") {
                                        edtLink("", "")
                                    } else {
                                        edtLink(selectedText, "")
                                    }
                                }
                            }
                        }
                        "图片" -> {
                            toast("图片")
                            binding.webContent.evaluateJavascript("javascript:getImageCount();") {
                                if (it.toInt() >=  Constant.PHOTO_SELECT_MAX_NUMBER_THINK_TANK) {
                                    com.drake.tooltip.toast("暂不支持上传超过10张图片")
                                } else {
                                    XPopup.Builder(this@RichTextEditActivity)
                                        .asBottomList(
                                            "请选择上传类型",
                                            arrayOf("拍照片", "从手机相册选择"), null
                                        ) { _, text1 ->
                                            when (text1) {
                                                "拍照片" -> {
                                                    AppPermissionsUtils.checkPermissions(
                                                        this@RichTextEditActivity,
                                                        Constant.READ_MEDIA_IMAGES_AND_CAMERA_STR,
                                                        Constant.READ_MEDIA_IMAGES_AND_CAMERA_STR_TIPS,
                                                        Constant.READ_MEDIA_IMAGES_AND_CAMERA_DENIED_STR,
                                                        object :
                                                            AppPermissionsUtils.PermissionsCallback {
                                                            override fun next() {
                                                                selectCamera(Constant.ALBUM_TYPE_PHOTO)
                                                            }

                                                            override fun cancel() {

                                                            }
                                                        },
                                                        Permission.READ_MEDIA_IMAGES,
                                                        Permission.CAMERA
                                                    )
                                                }

                                                "从手机相册选择" -> {
                                                    AppPermissionsUtils.checkPermissions(
                                                        this@RichTextEditActivity,
                                                        Constant.READ_MEDIA_IMAGES_STR,
                                                        Constant.READ_MEDIA_IMAGES_STR_TIPS,
                                                        Constant.READ_MEDIA_IMAGES_DENIED_STR,
                                                        object :
                                                            AppPermissionsUtils.PermissionsCallback {
                                                            override fun next() {
                                                                selectPhoto()
                                                            }

                                                            override fun cancel() {

                                                            }
                                                        },
                                                        Permission.READ_MEDIA_IMAGES
                                                    )
                                                }
                                            }
                                        }.show()
                                }
                            }
                        }
                        "视频" -> {
                            toast("视频")
                            binding.webContent.evaluateJavascript("javascript:getVideoCount();") {
                                if (it.toInt() >= Constant.VIDEO_SELECT_MAX_NUMBER_THINK_TANK) {
                                    com.drake.tooltip.toast("暂不支持上传超过3条视频")
                                } else {
                                    XPopup.Builder(this@RichTextEditActivity)
                                        .asBottomList(
                                            "请选择上传类型",
                                            arrayOf("拍视频", "从手机相册选择"), null
                                        ) { _, text1 ->
                                            when (text1) {
                                                "拍视频" -> {
                                                    AppPermissionsUtils.checkPermissions(
                                                        this@RichTextEditActivity,
                                                        Constant.READ_MEDIA_IMAGES_AND_CAMERA_STR,
                                                        Constant.READ_MEDIA_IMAGES_AND_CAMERA_STR_TIPS,
                                                        Constant.READ_MEDIA_IMAGES_AND_CAMERA_DENIED_STR,
                                                        object :
                                                            AppPermissionsUtils.PermissionsCallback {
                                                            override fun next() {
                                                                selectCamera(Constant.ALBUM_TYPE_VIDEO)
                                                            }

                                                            override fun cancel() {

                                                            }
                                                        },
                                                        Permission.READ_MEDIA_IMAGES,
                                                        Permission.CAMERA
                                                    )
                                                }

                                                "从手机相册选择" -> {
                                                    AppPermissionsUtils.checkPermissions(
                                                        this@RichTextEditActivity,
                                                        Constant.READ_MEDIA_IMAGES_STR,
                                                        Constant.READ_MEDIA_IMAGES_STR_TIPS,
                                                        Constant.READ_MEDIA_IMAGES_DENIED_STR,
                                                        object :
                                                            AppPermissionsUtils.PermissionsCallback {
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
                                        }.show()
                                }
                            }
                        }
                    }
                }
            }.models = list
    }

    private fun edtLink(selDescribe: String, selLink: String) {
        XPopup.Builder(requireActivity())
            .isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
            .dismissOnTouchOutside(false)
            .dismissOnBackPressed(false)
            .asCustom(
                ThinkTankEdtLinkPop(requireActivity(), selDescribe, selLink,
                    object : ThinkTankEdtLinkPop.OnThinkTankEdtLinkListerner {
                        override fun onConfirm(describe: String, link: String) {
                            if (edtLinkState) {
                                binding.webContent.evaluateJavascript(
                                    "javascript:focus();editLink('$link','$describe');", null
                                )
                            } else {
                                binding.webContent.evaluateJavascript(
                                    "javascript:focus();insertLink('$link','$describe');", null
                                )
                            }
                        }
                    })
            ).show()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWeb(wbContent: WebView) {
        val settings: WebSettings = wbContent.settings
        //开启JavaScript支持
        settings.javaScriptEnabled = true
        settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        wbContent.isFocusable = true
        wbContent.isFocusableInTouchMode = true
        wbContent.webViewClient = webViewClient
        wbContent.webChromeClient = webChromeClient
        wbContent.addJavascriptInterface(WebAppInterface(this), "editor")
        wbContent.loadUrl("file:///android_asset/tinymce/editor.html")
    }

    @SuppressLint("SetTextI18n")
    inner class WebAppInterface(val context: Context) {
        @JavascriptInterface
        fun contentChanged(postMessage: String) {
            Log.e("koi", "contentChanged  ==  $postMessage")
//            commitContent = postMessage
        }

        @JavascriptInterface
        fun validStateChanged(validStateChanged: String) {
            Log.e("koi", "validStateChanged  ==  $validStateChanged")
            hasContent = validStateChanged == "true"
            checkCanSend()
        }

        @JavascriptInterface
        fun textLengthChanged(num: String) {
            Log.e("koi", "textLengthChanged  ==  $num")
            runOnUiThread {
                binding.tvContentNum.text = "($num/2000)"
            }

        }

        @JavascriptInterface
        fun undoRedoState(undoRedoState: String) {
            Log.e("koi", "undoRedoState == $undoRedoState")
            if (undoRedoState.isNotEmpty()) {
                val bean = Gson().fromJson(undoRedoState, UndoRedoModel::class.java)
                if (bean.canUndo == "true") {
                    val model = binding.rvEdit.bindingAdapter.getModel<RichTextTypeModel>(0)
                    model.state = 2
                    model.res = R.drawable.icon_think_tank_revoke_unsel
                    model.notifyChange()
                } else {
                    val model = binding.rvEdit.bindingAdapter.getModel<RichTextTypeModel>(0)
                    model.state = 1
                    model.res = R.drawable.icon_think_tank_revoke_inoperable
                    model.notifyChange()
                }

                if (bean.canRedo == "true") {
                    val model = binding.rvEdit.bindingAdapter.getModel<RichTextTypeModel>(1)
                    model.state = 2
                    model.res = R.drawable.icon_think_tank_redo_unsel
                    model.notifyChange()
                } else {
                    val model = binding.rvEdit.bindingAdapter.getModel<RichTextTypeModel>(1)
                    model.state = 1
                    model.res = R.drawable.icon_think_tank_redo_inoperable
                    model.notifyChange()
                }
            }
        }

        @JavascriptInterface
        fun h1State(h1State: String) {
            Log.e("koi", "H1 h1State  ==  $h1State")
            val model = binding.rvEdit.bindingAdapter.getModel<RichTextTypeModel>(2)
            if (h1State == "true") {
                model.state = 3
                model.res = R.drawable.icon_think_tank_h1_sel
                model.notifyChange()
            } else {
                model.state = 2
                model.res = R.drawable.icon_think_tank_h1_unsel
                model.notifyChange()
            }
        }

        @JavascriptInterface
        fun boldState(boldState: String) {
            Log.e("koi", "加粗 boldState  ==  $boldState")
            val model = binding.rvEdit.bindingAdapter.getModel<RichTextTypeModel>(3)
            if (boldState == "true") {
                model.state = 3
                model.res = R.drawable.icon_think_tank_bold_sel
                model.notifyChange()
            } else {
                model.state = 2
                model.res = R.drawable.icon_think_tank_bold_unsel
                model.notifyChange()
            }
        }

        @JavascriptInterface
        fun italicState(italicState: String) {
            Log.e("koi", "斜体 italicState  ==  $italicState")
            val model = binding.rvEdit.bindingAdapter.getModel<RichTextTypeModel>(4)
            if (italicState == "true") {
                model.state = 3
                model.res = R.drawable.icon_think_tank_italic_sel
                model.notifyChange()
            } else {
                model.state = 2
                model.res = R.drawable.icon_think_tank_italic_unsel
                model.notifyChange()
            }
        }

        @JavascriptInterface
        fun underlineState(underlineState: String) {
            Log.e("koi", "下划线 underlineState  ==  $underlineState")
            val model = binding.rvEdit.bindingAdapter.getModel<RichTextTypeModel>(5)
            if (underlineState == "true") {
                model.state = 3
                model.res = R.drawable.icon_think_tank_line_sel
                model.notifyChange()
            } else {
                model.state = 2
                model.res = R.drawable.icon_think_tank_line_unsel
                model.notifyChange()
            }
        }

        @JavascriptInterface
        fun orderedListState(orderedListState: String) {
            Log.e("koi", "有序 orderedListState  ==  $orderedListState")
            val model = binding.rvEdit.bindingAdapter.getModel<RichTextTypeModel>(6)
            if (orderedListState == "true") {
                model.state = 3
                model.res = R.drawable.icon_think_tank_list_sel
                model.notifyChange()
            } else {
                model.state = 2
                model.res = R.drawable.icon_think_tank_list_unsel
                model.notifyChange()
            }
        }

        @JavascriptInterface
        fun unorderedListState(unorderedListState: String) {
            Log.e("koi", "无序 unorderedListState  ==  $unorderedListState")
            val model = binding.rvEdit.bindingAdapter.getModel<RichTextTypeModel>(7)
            if (unorderedListState == "true") {
                model.state = 3
                model.res = R.drawable.icon_think_tank_nolist_sel
                model.notifyChange()
            } else {
                model.state = 2
                model.res = R.drawable.icon_think_tank_nolist_unsel
                model.notifyChange()
            }
        }


        @JavascriptInterface
        fun linkState(linkState: String) {
            Log.e("koi", "链接 linkState  ==  $linkState")
            val model = binding.rvEdit.bindingAdapter.getModel<RichTextTypeModel>(8)
            if (linkState == "true") {
                model.state = 3
                model.res = R.drawable.icon_think_tank_link_sel
                model.notifyChange()
                edtLinkState = true
            } else {
                model.state = 2
                model.res = R.drawable.icon_think_tank_link_unsel
                model.notifyChange()
                edtLinkState = false
            }
        }

        @JavascriptInterface
        fun editLink(linkJson: String) {
            Log.e("koi", "linkJson  ==  $linkJson")
            if (linkJson.isNotEmpty()) {
                val bean = Gson().fromJson(linkJson, EditLinkModel::class.java)
                edtLink(bean.linkText, bean.link)
            }

        }
    }

    /**
     * 检测是否符合发布内容的条件
     */
    private fun checkCanSend() {
        if (hasTitleContent && hasContent) {
            canSend = true
            binding.tvRelease.setTextColor(Color.parseColor("#ff8500"))
        } else {
            canSend = false
            binding.tvRelease.setTextColor(Color.parseColor("#999999"))
        }
    }

    private val webViewClient = object : WebViewClient() {
        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            return super.shouldOverrideUrlLoading(view, request)
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            binding.webContent.evaluateJavascript(
                "javascript:init('请输入问题描述',${2000},null,${
                    Utils.getScreenHeightPercentageInPx(
                        this@RichTextEditActivity,
                        0.6
                    )
                });",
                null
            )

            binding.webContent.postDelayed({
                initWebData("")
                checkCanSend()
            }, 500)
        }
    }

    /**
     * 初始化web编辑器数据内容
     * @param content 现有编辑器内容
     * */
    private fun initWebData(
        content: String
    ) {
        if (content.isNotEmpty()) {
            val showContent = content.replace("\n", "\\n").replace("'","\\'")
            binding.webContent.evaluateJavascript(
                "javascript:setHTMLContent('$showContent');",
                null
            )
        }
    }

    private val webChromeClient = object : WebChromeClient() {

    }


    @Suppress("DEPRECATION")
    private fun selectCamera(cameraType: Int) {
        if (cameraType == Constant.ALBUM_TYPE_PHOTO) {
            // 创建拍照Intent
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (takePictureIntent.resolveActivity(packageManager) != null) {
                try {
                    photoFile = AppFileUtils.createImageFile(this@RichTextEditActivity)
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                }
                if (photoFile != null) {
                    imageUri = FileProvider.getUriForFile(
                        this@RichTextEditActivity,
                        "com.koi.projectlib.fileprovider",
                        photoFile!!
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                    startActivityForResult(
                        takePictureIntent,
                        Constant.REQUEST_IMAGE_CAPTURE
                    )
                }
            }
        } else {
            // 创建拍视频Intent
            val takePictureIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
            if (takePictureIntent.resolveActivity(packageManager) != null) {
                try {
                    videoFile = AppFileUtils.createMoviesFile(this@RichTextEditActivity)
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                }
                if (videoFile != null) {
                    videoUri = FileProvider.getUriForFile(
                        this@RichTextEditActivity,
                        "com.koi.projectlib.fileprovider",
                        videoFile!!
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri)
                    startActivityForResult(
                        takePictureIntent,
                        Constant.REQUEST_VIDEO_CAPTURE
                    )
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constant.REQUEST_IMAGE_CAPTURE) {
            // 获取拍摄的照片
            if (resultCode == RESULT_OK) {
                // 压缩图片并上传到服务器
                val compressedImageFile = photoFile?.let { AppFileUtils.compressImage(it) }
                if (compressedImageFile != null) {
                    val fileList = mutableListOf<File>()
                    fileList.add(File(compressedImageFile.absolutePath))
                    //执行上传操作
//                    upLoadImgOrVideoList(fileList, 1)
                    toast("执行上传完成并插入图片的操作")
//                    binding.webContent.evaluateJavascript(
//                        "javascript:insertImages($upImgListStr);",
//                        null
//                    )
                }
            } else {
                FileUtils.delete(photoFile)
            }
        } else if (requestCode == Constant.REQUEST_VIDEO_CAPTURE) {
            // 获取拍摄的视频
            if (resultCode == RESULT_OK) {
                if (videoFile != null) {
                    if ((AppFileUtils.getFileSize(videoFile!!) / (1024 * 1024)).toInt() > 200) {
                        FileUtils.delete(videoFile)
                        com.drake.tooltip.toast("视频文件不得大于200M")
                        return
                    }
                    Log.i(
                        "onResult",
                        "拍摄完 文件大小: " + AppFileUtils.getFileSize(videoFile!!) / (1024 * 1024)
                    )
                    val fileList = mutableListOf<File>()
                    val destPath = "${PathUtils.getExternalDcimPath()}/${videoFile!!.name}"
                    val vImgPath = AppFileUtils.extractAndSaveVideoFrame(0,videoFile!!.absolutePath)
                    val commands = Utils.getBoxScale(videoFile!!.absolutePath, destPath)
                    myRxFFmpegSubscriber = MyRxFFmpegSubscriber(
                        this,
                        compressDialog,
                        object : MyRxFFmpegSubscriber.FFmpegListener {
                            override fun onError() {
                                FileUtils.delete(videoFile)
                                com.drake.tooltip.toast("压缩视频失败,请重新选择")
                            }

                            override fun onFinish() {
//                                fileList.add(File(videoFile!!.absolutePath))
                                fileList.add(File(vImgPath))
                                fileList.add(File(destPath))
                                //执行上传操作
//                                upLoadImgOrVideoList(fileList, 2)
                                toast("执行上传完成并插入视频的操作")
//                                binding.webContent.evaluateJavascript(
//                                    "javascript:insertVideo('${data.paths[1]}','${data.paths[0]}');",
//                                    null
//                                )
                                FileUtils.delete(videoFile)
                            }
                        })
                    //开始执行FFmpeg命令
                    RxFFmpegInvoke.getInstance()
                        .runCommandRxJava(commands)
                        .subscribe(myRxFFmpegSubscriber)
                }
            } else {
                FileUtils.delete(videoFile)
            }
        }
    }

    private fun selectPhoto() {
        binding.webContent.evaluateJavascript("javascript:getImageCount();") { it ->
            PictureSelector.create(this)
                .openGallery(SelectMimeType.ofImage())
                .setSelectMaxFileSize(1024 * 10)
                .setMaxSelectNum(Constant.PHOTO_SELECT_MAX_NUMBER_THINK_TANK - it.toInt())
                .isWebp(true)
                .isGif(true)
                .isBmp(false)
                .isDisplayCamera(false)
                .setImageEngine(GlideEngine.createGlideEngine())
                .setSelectorUIStyle(PictureSelectorStyle())
                .isWithSelectVideoImage(false)
                .setMaxVideoSelectNum(1)
                .setSandboxFileEngine { context, srcPath, mineType, call ->
                    call?.onCallback(
                        srcPath,
                        SandboxTransformUtils.copyPathToSandbox(context, srcPath, mineType)
                    )
                }
                .forResult(object : OnResultCallbackListener<LocalMedia> {
                    override fun onResult(result: java.util.ArrayList<LocalMedia>) {
                        for (media in result) {
                            if (media.width == 0 || media.height == 0) {
                                val imageExtraInfo =
                                    MediaUtils.getImageSize(this@RichTextEditActivity, media.path)
                                media.width = imageExtraInfo.width
                                media.height = imageExtraInfo.height
                            }
                        }
                        val fileList = mutableListOf<File>()
                        result.map {
                            if (it.path.isNotEmpty() && it.path.startsWith("http") ||
                                it.path.startsWith("https")
                            ) {
                                return@map
                            }
                            fileList.add(File(it.availablePath))
                        }

                        if (fileList.isNotEmpty()) {
                            toast("执行上传完成并插入图片的操作")

//                            binding.webContent.evaluateJavascript(
//                                "javascript:insertImages($upImgListStr);",
//                                null
//                            )
                        }
                    }

                    override fun onCancel() {
                    }
                })
        }
    }

    @Suppress("DEPRECATION")
    private fun selectVideo() {
        binding.webContent.evaluateJavascript("javascript:getVideoCount();") { it ->
            PictureSelector.create(this)
                .openGallery(SelectMimeType.ofVideo())
                .setSelectMaxFileSize(1024 * 200)
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
                                    MediaUtils.getVideoSize(this@RichTextEditActivity, media.path)
                                media.width = videoExtraInfo.width
                                media.height = videoExtraInfo.height
                            }
                            val fileList = mutableListOf<File>()
                            val destPath = "${PathUtils.getExternalDcimPath()}/${
                                SimpleDateFormat("yyyyMMdd_HHmmss").format(
                                    Date()
                                )
                            }_${media.fileName}"
                            val commands = Utils.getBoxScale(media.realPath, destPath)
                            val vImgPath = AppFileUtils.extractAndSaveVideoFrame(0,media.realPath)
                            myRxFFmpegSubscriber = MyRxFFmpegSubscriber(
                                this@RichTextEditActivity,
                                compressDialog,
                                object : MyRxFFmpegSubscriber.FFmpegListener {
                                    override fun onError() {
                                        com.drake.tooltip.toast("压缩视频失败,请重新选择")
                                    }

                                    override fun onFinish() {
                                        fileList.add(File(vImgPath))
                                        fileList.add(File(destPath))
                                        toast("执行上传完成并插入视频的操作")
//                                        binding.webContent.evaluateJavascript(
//                                            "javascript:insertVideo('${data.paths[1]}','${data.paths[0]}');",
//                                            null
//                                        )
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

    }
}