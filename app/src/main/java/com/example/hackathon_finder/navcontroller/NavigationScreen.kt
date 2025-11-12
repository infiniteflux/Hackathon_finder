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
import com.example.hackathon_finder.bottomNavigation.AppBottomNavigation
import com.example.hackathon_finder.screens.ChatBotScreen
import com.example.hackathon_finder.screens.FavouriteScreen
import com.example.hackathon_finder.screens.HackathonWebViewScreen
import com.example.hackathon_finder.screens.HomeScreen
import com.example.hackathon_finder.screens.SearchHackathon
import com.example.hackathon_finder.viewModel.ChatBotViewModel
import com.example.hackathon_finder.viewModel.FavouriteViewModel
import com.example.hackathon_finder.viewModel.HackathonViewModel

@Composable
fun NavigationScreen(modifier: Modifier = Modifier,
                     hackathonViewModel: HackathonViewModel,
                     favouriteViewModel: FavouriteViewModel,
                     chatBotViewModel: ChatBotViewModel
) {
    val navController = rememberNavController()
    Scaffold(
        modifier = modifier,
        bottomBar = {
            AppBottomNavigation(navController = navController)
        })
    { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavHost(
                navController = navController,
                startDestination = AppRoutes.HOME
            ) {
                composable(AppRoutes.HOME){
                    HomeScreen(navController =navController)
                }

                composable (AppRoutes.HACKATHON){
                    SearchHackathon(navController = navController , hackathonViewModel = hackathonViewModel, favouriteViewModel= favouriteViewModel)
                }

                composable(
                    route = AppRoutes.WEBVIEW_ROUTE,
                    arguments = listOf(navArgument(AppRoutes.WEBVIEW_URL_ARG) {
                        type = NavType.StringType
                    })
                ) { backStackEntry ->
                    val encodedUrl = backStackEntry.arguments?.getString(AppRoutes.WEBVIEW_URL_ARG)
                    HackathonWebViewScreen(
                        encodedUrl = encodedUrl,
                        onBack = { navController.popBackStack() },
                        navController = navController
                    )
                }

                composable (AppRoutes.FAVOURITE){
                    FavouriteScreen(navController = navController, favouriteViewModel = favouriteViewModel)
                }

                composable(AppRoutes.CHATBOT) {
                    ChatBotScreen(chatBotViewModel=chatBotViewModel,
                        navController = navController)
                }
            }
        }
    }
}