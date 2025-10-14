package com.example.hackathon_finder.bottomNavigation

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hackathon_finder.data.BottomNavItem
import com.example.hackathon_finder.screens.BrightBlue
import com.example.hackathon_finder.screens.DarkNavy
import com.example.hackathon_finder.screens.TextGray

@Composable
fun AppBottomNavigation(
    selectedItemIndex: Int,
    onItemSelected: (Int) -> Unit
) {
    val items = listOf(
        BottomNavItem("Explore", Icons.Default.Search),
        BottomNavItem("Favorites", Icons.Default.Favorite),
        BottomNavItem("Profile", Icons.Default.Person)
    )

    NavigationBar(
        containerColor = DarkNavy,
        contentColor = BrightBlue
    ) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedItemIndex == index,
                onClick = { onItemSelected(index) },
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
