package com.example.mealtracker.ui.meal

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.mealtracker.data.Meal
import com.example.mealtracker.data.MealsRepository
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

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
    val dateAdded: Long = System.currentTimeMillis(),
    val isFavourite: Boolean = false,
    val isTracked: Boolean = false
)

fun MealDetails.toMeal(): Meal = Meal(
    id = id,
    name = name,
    image = image,
    description = description,
    calories = calories,
    dateAdded = dateAdded,
    isFavourite = isFavourite,
    isTracked = isTracked

)

/**
 * Extension function to convert [Item] to [ItemUiState]
 */
fun Meal.toMealUiState(isEntryValid: Boolean = false): MealUiState = MealUiState(
    mealDetails = this.toMealDetails(),
    isEntryValid = isEntryValid
)

fun Meal.toMealDetails(): MealDetails = MealDetails(
    id = id,
    name = name,
    image = image,
    description = description,
    calories = calories,
    dateAdded = dateAdded,
    isFavourite = isFavourite,
    isTracked = isTracked
)

@RequiresApi(Build.VERSION_CODES.O)
fun Meal.toStringDate(dateAdded: Long): String {
    val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
    return Instant.ofEpochMilli(dateAdded)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
        .format(formatter)
}