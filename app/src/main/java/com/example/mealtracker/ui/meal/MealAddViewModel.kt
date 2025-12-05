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

/**
 * ViewModel to manage the state of the "Add Meal" screen.
 * Handles input validation and communicates with the repository to save data.
 */
class AddMealViewModel(private val mealRepository: MealsRepository): ViewModel() {

    // Holds the current state of the UI (form data and validation status).
    // Uses standard Compose State to trigger recompositions.
    var mealUiState by mutableStateOf(MealUiState())
        private set

    /**
     * Updates the UI state when the user types in the form.
     * Also triggers validation logic on every update.
     */
    fun updateUiState(mealDetails: MealDetails) {
        mealUiState =
            MealUiState(mealDetails = mealDetails, isEntryValid = validateInput(mealDetails))
    }

    /**
     * Inserts the new meal into the database.
     * Suspend function - must be called from a coroutine.
     */
    suspend fun saveMeal() {
        if (validateInput()) {
            mealRepository.upsertMeal(mealUiState.mealDetails.toMeal())
        }
    }

    /**
     * Checks if the input is valid (non-blank strings, calories > 0).
     */
    private fun validateInput(uiState: MealDetails = mealUiState.mealDetails): Boolean {
        return with(uiState) {
            name.isNotBlank() && calories != 0 && description.isNotBlank()
        }
    }
}

/**
 * Wrapper class for the UI State.
 * @param mealDetails The actual data entered by the user.
 * @param isEntryValid Flag indicating if the Save button should be enabled.
 */
data class MealUiState(
    val mealDetails: MealDetails = MealDetails(),
    val isEntryValid: Boolean = false
)

/**
 * Data class representing the form fields.
 * Similar to the 'Meal' entity but decoupled for UI purposes.
 */
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

/**
 * Extension function to convert UI state back to the Database Entity.
 */
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
 * Extension function to convert Database Entity to UI state.
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

/**
 * Helper to format the 'dateAdded' timestamp into a readable string.
 */
@RequiresApi(Build.VERSION_CODES.O)
fun Meal.toStringDate(dateAdded: Long): String {
    val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
    return Instant.ofEpochMilli(dateAdded)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
        .format(formatter)
}