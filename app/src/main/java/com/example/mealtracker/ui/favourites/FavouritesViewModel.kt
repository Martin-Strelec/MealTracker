package com.example.mealtracker.ui.favourites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mealtracker.data.Meal
import com.example.mealtracker.data.MealsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel for the Favourites Screen.
 * Manages the stream of favourite meals and handles search filtering.
 */
class FavouritesViewModel(private val mealsRepository: MealsRepository) : ViewModel() {

    // Internal mutable state for the search bar text
    private val _searchQuery = MutableStateFlow("")
    // Exposed immutable state for the UI
    val searchQuery = _searchQuery.asStateFlow()

    /**
     * The main UI state flow.
     * Combines the stream of ALL favourite meals from the repository with the current search query.
     * This ensures the list updates immediately when the database changes OR when the user types.
     */
    val favouritesUiState: StateFlow<FavouritesUiState> =
        combine(mealsRepository.getFavouriteMeals(), _searchQuery) { meals, query ->
            if (query.isBlank()) {
                // Return all favourites if no search query
                FavouritesUiState(meals)
            } else {
                // Filter the list by name if a query exists (case-insensitive)
                FavouritesUiState(meals.filter { it.name.contains(query, ignoreCase = true) })
            }
        }
            .stateIn(
                scope = viewModelScope, // Scope the Flow to the ViewModel lifecycle
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS), // Stop upstream flow 5s after UI disconnects
                initialValue = FavouritesUiState()
            )

    /**
     * Updates the search query state.
     */
    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

/**
 * Data class representing the UI state for the Favourites screen.
 */
data class FavouritesUiState(val mealList: List<Meal> = listOf())