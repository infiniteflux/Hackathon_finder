package com.example.hackathon_finder.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hackathon_finder.data.Hackathon
import com.example.hackathon_finder.data.HackathonUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class HackathonViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(HackathonUiState())
    val uiState = _uiState.asStateFlow()

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    fun findHackathons(
        topic: String,
        technology: String,
        prize: String,
        country: String,
        apiKey: String
    ) {
        if (apiKey.isBlank() || apiKey == "YOUR_API_KEY") {
            _uiState.update {
                it.copy(isLoading = false, error = "Please add your Gemini API key to SearchHackathon.kt")
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, hackathons = emptyList()) }
            try {
                val requestBody = createRequestBody(topic, technology, prize, country)
                val request = createRequest(apiKey, requestBody)

                val (responseCode, responseBody, fullError) = withContext(Dispatchers.IO) {
                    try {
                        val response = client.newCall(request).execute()
                        val body = response.body?.string()
                        if (response.isSuccessful && !body.isNullOrEmpty()) {
                            Triple(response.code, body, null)
                        } else {
                            // Try to parse the Google API error message
                            val errorBody = body ?: response.message
                            val errorMsg = try {
                                JSONObject(errorBody).getJSONObject("error").getString("message")
                            } catch (e: Exception) {
                                errorBody
                            }
                            Triple(response.code, null, "API Error: ${response.code} $errorMsg")
                        }
                    } catch (e: Exception) {
                        Triple(500, null, e.message ?: "An unknown network error occurred.")
                    }
                }

                if (responseBody != null) {
                    val hackathons = parseResponse(responseBody)
                    if (hackathons.isEmpty() && _uiState.value.error == null) {
                        _uiState.update { it.copy(isLoading = false, error = "No hackathons found matching your criteria.") }
                    } else {
                        _uiState.update { it.copy(isLoading = false, hackathons = hackathons) }
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = fullError) }
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = e.message ?: "An unknown error occurred.")
                }
            }
        }
    }

    private fun createRequestBody(
        topic: String,
        technology: String,
        prize: String,
        country: String
    ): String {

        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        val queryParts = mutableListOf("upcoming hackathons")
        if (topic.isNotBlank()) queryParts.add(topic)
        if (technology.isNotBlank()) queryParts.add(technology)
        if (country.isNotBlank()) {
            queryParts.add("in $country")
        }
        if (prize.isNotBlank()) queryParts.add("with prize pool over $prize")


        val userPrompt = queryParts.joinToString(" ")


        val systemInstruction = """
        You are a hackathon finder assistant. The user will provide a query.
        You MUST use the Google Search tool to find live, real-world hackathons that match the user's query.
        Today's date is $currentDate. You MUST only return events starting on or after this date.

        CRITICAL RULES:
        1.  **USE SEARCH:** You MUST use the search tool. Do not make up, hallucinate, or invent hackathons.
        2.  **REAL DATA ONLY:** Every field (name, url, prize) MUST be from the Google Search results.
        3.  **VALID URLs:** The 'url' MUST be a direct, valid, non-404 link to the hackathon's official page. **DO NOT return URLs starting with 'https://vertexaisearch.cloud.google.com/' or similar grounding/redirect links.**
        4.  **PRIZE:** The 'prize' string MUST include the correct currency symbol (e.g., ₹50,000, $1000 USD). If no prize is found, use "Not specified".
        5.  **LOCATION:** 'location' MUST be "City, State, Country" or "Online".
        6.  **FINAL RESPONSE:** After calling tools, your *final* response to the user MUST be **ONLY** a valid JSON array.
        7.  **NO FAKES:** If no real hackathons are found, you MUST return an empty array [].
        8.  **NO EXTRA TEXT:** Do NOT add any text like "Here are the hackathons..." or markdown `\`\`\`json` flags. Your entire response must be the JSON array itself.
        """.trimIndent()


        val root = JSONObject()
        val contents = JSONArray()
        val parts = JSONArray()
        parts.put(JSONObject().put("text", userPrompt))
        contents.put(JSONObject().put("parts", parts))
        root.put("contents", contents)

        val systemInstructionJson = JSONObject()
        systemInstructionJson.put("parts", JSONArray().put(JSONObject().put("text", systemInstruction)))
        root.put("systemInstruction", systemInstructionJson)

        val tools = JSONArray()
        tools.put(JSONObject().put("google_search", JSONObject()))
        root.put("tools", tools)

        return root.toString()
    }

    private fun createRequest(apiKey: String, requestBody: String): Request {
        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey"
        return Request.Builder()
            .url(url)
            .addHeader("Content-Type", "application/json")
            .post(requestBody.toRequestBody("application/json".toMediaType()))
            .build()
    }

    private fun parseResponse(responseBody: String): List<Hackathon> {
        return try {
            val mainResponse = JSONObject(responseBody)
            val text = mainResponse
                .getJSONArray("candidates")
                .getJSONObject(0)
                .getJSONObject("content")
                .getJSONArray("parts")
                .getJSONObject(0)
                .getString("text")

            val startIndex = text.indexOfFirst { it == '[' }
            val endIndex = text.indexOfLast { it == ']' }

            if (startIndex == -1 || endIndex == -1 || endIndex < startIndex) {
                if (text.contains("no hackathons", ignoreCase = true) || text.contains("couldn't find", ignoreCase = true)) {
                    return emptyList()
                }
                throw Exception("No valid JSON array found in AI response. Raw text: $text")
            }

            val cleanedText = text.substring(startIndex, endIndex + 1)

            val hackathonArray = JSONArray(cleanedText)
            val hackathons = mutableListOf<Hackathon>()
            for (i in 0 until hackathonArray.length()) {
                val h = hackathonArray.getJSONObject(i)

                val name = h.optString("name", "")
                val url = h.optString("url", "")

                if (name.isNotBlank() && url.isNotBlank()) {
                    hackathons.add(
                        Hackathon(
                            name = name,
                            description = h.optString("description", "No description provided."),
                            url = url,
                            startDate = h.optString("startDate", "TBA"),
                            endDate = h.optString("endDate", "TBA"),
                            prize = h.optString("prize", "Not specified"),
                            mode = h.optString("mode", "Not specified"),
                            location = h.optString("location", "Not specified")
                        )
                    )
                }
            }

            hackathons
        } catch (e: Exception) {
            throw Exception("Failed to parse AI's JSON response. Raw: $responseBody")
        }
    }
}

