package com.koi.projectlib.chat

class AIResponse {
    fun getResponse(message: String): String {
        return when {
            message.contains("hello", ignoreCase = true) -> "Hi there! How can I help you?"
            message.contains("weather", ignoreCase = true) -> "The weather today is sunny with a high of 25°C."
            else -> "大哥，你再说什么?大哥。"
        }
    }
}