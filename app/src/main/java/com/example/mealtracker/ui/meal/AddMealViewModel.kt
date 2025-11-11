package com.example.mealtracker.ui.meal

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.mealtracker.data.Meal
import com.example.mealtracker.data.MealsRepository

class AddMealViewModel(private val mealRepository: MealsRepository): ViewModel() {
    var mealUiState by mutableStateOf(MealUiState())
        private set

    fun updateUiState(mealDetails: MealDetails) {
        mealUiState =
            MealUiState(mealDetails = mealDetails, isEntryValid = validateInput(mealDetails))
    }

    suspend fun saveMeal() {
        if (validateInput()) {
            mealRepository.upsertMeal(mealUiState.mealDetails.toMeal())
        }
    }

    private fun validateInput(uiState: MealDetails = mealUiState.mealDetails): Boolean {
        return with(uiState) {
            name.isNotBlank() && calories != 0 && description.isNotBlank()
        }
    }
}

/**
 * Represents Ui State for an Item.
 */
data class MealUiState(
    val mealDetails: MealDetails = MealDetails(),
    val isEntryValid: Boolean = false
)

data class MealDetails(
    val id: Int = 0,
    val name: String = "",
    val image: String = "",
    val description: String = "",
    val calories: Int = 0,
    val dateAdded: Long = System.currentTimeMillis()
)

fun MealDetails.toMeal(): Meal = Meal(
    id = id,
    name = name,
    image = "",
    description = description,
    calories = calories,
    dateAdded = System.currentTimeMillis()
)

fun Meal.toMealDetails(): MealDetails = MealDetails(
    id = id,
    name = name,
    image = "",
    description = description,
    calories = calories,
    dateAdded = System.currentTimeMillis()
)