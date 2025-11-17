package com.example.hackathon_finder.viewModel

import com.example.hackathon_finder.data.ChatMessage
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hackathon_finder.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit


class ChatBotViewModel : ViewModel() {

    companion object {
        private const val TAG = "ChatBotViewModel"
    }

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages = _messages.asStateFlow()

    private val _inputText = MutableStateFlow("")
    val inputText = _inputText.asStateFlow()

    private val apiKey = BuildConfig.GEMINI_API_KEY

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    // System instruction text
    private val systemInstructionText = """
        You are a helpful assistant for hackathon participants. 
        Your ONLY job is to provide tips, strategies, and advice related to hackathons (like ideas, pitching, coding, or team building).

        If the user asks about ANYTHING else (like weather, history, movies, or general chit-chat), 
        you MUST respond with this exact phrase: 
        "I'm sorry, I am only able to respond to questions about hackathons."
    """.trimIndent()

    init {
        Log.d(TAG, "ChatBotViewModel initialized")

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
        Log.d(TAG, "Input text changed: $text")
    }

    fun sendMessage() {
        val userMessageText = _inputText.value
        Log.d(TAG, "sendMessage() called with: '$userMessageText'")

        if (userMessageText.isBlank()) {
            Log.w(TAG, "Message is blank, ignoring")
            return
        }

        val userMessage = ChatMessage(text = userMessageText, isFromUser = true)
        val typingMessage = ChatMessage(
            text = "Typing...",
            isFromUser = false,
            id = "typing-message"
        )

        _messages.value = _messages.value + userMessage + typingMessage
        _inputText.value = ""

        Log.d(TAG, "Message added to list. Total messages: ${_messages.value.size}")

        viewModelScope.launch {
            try {
                Log.d(TAG, "Starting API call to Gemini REST API...")

                val fullPrompt = """
                    $systemInstructionText
                    
                    User: $userMessageText
                """.trimIndent()

                val response = callGeminiAPI(fullPrompt)

                Log.d(TAG, "API call successful!")
                Log.d(TAG, "Response text: $response")

                val botResponse = ChatMessage(text = response, isFromUser = false)

                _messages.value = _messages.value
                    .filterNot { it.id == "typing-message" } + botResponse

                Log.d(TAG, "Bot response added. Total messages: ${_messages.value.size}")

            } catch (e: Exception) {
                Log.e(TAG, "Error in sendMessage(): ${e.message}", e)
                e.printStackTrace()

                val errorResponse = ChatMessage(
                    text = "Error: ${e.message ?: "Unknown error"}",
                    isFromUser = false
                )
                _messages.value = _messages.value
                    .filterNot { it.id == "typing-message" } + errorResponse

                Log.d(TAG, "Error message added. Total messages: ${_messages.value.size}")
            }
        }
    }

    private suspend fun callGeminiAPI(prompt: String): String = withContext(Dispatchers.IO) {
        // Use the v1 API endpoint with gemini-1.5-flash
        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-preview-09-2025:generateContent?key=$apiKey"

        val jsonBody = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", prompt)
                        })
                    })
                })
            })
        }

        Log.d(TAG, "Request URL: $url")
        Log.d(TAG, "Request body: ${jsonBody.toString(2)}")

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = jsonBody.toString().toRequestBody(mediaType)

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        val response = client.newCall(request).execute()
        val responseBody = response.body?.string() ?: throw Exception("Empty response")

        Log.d(TAG, "Response code: ${response.code}")
        Log.d(TAG, "Response body: $responseBody")

        if (!response.isSuccessful) {
            throw Exception("API Error ${response.code}: $responseBody")
        }

        val jsonResponse = JSONObject(responseBody)
        val candidates = jsonResponse.getJSONArray("candidates")
        val content = candidates.getJSONObject(0).getJSONObject("content")
        val parts = content.getJSONArray("parts")
        val text = parts.getJSONObject(0).getString("text")

        text
    }
}