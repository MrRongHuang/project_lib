package com.koi.projectlib.ui.ac

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.WindowManager
import android.webkit.*
import androidx.recyclerview.widget.RecyclerView
import com.drake.brv.utils.bindingAdapter
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import com.drake.engine.base.EngineActivity
import com.drake.net.utils.TipUtils.toast
import com.drake.statusbar.immersive
import com.google.gson.Gson
import com.koi.projectlib.R
import com.koi.projectlib.databinding.ActivityRichTextEditBinding
import com.koi.projectlib.dialog.ThinkTankEdtLinkPop
import com.koi.projectlib.model.EditLinkModel
import com.koi.projectlib.model.RichTextTypeModel
import com.koi.projectlib.model.UndoRedoModel
import com.lxj.xpopup.XPopup

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
                binding.webContent.settings.javaScriptEnabled = false
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
                        }
                        "视频" -> {
                            toast("视频")
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
                            binding.webContent.evaluateJavascript(
                                "javascript:focus();insertLink('$link','$describe');", null
                            )
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
        wbContent.loadUrl("file:///android_asset/editor.html")
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
        }
    }

    private val webChromeClient = object : WebChromeClient() {

    }
}