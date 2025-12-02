package com.example.mealtracker.ui.tracked

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mealtracker.data.Meal
import com.example.mealtracker.data.MealsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class TrackingViewModel(private val mealsRepository: MealsRepository) : ViewModel() {

    val trackingUiState: StateFlow<TrackingUiState> =
        mealsRepository.getTrackedMeals().map {TrackingUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = TrackingUiState()
            )

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L

    }
}

data class TrackingUiState(val mealList: List<Meal> = listOf())