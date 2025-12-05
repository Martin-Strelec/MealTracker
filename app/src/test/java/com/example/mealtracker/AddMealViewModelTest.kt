package com.example.mealtracker

import com.example.mealtracker.fakerepository.FakeMealsRepository
import com.example.mealtracker.rules.TestDispatcherRule
import com.example.mealtracker.ui.meal.AddMealViewModel
import com.example.mealtracker.ui.meal.MealDetails
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/**
 * Unit tests for the [AddMealViewModel].
 * Verifies the initial state and input validation logic before data is saved.
 */
class AddMealViewModelTest {

    // Replaces the Main dispatcher with a Test dispatcher for coroutine execution during tests.
    @get:Rule
    val testDispatcher = TestDispatcherRule()

    private lateinit var viewModel: AddMealViewModel
    private lateinit var fakeRepository: FakeMealsRepository

    /**
     * Test: Verifies that when the ViewModel is first created, the UI state is empty and invalid.
     */
    @Test
    fun addMealViewModel_Initialization_StateIsEmpty() {
        // Arrange: Initialize dependencies
        fakeRepository = FakeMealsRepository()
        viewModel = AddMealViewModel(fakeRepository)

        // Act: Retrieve current state
        val mealUiState = viewModel.mealUiState

        // Assert: Ensure fields are empty and isValid is false
        assertTrue(mealUiState.mealDetails.name.isEmpty())
        assertTrue(mealUiState.mealDetails.description.isEmpty())
        assertFalse(mealUiState.isEntryValid)
    }

    /**
     * Test: Verifies that validation logic correctly identifies invalid inputs.
     * Checks empty names, empty descriptions, and zero calories.
     */
    @Test
    fun addMealViewModel_InvalidInput_ReturnsFalse() {
        fakeRepository = FakeMealsRepository()
        viewModel = AddMealViewModel(fakeRepository)

        // Case 1: Empty Name
        val invalidMeal1 = MealDetails(name = "", description = "Valid", calories = 100)
        viewModel.updateUiState(invalidMeal1)
        assertFalse(viewModel.mealUiState.isEntryValid)

        // Case 2: Zero Calories
        val invalidMeal2 = MealDetails(name = "Valid", description = "Valid", calories = 0)
        viewModel.updateUiState(invalidMeal2)
        assertFalse(viewModel.mealUiState.isEntryValid)

        // Case 3: Empty Description
        val invalidMeal3 = MealDetails(name = "Valid", description = "", calories = 100)
        viewModel.updateUiState(invalidMeal3)
        assertFalse(viewModel.mealUiState.isEntryValid)
    }

    /**
     * Test: Verifies that valid input results in a valid state.
     */
    @Test
    fun addMealViewModel_ValidInput_ReturnsTrue() {
        fakeRepository = FakeMealsRepository()
        viewModel = AddMealViewModel(fakeRepository)

        // Arrange: Create a valid meal object
        val validMeal = MealDetails(name = "Apple", description = "Fruit", calories = 95)

        // Act: Update the ViewModel
        viewModel.updateUiState(validMeal)

        // Assert: State should be valid and match input
        assertTrue(viewModel.mealUiState.isEntryValid)
        assertEquals("Apple", viewModel.mealUiState.mealDetails.name)
    }
}