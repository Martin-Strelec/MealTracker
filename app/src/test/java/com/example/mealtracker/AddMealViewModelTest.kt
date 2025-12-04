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


class AddMealViewModelTest {

    @get:Rule
    val testDispatcher = TestDispatcherRule()

    private lateinit var viewModel: AddMealViewModel
    private lateinit var fakeRepository: FakeMealsRepository

    @Test
    fun addMealViewModel_Initialization_StateIsEmpty() {
        fakeRepository = FakeMealsRepository()
        viewModel = AddMealViewModel(fakeRepository)

        val mealUiState = viewModel.mealUiState
        assertTrue(mealUiState.mealDetails.name.isEmpty())
        assertTrue(mealUiState.mealDetails.description.isEmpty())
        assertFalse(mealUiState.isEntryValid)
    }

    @Test
    fun addMealViewModel_InvalidInput_ReturnsFalse() {
        fakeRepository = FakeMealsRepository()
        viewModel = AddMealViewModel(fakeRepository)

        val invalidMeal1 = MealDetails(name = "", description = "Valid", calories = 100)
        viewModel.updateUiState(invalidMeal1)
        assertFalse(viewModel.mealUiState.isEntryValid)

        val invalidMeal2 = MealDetails(name = "Valid", description = "Valid", calories = 0)
        viewModel.updateUiState(invalidMeal2)
        assertFalse(viewModel.mealUiState.isEntryValid)

        val invalidMeal3 = MealDetails(name = "Valid", description = "", calories = 100)
        viewModel.updateUiState(invalidMeal3)
        assertFalse(viewModel.mealUiState.isEntryValid)
    }

    @Test
    fun addMealViewModel_ValidInput_ReturnsTrue() {
        fakeRepository = FakeMealsRepository()
        viewModel = AddMealViewModel(fakeRepository)

        val validMeal = MealDetails(name = "Apple", description = "Fruit", calories = 95)
        viewModel.updateUiState(validMeal)
        assertTrue(viewModel.mealUiState.isEntryValid)
        assertEquals("Apple", viewModel.mealUiState.mealDetails.name)
    }
}