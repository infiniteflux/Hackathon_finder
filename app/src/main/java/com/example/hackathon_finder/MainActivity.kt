package com.example.hackathon_finder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hackathon_finder.navcontroller.NavigationScreen
import com.example.hackathon_finder.ui.theme.Hackathon_finderTheme
import com.example.hackathon_finder.viewModel.HackathonViewModel
import com.example.hackathon_finder.viewModel.HomeViewModel
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object AppRoutes {
    const val HOME = "Home"
    const val HACKATHON = "SearchHackathon"
    private const val WEBVIEW_SCREEN = "webview"
    const val WEBVIEW_URL_ARG = "url"
    const val WEBVIEW_ROUTE = "$WEBVIEW_SCREEN?$WEBVIEW_URL_ARG={$WEBVIEW_URL_ARG}"

    // 2. Add a helper function to safely build the route with an encoded URL
    fun getWebViewRoute(url: String): String {
        val encodedUrl = URLEncoder.encode(url, StandardCharsets.UTF_8.toString())
        return "$WEBVIEW_SCREEN?$WEBVIEW_URL_ARG=$encodedUrl"
    }
}
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Hackathon_finderTheme {
                val homeViewModel: HomeViewModel = viewModel()
                val hackathonViewModel: HackathonViewModel = viewModel()
                NavigationScreen(
                    homeViewModel = homeViewModel,
                    hackathonViewModel = hackathonViewModel
                )
            }
        }
    }
}


