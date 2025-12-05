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

/**
 * Unit tests for [MealEditViewModel].
 * Tests loading existing data, validating edits, and saving changes back to the repository.
 */
class EditMealViewModelTest {

    // Rule to handle coroutine execution on the Main thread during tests
    @get:Rule
    val testDispatcher = TestDispatcherRule()

    private lateinit var viewModel: MealEditViewModel
    private lateinit var fakeRepository: FakeMealsRepository

    // Sample data for the test setup
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

    /**
     * Setup runs before every test.
     * 1. Creates a fake repository.
     * 2. Pre-populates it with a sample meal.
     * 3. Simulates SavedStateHandle to pass the meal ID to the ViewModel.
     */
    @Before
    fun setup() {
        fakeRepository = FakeMealsRepository()
        fakeRepository.addMeal(sampleMeal)

        // Simulate navigation arguments passing the ID '1'
        var savedStateHandle = SavedStateHandle(mapOf("itemId" to mealId))

        viewModel = MealEditViewModel(savedStateHandle, fakeRepository)
    }

    /**
     * Test: Verifies that the ViewModel correctly fetches the meal from the repository on init.
     */
    @Test
    fun mealEditViewModel_Initialization_LoadsCorrectly() {
        val state = viewModel.mealUiState
        assertEquals(sampleMeal.name, state.mealDetails.name)
        assertEquals(sampleMeal.description, state.mealDetails.description)
        assertEquals(sampleMeal.calories, state.mealDetails.calories)
        assertTrue(state.isEntryValid)
    }

    /**
     * Test: Verifies that calling updateUiState updates the local UI state correctly.
     */
    @Test
    fun mealEditViewModel_UpdateUiState_UpdatesState() {
        val newDetails = viewModel.mealUiState.mealDetails.copy(name = "Updated Name")
        viewModel.updateUiState(newDetails)

        assertEquals("Updated Name", viewModel.mealUiState.mealDetails.name)
        assertTrue(viewModel.mealUiState.isEntryValid)
    }

    /**
     * Test: Verifies validation logic fails for invalid inputs during editing.
     */
    @Test
    fun mealEditViewModel_InvalidInput_ReturnsFalse() {
        // Invalid Name
        val invalidName = viewModel.mealUiState.mealDetails.copy(name = "")
        viewModel.updateUiState(invalidName)
        assertFalse(viewModel.mealUiState.isEntryValid)

        // Invalid Description
        val invalidDescription = viewModel.mealUiState.mealDetails.copy(name = "Valid", description = "")
        viewModel.updateUiState(invalidDescription)
        assertFalse(viewModel.mealUiState.isEntryValid)

        // Invalid Calories
        val invalidCalories = viewModel.mealUiState.mealDetails.copy(name = "Valid", description = "Valid", calories = 0)
        viewModel.updateUiState(invalidCalories)
        assertFalse(viewModel.mealUiState.isEntryValid)
    }

    /**
     * Test: Verifies validation logic passes for valid inputs.
     */
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

    /**
     * Test: Verifies that calling updateItem() actually persists the changes to the fake repository.
     * Uses [runTest] because repository operations are suspending functions.
     */
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