package com.example.mealtracker.ui.favourites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mealtracker.data.Meal
import com.example.mealtracker.data.MealsRepository
import com.example.mealtracker.ui.home.HomeUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class FavouritesViewModel(private val mealsRepository: MealsRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    val favouritesUiState: StateFlow<FavouritesUiState> =
        combine(mealsRepository.getMealsOrderedByNameStream(), _searchQuery) {meals, query ->
            if (query.isBlank()) {
                FavouritesUiState(meals)
            } else {
                FavouritesUiState(meals.filter {it.name.contains(query, ignoreCase = true)})
            }
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = FavouritesUiState()
            )

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

data class FavouritesUiState(val mealList: List<Meal> = listOf())