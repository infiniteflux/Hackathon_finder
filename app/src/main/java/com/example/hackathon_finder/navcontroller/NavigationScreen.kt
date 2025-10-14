package com.example.hackathon_finder.navcontroller

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.hackathon_finder.AppRoutes
import com.example.hackathon_finder.screens.HomeScreen
import com.example.hackathon_finder.viewModel.HomeViewModel

@Composable
fun NavigationScreen(modifier: Modifier = Modifier,
                     homeViewModel: HomeViewModel
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
            }
        }
    }
}