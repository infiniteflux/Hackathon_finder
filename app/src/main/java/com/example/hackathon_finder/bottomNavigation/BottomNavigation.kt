package com.example.hackathon_finder.bottomNavigation

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.hackathon_finder.data.BottomNavItem
import com.example.hackathon_finder.screens.BrightBlue
import com.example.hackathon_finder.screens.DarkNavy
import com.example.hackathon_finder.screens.TextGray

@Composable
fun AppBottomNavigation(navController: NavController) {
    val items = listOf(
        BottomNavItem("Home", Icons.Default.Home, "Home"),
        BottomNavItem("Favorites", Icons.Default.Favorite, "Favourite"),
        BottomNavItem("ChatBot", Icons.AutoMirrored.Filled.Chat, "ChatBot")
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = DarkNavy,
        contentColor = BrightBlue
    ) {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    }
                },
                label = { Text(item.label) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        modifier = Modifier.size(28.dp)
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = BrightBlue,
                    selectedTextColor = BrightBlue,
                    unselectedIconColor = TextGray,
                    unselectedTextColor = TextGray,
                    indicatorColor = DarkNavy
                )
            )
        }
    }
}
