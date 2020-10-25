package com.example.jetiq_chat.model

import java.util.*

data class ChatMessage(
    val text: String,
    val user: String,
    val timestamp: Date
)