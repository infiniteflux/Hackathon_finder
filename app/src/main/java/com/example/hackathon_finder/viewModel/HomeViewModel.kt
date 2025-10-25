package com.example.hackathon_finder.viewModel
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class HomeUiState(
    val selectedNavItem: Int = 0
)

class HomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    /**
     * Updates the currently selected navigation item.
     * @param index The index of the selected item (0: Explore, 1: Favorites, 2: Profile)
     */
    fun onNavItemClicked(index: Int) {
        _uiState.update { currentState ->
            currentState.copy(selectedNavItem = index)
        }
    }

    /**
     * Placeholder function for when the main button is clicked.
     * You would typically trigger navigation or another business logic event here.
     */
    fun onStartExploringClicked() {

        println("Start Exploring Clicked!")
    }
}