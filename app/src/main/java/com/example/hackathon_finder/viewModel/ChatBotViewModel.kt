package com.example.hackathon_finder.viewModel

import com.example.hackathon_finder.data.ChatMessage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hackathon_finder.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatBotViewModel : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages = _messages.asStateFlow()

    private val _inputText = MutableStateFlow("")
    val inputText = _inputText.asStateFlow()
    val apiKey = BuildConfig.GEMINI_API_KEY

    // --- Gemini API Setup ---
    private val generativeModel: GenerativeModel

    // 👇 System instruction as text
    private val systemInstructionText = """
        You are a helpful assistant for hackathon participants. 
        Your ONLY job is to provide tips, strategies, and advice related to hackathons (like ideas, pitching, coding, or team building).

        If the user asks about ANYTHING else (like weather, history, movies, or general chit-chat), 
        you MUST respond with this exact phrase: 
        "I'm sorry, I am only able to respond to questions about hackathons."
    """.trimIndent()

    init {
        // ✅ Use the new constructor (only modelName + apiKey are allowed)
        generativeModel = GenerativeModel(
            modelName = "gemini-1.5-flash-latest",
            apiKey =apiKey
        )

        // Add a welcome message
        _messages.value = listOf(
            ChatMessage(
                text = "Hi! I'm a hackathon expert. Ask me for tips, strategies, or preparation advice!",
                isFromUser = false
            )
        )
    }

    fun onInputTextChange(text: String) {
        _inputText.value = text
    }

    fun sendMessage() {
        val userMessageText = _inputText.value
        if (userMessageText.isBlank()) return

        val userMessage = ChatMessage(text = userMessageText, isFromUser = true)
        val typingMessage =
            ChatMessage(text = "Typing...", isFromUser = false, id = "typing-message")

        _messages.value = _messages.value + userMessage + typingMessage
        _inputText.value = ""

        viewModelScope.launch {
            try {
                // ✅ Convert system + user text into Content objects
                val systemContent = content(role = "system") {
                    text(systemInstructionText)
                }
                val userContent = content(role = "user") {
                    text(userMessageText)
                }

                // ✅ Generate content using both system + user messages
                val response = generativeModel.generateContent(
                    systemContent,
                    userContent
                )

                val botText = response.text ?: "Sorry, I'm having trouble thinking right now."
                val botResponse = ChatMessage(text = botText, isFromUser = false)

                _messages.value = _messages.value
                    .filterNot { it.id == "typing-message" } + botResponse

            } catch (e: Exception) {
                val errorResponse = ChatMessage(
                    text = "Sorry, I can't connect. Please check your internet.",
                    isFromUser = false
                )
                _messages.value = _messages.value
                    .filterNot { it.id == "typing-message" } + errorResponse
            }
        }
    }
}
