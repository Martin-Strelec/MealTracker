package com.example.mealtracker.ui.meal

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mealtracker.data.MealsRepository
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * ViewModel for the Edit Meal Screen.
 * Retrieves initial data from the repository and manages the form state.
 */
class MealEditViewModel(
    savedStateHandle: SavedStateHandle,
    private val mealsRepository: MealsRepository
) : ViewModel() {

    /**
     * Holds current item ui state (form data).
     */
    var mealUiState by mutableStateOf(MealUiState())
        private set

    private val mealId: Int = checkNotNull(savedStateHandle[EditMealDestination.itemIdArg])

    // Initializer block to fetch the meal details when the ViewModel is created.
    init {
        viewModelScope.launch {
            // Fetch the stream, take the first non-null value, and map it to UI state.
            mealUiState = mealsRepository.getMealStream(mealId)
                .filterNotNull()
                .first()
                .toMealUiState(true) // Set isValid to true initially since it's existing valid data
        }
    }

    /**
     * Updates the [mealUiState] with the value provided in the argument. This method also triggers
     * a validation for input values.
     */
    fun updateUiState(mealDetails: MealDetails) {
        mealUiState =
            MealUiState(mealDetails = mealDetails, isEntryValid = validateInput(mealDetails))
    }

    /**
     * Update the item in the [MealsRepository]'s data source.
     * Called when the user clicks Save.
     */
    suspend fun updateItem() {
        if (validateInput(mealUiState.mealDetails)) {
            mealsRepository.upsertMeal(mealUiState.mealDetails.toMeal())
        }
    }

    /**
     * Validates input (Name and Description must not be blank).
     */
    private fun validateInput(uiState: MealDetails = mealUiState.mealDetails): Boolean {
        return with(uiState) {
            name.isNotBlank()  && description.isNotBlank() && calories != 0
        }
    }
}