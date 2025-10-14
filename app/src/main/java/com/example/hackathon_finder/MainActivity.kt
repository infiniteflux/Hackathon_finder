package com.example.hackathon_finder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hackathon_finder.navcontroller.NavigationScreen
import com.example.hackathon_finder.ui.theme.Hackathon_finderTheme
import com.example.hackathon_finder.viewModel.HomeViewModel

object AppRoutes {
    const val HOME = "Home"
}
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Hackathon_finderTheme {
                val homeViewModel: HomeViewModel = viewModel()
                NavigationScreen(
                    homeViewModel = homeViewModel
                )
            }
        }
    }
}


