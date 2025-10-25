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


class HackathonViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(HackathonUiState())
    val uiState = _uiState.asStateFlow()

    private val client = OkHttpClient()

    /**
     * Find hackathons based on below criteria:
     * @param data - topic of hackathon
     * @param technology - domain
     * @param prize - prize range
     * @param country - country to search in (blank for worldwide)
     * @param apiKey - your api key
     */

    fun findHackathons(
        data: String,
        technology: String,
        prize: String,
        country: String, // <-- ADDED COUNTRY
        apiKey: String
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, hackathons = emptyList()) }

            // Handle empty API key before making a call
            if (apiKey.isBlank() || apiKey == "YOUR_API_KEY") {
                _uiState.update { it.copy(isLoading = false, error = "Please add your API key in SearchHackathon.kt") }
                return@launch
            }

            try {
                // --- PASS COUNTRY TO REQUEST BODY ---
                val requestBody = createRequestBody(data, technology, prize, country)
                val request = createRequest(apiKey, requestBody)

                // --- FIX: Wrap the blocking .execute() call in withContext(Dispatchers.IO) ---
                val response = withContext(Dispatchers.IO) {
                    client.newCall(request).execute()
                }
                // --- END FIX ---

                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    if (!responseBody.isNullOrEmpty()) {
                        // Also wrap parsing in a background thread in case JSON is large
                        val hackathons = withContext(Dispatchers.Default) {
                            parseResponse(responseBody)
                        }
                        _uiState.update { it.copy(isLoading = false, hackathons = hackathons) }
                    } else {
                        _uiState.update { it.copy(isLoading = false, error = "Empty response from API.") }
                    }
                } else {
                    val errorBody = response.body?.string()
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "API Error: ${response.code} ${response.message} - ${errorBody ?: "No details"}"
                        )
                    }
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = e.message ?: "An unknown error occurred.")
                }
            }
        }
    }

    // --- ENTIRE FUNCTION UPDATED TO BE SAFER ---
    private fun createRequestBody(
        data: String,
        technology: String,
        prize: String,
        country: String // <-- ADDED COUNTRY
    ): String {

        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        // --- UPDATED PROMPT ---
        val prompt = """
            You are a hackathon finder assistant. 
            Given the user's criteria, return a JSON array of hackathons.
            
            USER CRITERIA:
            - Topic/Field: "$data"
            - Technology Focus: "$technology"
            - Prize: "$prize"
            - Country: "${if (country.isBlank()) "Worldwide" else country}"
            
            RULES:
            1. Today's date is $currentDate. You MUST only return hackathons starting on or after this date. Do NOT include finished events.
            2. If Country is "Worldwide", search all countries. Otherwise, search only in the specified country (or online events open to that country).
            
            JSON OUTPUT FORMAT:
            Each hackathon must include the following keys:
            "name", "description", "url", "startDate", "endDate", "prize",
            "mode" (must be "Online" or "Offline"),
            "location" (must be in "Area, State, Country" format, or "Online" if mode is Online)

            PRIZE FORMAT:
            The "prize" field MUST include the correct local currency symbol based on the hackathon's location (e.g., "$5,000 USD", "₹1,00,000 INR", "€10,000").

            Respond **only** with valid JSON (no extra text outside the array). 
            If no hackathons are found, return an empty array [].
        """.trimIndent()
        // --- END UPDATED PROMPT ---


        // 2. Build the JSON using JSONObject and JSONArray
        val root = JSONObject()
        root.put("model", "provider-5/grok-4-0709")

        val messagesArray = JSONArray()

        val systemMessage = JSONObject()
        systemMessage.put("role", "system")
        systemMessage.put("content", "You are a helpful assistant that finds hackathons based on user criteria.")

        val userMessage = JSONObject()
        userMessage.put("role", "user")
        userMessage.put("content", prompt) // JSONObject handles escaping the prompt string

        messagesArray.put(systemMessage)
        messagesArray.put(userMessage)

        root.put("messages", messagesArray)
        root.put("temperature", 0.5)
        root.put("max_tokens", 1024) // Increased max_tokens for more data

        // 3. Return the JSON as a string
        return root.toString()
    }
    // --- END OF UPDATE ---

    private fun createRequest(apiKey: String, requestBody: String): Request {
        return Request.Builder()
            .url("https://api.a4f.co/v1/chat/completions")
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("Content-Type", "application/json")
            .post(requestBody.toRequestBody("application/json".toMediaType()))
            .build()
    }

    private fun parseResponse(responseBody: String): List<Hackathon> {
        try {
            val jsonResponse = JSONObject(responseBody)
            val text = jsonResponse
                .getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content")

            // The API might return the JSON as a string, sometimes with markdown backticks
            // This makes the parsing more robust.
            val cleanedText = text.trim().removePrefix("```json").removeSuffix("```")

            val hackathonArray = JSONArray(cleanedText)
            val hackathons = mutableListOf<Hackathon>()
            for (i in 0 until hackathonArray.length()) {
                val h = hackathonArray.getJSONObject(i)
                hackathons.add(
                    Hackathon(
                        name = h.optString("name", "No Name"),
                        description = h.optString("description", "No Description"),
                        url = h.optString("url", ""),
                        startDate = h.optString("startDate", "TBA"),
                        endDate = h.optString("endDate", "TBA"),
                        prize = h.optString("prize", "N/A"),
                        mode = h.optString("mode", "N/A"),
                        location = h.optString("location", "N/A")
                    )
                )
            }
            return hackathons
        } catch (e: Exception) {
            // This will give a much more useful error message if the AI's response is bad
            throw Exception("Failed to parse JSON response from AI. Details: ${e.message}")
        }
    }
}

