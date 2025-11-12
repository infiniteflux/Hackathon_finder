package com.example.hackathon_finder.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.hackathon_finder.AppRoutes
import com.example.hackathon_finder.BuildConfig
import com.example.hackathon_finder.data.Hackathon
import com.example.hackathon_finder.viewModel.FavouriteViewModel
import com.example.hackathon_finder.viewModel.HackathonViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchHackathon(
    navController: NavController,
    hackathonViewModel: HackathonViewModel,
    favouriteViewModel: FavouriteViewModel
) {
    var topic by rememberSaveable { mutableStateOf("") }
    var technology by rememberSaveable { mutableStateOf("") }
    var prize by rememberSaveable { mutableStateOf("") }
    var country by rememberSaveable { mutableStateOf("") }
    val apiKey = BuildConfig.GEMINI_API_KEY

    val uiState by hackathonViewModel.uiState.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current

    val searchAction = {
        keyboardController?.hide()
        val apiKey = apiKey
        hackathonViewModel.findHackathons(topic, technology, prize, country, apiKey)
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Text(
                        text = "Search Hackathons",
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
        }
        ,
        containerColor = Color(0xFFF0F4F8)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            HackathonTextField(
                value = topic,
                onValueChange = { topic = it },
                label = "Hackathon Topic (e.g., AI, Health)",
                imeAction = ImeAction.Next
            )
            Spacer(modifier = Modifier.height(8.dp))
            HackathonTextField(
                value = technology,
                onValueChange = { technology = it },
                label = "Technology (e.g., Python, Mobile)",
                imeAction = ImeAction.Next
            )
            Spacer(modifier = Modifier.height(8.dp))
            HackathonTextField(
                value = prize,
                onValueChange = { prize = it },
                label = "Prize Pool (e.g., > $1000, any)",
                imeAction = ImeAction.Next
            )
            Spacer(modifier = Modifier.height(8.dp))


            HackathonTextField(
                value = country,
                onValueChange = { country = it },
                label = "Country (e.g., India, USA) or blank",
                imeAction = ImeAction.Search,
                onSearch = searchAction
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = searchAction,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Search", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    uiState.isLoading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                    uiState.error != null -> {
                        Text(
                            text = "An error occurred: ${uiState.error}",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    uiState.hackathons.isNotEmpty() -> {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(uiState.hackathons) { hackathon ->
                                HackathonCard(hackathon = hackathon,
                                    favouriteViewModel = favouriteViewModel,
                                    onClick = {
                                    if (hackathon.url.isNotBlank()) {
                                        navController.navigate(AppRoutes.getWebViewRoute(hackathon.url))
                                    }
                                })
                            }
                        }
                    }
                    !uiState.isLoading && uiState.error == null -> {
                        Text(
                            text = "Enter your criteria and tap 'Search' to find hackathons.",
                            color = Color.Gray,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(horizontal = 24.dp)
                        )
                    }
                }
            }
        }
    }

}

@Composable
private fun HackathonTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    imeAction: ImeAction,
    onSearch: () -> Unit = {}
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = KeyboardOptions(imeAction = imeAction),
        keyboardActions = KeyboardActions(
            onSearch = { onSearch() }
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HackathonCard(hackathon: Hackathon, onClick: () -> Unit, favouriteViewModel: FavouriteViewModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = hackathon.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )

               // Check once when composable loads
                val isFavourite = remember { mutableStateOf(false) }
                LaunchedEffect(Unit) {
                    favouriteViewModel.isHackathonExist(hackathon) {
                        isFavourite.value = it
                    }
                }

                IconButton(
                    onClick = {
                        if (isFavourite.value) {
                            favouriteViewModel.deleteHackathon(hackathon)
                        } else {
                            favouriteViewModel.saveHackathon(hackathon)
                        }
                        isFavourite.value = !isFavourite.value
                    }
                ) {
                    Icon(
                        imageVector = if (isFavourite.value) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = if (isFavourite.value) "Unfavorite" else "Favorite",
                        tint = if (isFavourite.value) Color.Red else MaterialTheme.colorScheme.primary
                    )
                }

            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = hackathon.description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(16.dp))

            InfoRow(
                icon = if (hackathon.mode.equals("Online", ignoreCase = true))
                    Icons.Default.Computer
                else
                    Icons.Default.LocationOn,
                text = "${hackathon.mode} - ${hackathon.location}"
            )
            Spacer(modifier = Modifier.height(8.dp))

            InfoRow(
                icon = Icons.Default.CalendarMonth,
                text = "${hackathon.startDate} to ${hackathon.endDate}"
            )
            Spacer(modifier = Modifier.height(8.dp))
            InfoRow(
                icon = Icons.Default.EmojiEvents,
                text = "Prize: ${hackathon.prize}"
            )

            if (hackathon.url.isNotBlank()) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "View Website",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Icon(
                        imageVector = Icons.Default.Public,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(start = 4.dp).size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun InfoRow(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

