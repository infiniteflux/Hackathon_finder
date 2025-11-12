package com.example.hackathon_finder.data

import java.util.UUID


data class ChatMessage(
    val id: String = UUID.randomUUID().toString(), // 👈 ID is required
    val text: String,
    val isFromUser: Boolean
)