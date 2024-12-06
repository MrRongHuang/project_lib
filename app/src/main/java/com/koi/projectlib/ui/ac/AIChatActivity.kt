package com.koi.projectlib.ui.ac

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.drake.brv.utils.divider
import com.drake.engine.base.EngineActivity
import com.drake.statusbar.immersive
import com.hjq.bar.OnTitleBarListener
import com.hjq.bar.TitleBar
import com.koi.projectlib.R
import com.koi.projectlib.adapter.ChatAdapter
import com.koi.projectlib.chat.AIResponse
import com.koi.projectlib.databinding.ActivityAichatBinding
import com.koi.projectlib.model.Message

class AIChatActivity : EngineActivity<ActivityAichatBinding>(R.layout.activity_aichat) {
    private val messages = mutableListOf<Message>()
    private lateinit var adapter: ChatAdapter
    val aiResponse = AIResponse()

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

        adapter = ChatAdapter(messages)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.divider {
            setDrawable(R.drawable.shape_divider_vertical_transparent_height_10dp)
            includeVisible = true
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
    }

    override fun onClick(v: View) {
        when (v) {
            binding.buttonSend -> {
                val userMessage = binding.editTextMessage.text.toString().trim()
                if (userMessage.isNotEmpty()) {
                    messages.add(Message(userMessage, true))
                    adapter.notifyItemInserted(messages.size - 1)
                    binding.recyclerView.scrollToPosition(messages.size - 1)

                    // Clear input
                    binding.editTextMessage.text?.clear()

                    // Generate AI response
                    val response = aiResponse.getResponse(userMessage)
                    messages.add(Message(response, false))
                    adapter.notifyItemInserted(messages.size - 1)
                    binding.recyclerView.scrollToPosition(messages.size - 1)
                }
            }
        }
    }

}