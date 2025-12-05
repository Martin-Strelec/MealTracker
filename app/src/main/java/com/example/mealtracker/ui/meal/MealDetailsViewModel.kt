package com.example.mealtracker.ui.meal

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mealtracker.data.MealsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel for the Meal Details screen.
 * Fetches the specific meal based on the ID passed in the navigation arguments.
 */
class MealDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val mealsRepository: MealsRepository
) : ViewModel() {

    // Retrieve the meal ID from the navigation arguments
    private val mealId: Int = checkNotNull(savedStateHandle[MealDetailsDestination.itemIdArg])

    /**
     * StateFlow holding the details of the requested meal.
     * Maps the Repository Flow<Meal?> to MealDetailsUiState.
     */
    val uiState: StateFlow<MealDetailsUiState> =
        mealsRepository.getMealStream(mealId)
            .filterNotNull()
            .map{
                MealDetailsUiState(mealDetails = it.toMealDetails())
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = MealDetailsUiState()
            )

    /**
     * Toggles the 'Favourite' boolean flag for the current meal.
     * Updates the database asynchronously.
     */
    fun toggleFavourite() {
        val currentMeal = uiState.value.mealDetails.toMeal()
        val updatedMeal = currentMeal.copy(isFavourite = !currentMeal.isFavourite)
        viewModelScope.launch {
            mealsRepository.upsertMeal(updatedMeal)
        }
    }

    /**
     * Toggles the 'Tracked' boolean flag.
     * (Note: Logic might need review depending on how 'tracking' is defined in the requirement,
     * usually tracking is a separate table, but this toggles a flag on the Meal entity).
     */
    fun toggleTracked() {
        val currentMeal = uiState.value.mealDetails.toMeal()
        val updatedMeal = currentMeal.copy(isTracked = !currentMeal.isTracked)
        viewModelScope.launch {
            mealsRepository.upsertMeal(updatedMeal)
        }
    }

    /**
     * Deletes the current meal from the database.
     */
    suspend fun deleteMeal() {
        mealsRepository.deleteMeal(uiState.value.mealDetails.toMeal())
    }


    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

/**
 * UI State for the Details screen.
 */
data class MealDetailsUiState(
    val mealDetails: MealDetails = MealDetails()
)