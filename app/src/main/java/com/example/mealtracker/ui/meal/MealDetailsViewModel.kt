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

class MealDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val mealsRepository: MealsRepository
) : ViewModel() {

    private val mealId: Int = checkNotNull(savedStateHandle[MealDetailsDestination.itemIdArg])

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

    fun toggleFavourite() {
        val currentMeal = uiState.value.mealDetails.toMeal()
        val updatedMeal = currentMeal.copy(isFavourite = !currentMeal.isFavourite)
        viewModelScope.launch {
            mealsRepository.upsertMeal(updatedMeal)
        }
    }

    fun toggleTracked() {
        val currentMeal = uiState.value.mealDetails.toMeal()
        val updatedMeal = currentMeal.copy(isTracked = !currentMeal.isTracked)
        viewModelScope.launch {
            mealsRepository.upsertMeal(updatedMeal)
        }
    }

    suspend fun deleteMeal() {
        mealsRepository.deleteMeal(uiState.value.mealDetails.toMeal())
    }


    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

data class MealDetailsUiState(
    val mealDetails: MealDetails = MealDetails()
)