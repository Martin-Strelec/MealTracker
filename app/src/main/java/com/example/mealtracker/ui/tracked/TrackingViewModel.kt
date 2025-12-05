package com.example.mealtracker.ui.tracked

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mealtracker.data.Meal
import com.example.mealtracker.data.MealsRepository
import com.example.mealtracker.data.TrackedMeal
import com.example.mealtracker.data.TrackedMealEntry
import com.example.mealtracker.ui.home.HomeUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId

/**
 * ViewModel for the Tracking Screen.
 * Manages the date filter, search query for adding meals, and the list of tracked meals.
 */
@RequiresApi(Build.VERSION_CODES.O)
class TrackingViewModel(private val mealsRepository: MealsRepository) : ViewModel() {

    // Current selected date (timestamp in millis)
    private val _selectedDate = MutableStateFlow(System.currentTimeMillis())
    val selectedDate = _selectedDate.asStateFlow()

    // Search query for the "Add Meal" dialog
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    /**
     * Stream of ALL available meals, filtered by the search query.
     * Used in the "Add Tracked Meal" dialog.
     */
    val allMealsState: StateFlow<List<Meal>> =
        combine(mealsRepository.getMealsOrderedByNameStream(), _searchQuery) {meals, query ->
            if (query.isBlank()) {
                meals
            } else {
                meals.filter {it.name.contains(query, ignoreCase = true)}
            }
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = listOf()
            )

    /**
     * Stream of Tracked Meals for the SELECTED date.
     * Combines the repository stream of all tracked meals with the selected date state.
     * Also calculates total calories on the fly.
     */
    val trackingUiState: StateFlow<TrackingUiState> =
        combine(mealsRepository.getAllTrackedMeals(), _selectedDate) { meals, date ->
            // Filter: Keep only meals where dateConsumed matches the selected date (ignoring time)
            val filteredMeals = meals.filter {isSameDay(it.dateConsumed, date) }
            // Calculate total calories for the filtered list
            val totalCalories = filteredMeals.sumOf { it.meal.calories }
            TrackingUiState(filteredMeals, totalCalories)
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = TrackingUiState()
            )

    /**
     * Updates the currently selected date.
     */
    fun updateSelectedDate(date: Long) {
        _selectedDate.value = date

    }

    /**
     * Adds a new record to the 'tracked_meals' table.
     * Links the meal ID with the currently selected date.
     */
    fun trackNewMeal(meal: Meal) {
        viewModelScope.launch {
            mealsRepository.insertTrackedMeal(meal.id, selectedDate.value)
        }
    }

    /**
     * Removes a tracked meal record.
     */
    fun removeTrackedMeal(entry: TrackedMealEntry) {
        viewModelScope.launch {
            mealsRepository.deleteTrackedMeal(entry.trackId, entry.meal.id, entry.dateConsumed)
        }
    }

    /**
     * Updates the search query for the dialog.
     */
    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    /**
     * Helper function to determine if two timestamps fall on the same calendar day.
     * Uses the system's default time zone.
     */
    private fun isSameDay(timestamp1: Long, timestamp2: Long): Boolean {
        val zoneId = ZoneId.systemDefault()
        val date1 = Instant.ofEpochMilli(timestamp1).atZone(zoneId).toLocalDate()
        val date2 = Instant.ofEpochMilli(timestamp2).atZone(zoneId).toLocalDate()
        return date1 == date2
    }

    companion object {
        // Timeout for keeping the flow active during configuration changes
        private const val TIMEOUT_MILLIS = 5_000L

    }
}

/**
 * Data class representing the state of the Tracking screen.
 */
data class TrackingUiState(
    val mealList: List<TrackedMealEntry> = listOf(),
    val totalCalories: Int = 0
)