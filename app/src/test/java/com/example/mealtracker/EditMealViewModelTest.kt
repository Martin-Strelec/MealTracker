package com.example.mealtracker

import androidx.lifecycle.SavedStateHandle
import com.example.mealtracker.data.Meal
import com.example.mealtracker.fakerepository.FakeMealsRepository
import com.example.mealtracker.rules.TestDispatcherRule
import com.example.mealtracker.ui.meal.MealEditViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.Rule
import kotlin.test.assertEquals

class EditMealViewModelTest {

    @get:Rule
    val testDispatcher = TestDispatcherRule()

    private lateinit var viewModel: MealEditViewModel
    private lateinit var fakeRepository: FakeMealsRepository

    private val mealId = 1
    private val sampleMeal = Meal(
        id = mealId,
        name = "Original Meal",
        description = "Original Description",
        calories = 100,
        dateAdded = 123456789L,
        isFavourite = false,
        isTracked = false,
        image = ""
    )

    @Before
    fun setup() {
        fakeRepository = FakeMealsRepository()
        fakeRepository.addMeal(sampleMeal)

        var savedStateHandle = SavedStateHandle(mapOf("itemId" to mealId))

        viewModel = MealEditViewModel(savedStateHandle, fakeRepository)
    }

    //Loading meal
    @Test
    fun mealEditViewModel_Initialization_LoadsCorrectly() {
        val state = viewModel.mealUiState
        assertEquals(sampleMeal.name, state.mealDetails.name)
        assertEquals(sampleMeal.description, state.mealDetails.description)
        assertEquals(sampleMeal.calories, state.mealDetails.calories)
        assertTrue(state.isEntryValid)
    }

    //Updating the state of the meal
    @Test
    fun mealEditViewModel_UpdateUiState_UpdatesState() {
        val newDetails = viewModel.mealUiState.mealDetails.copy(name = "Updated Name")
        viewModel.updateUiState(newDetails)

        assertEquals("Updated Name", viewModel.mealUiState.mealDetails.name)
        assertTrue(viewModel.mealUiState.isEntryValid)
    }

    //Validation check (returns false)
    @Test
    fun mealEditViewModel_InvalidInput_ReturnsFalse() {
        val invalidName = viewModel.mealUiState.mealDetails.copy(name = "")
        viewModel.updateUiState(invalidName)
        assertFalse(viewModel.mealUiState.isEntryValid)

        val invalidDescription = viewModel.mealUiState.mealDetails.copy(name = "Valid", description = "")
        viewModel.updateUiState(invalidDescription)
        assertFalse(viewModel.mealUiState.isEntryValid)

        val invalidCalories = viewModel.mealUiState.mealDetails.copy(name = "Valid", description = "Valid", calories = 0)
        viewModel.updateUiState(invalidCalories)
        assertFalse(viewModel.mealUiState.isEntryValid)
    }

    //Validation check (returns true)
    @Test
    fun mealEditViewModel_ValidInput_ReturnsTrue() {
        val validMeal = viewModel.mealUiState.mealDetails.copy(
            name = "Valid Name",
            description = "Valid Description",
            calories = 200
        )
        viewModel.updateUiState(validMeal)
        assertTrue(viewModel.mealUiState.isEntryValid)
    }

    //Updating the meal in the repository
    @Test
    fun mealEditViewModel_UpdateMeal_UpdatesRepository() = runTest {
        // 1. Update the UI state with new values
        val updatedName = "Final Name"
        val newDetails = viewModel.mealUiState.mealDetails.copy(name = updatedName)
        viewModel.updateUiState(newDetails)

        // 2. Call the updateItem function (Changed from updateMeal to match ViewModel)
        viewModel.updateItem()

        // 3. Verify the change is reflected in the repository
        val dbMeal = fakeRepository.getMealStream(mealId).first()
        assertEquals(updatedName, dbMeal?.name)
    }
}