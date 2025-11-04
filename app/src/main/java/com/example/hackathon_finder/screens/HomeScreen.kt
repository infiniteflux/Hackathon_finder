package com.example.hackathon_finder.screens
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.hackathon_finder.AppRoutes
import com.example.hackathon_finder.R
import com.example.hackathon_finder.viewModel.HomeViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    HomeViewModel: HomeViewModel
) {
    val uiState by HomeViewModel.uiState.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavy)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            painter = painterResource(id = R.drawable.img),
            contentDescription = "Hackathon Illustration",
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentScale = ContentScale.Fit
        )

        Text(
            text = "Find Your Next Hackathon",
            color = Color.White,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            lineHeight = 40.sp
        )

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Discover exciting hackathons and connect with fellow innovators.",
            color = TextGray,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )
        Spacer(modifier = Modifier.weight(0.5f))

        Button(
            onClick = { navController.navigate(AppRoutes.HACKATHON) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BrightBlue)
        ) {
            Text(
                text = "Start Exploring",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}


val DarkNavy @Composable get() = colorResource(id = R.color.dark_navy)
val BrightBlue @Composable get() = colorResource(id = R.color.bright_blue)
val TextGray @Composable get() = colorResource(id = R.color.text_gray)

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun HomeScreenPreview() {
    HomeScreen(navController = rememberNavController(), HomeViewModel = viewModel ())
}
