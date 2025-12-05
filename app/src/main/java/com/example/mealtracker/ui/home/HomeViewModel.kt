package com.example.mealtracker.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mealtracker.data.Meal
import com.example.mealtracker.data.MealsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel for the Home Screen.
 * Responsible for holding the search query state and providing a filtered list of meals to the UI.
 */
class HomeViewModel(private val mealsRepository: MealsRepository) : ViewModel() {

    // Internal mutable state for the search query entered by the user.
    private val _searchQuery = MutableStateFlow("")
    // Exposed immutable state for the UI to observe.
    val searchQuery = _searchQuery.asStateFlow()

    /**
     * The main UI state flow.
     * It combines two streams of data:
     * 1. The list of meals from the database (via Repository).
     * 2. The current search query string.
     *
     * This ensures that whenever the user types OR the database updates, this flow re-emits a new list.
     */
    val homeUiState: StateFlow<HomeUiState> =
        combine(mealsRepository.getMealsOrderedByNameStream(), _searchQuery) { meals, query ->
            if (query.isBlank()) {
                // If the search bar is empty, return the full list.
                HomeUiState(meals)
            } else {
                // If there is a query, filter the list by name (case-insensitive).
                HomeUiState(meals.filter { it.name.contains(query, ignoreCase = true) })
            }
        }
            .stateIn(
                scope = viewModelScope, // Tie the flow's lifecycle to the ViewModel.
                // Stop the upstream flow 5 seconds after the last subscriber disappears (e.g., rotation).
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = HomeUiState() // Initial empty state.
            )

    /**
     * Updates the search query state.
     * Called by the UI TextField onValueChange.
     */
    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    companion object {
        // Time in milliseconds to keep the flow active after UI unsubscription.
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

/**
 * Data class representing the UI state for the Home screen.
 */
data class HomeUiState(val mealList: List<Meal> = listOf())