package com.example.mealtracker.ui.meal

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

class EditMealViewModel(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    /**
     * Holds current item ui state
     */
    var mealUiState by mutableStateOf(MealUiState())
        private set

    private val itemId: Int = checkNotNull(savedStateHandle[EditMealDestination.itemIdArg])

    private fun validateInput(uiState: MealDetails = mealUiState.mealDetails): Boolean {
        return with(uiState) {
            name.isNotBlank() && image.isNotBlank() && description.isNotBlank() && calories != 0
        }
    }
}