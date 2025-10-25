package com.example.hackathon_finder.navcontroller

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.hackathon_finder.AppRoutes
import com.example.hackathon_finder.screens.HackathonWebViewScreen
import com.example.hackathon_finder.screens.HomeScreen
import com.example.hackathon_finder.screens.SearchHackathon
import com.example.hackathon_finder.viewModel.HackathonViewModel
import com.example.hackathon_finder.viewModel.HomeViewModel

@Composable
fun NavigationScreen(modifier: Modifier = Modifier,
                     homeViewModel: HomeViewModel,
                     hackathonViewModel: HackathonViewModel
) {
    val navController = rememberNavController()
    Scaffold(modifier = modifier) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavHost(
                navController = navController,
                startDestination = AppRoutes.HOME
            ) {
                composable(AppRoutes.HOME){
                    HomeScreen(navController =navController , HomeViewModel = homeViewModel)
                }

                composable (AppRoutes.HACKATHON){
                    SearchHackathon(navController = navController , hackathonViewModel = hackathonViewModel)
                }

                // --- THIS IS THE FIX ---
                // Add the new destination for the WebView
                composable(
                    route = AppRoutes.WEBVIEW_ROUTE,
                    arguments = listOf(navArgument(AppRoutes.WEBVIEW_URL_ARG) {
                        type = NavType.StringType
                    })
                ) { backStackEntry ->
                    // Retrieve the encoded URL from the navigation arguments
                    val encodedUrl = backStackEntry.arguments?.getString(AppRoutes.WEBVIEW_URL_ARG)

                    // Show the WebView screen
                    HackathonWebViewScreen(
                        encodedUrl = encodedUrl,
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}