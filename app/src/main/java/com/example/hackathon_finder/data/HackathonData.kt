package com.example.hackathon_finder.data

data class Hackathon(
    val name: String,
    val description: String,
    val url: String,
    val startDate: String,
    val endDate: String,
    val prize: String,
    val mode: String,
    val  location : String
)

data class HackathonUiState(
    val hackathons: List<Hackathon> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
