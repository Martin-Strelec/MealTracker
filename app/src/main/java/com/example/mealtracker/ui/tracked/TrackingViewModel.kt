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

@RequiresApi(Build.VERSION_CODES.O)
class TrackingViewModel(private val mealsRepository: MealsRepository) : ViewModel() {

    private val _selectedDate = MutableStateFlow(System.currentTimeMillis())
    val selectedDate = _selectedDate.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

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
    val trackingUiState: StateFlow<TrackingUiState> =
        combine(mealsRepository.getAllTrackedMeals(), _selectedDate) { meals, date ->
            val filteredMeals = meals.filter {isSameDay(it.dateConsumed, date) }
            val totalCalories = filteredMeals.sumOf { it.meal.calories }
            TrackingUiState(filteredMeals, totalCalories)
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = TrackingUiState()
            )

    fun updateSelectedDate(date: Long) {
        _selectedDate.value = date

    }

    fun trackNewMeal(meal: Meal) {
        viewModelScope.launch {
            mealsRepository.insertTrackedMeal(meal.id, selectedDate.value)
        }
    }

    fun removeTrackedMeal(entry: TrackedMealEntry) {
        viewModelScope.launch {
            mealsRepository.deleteTrackedMeal(entry.trackId, entry.meal.id, entry.dateConsumed)
        }
    }
    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    private fun isSameDay(timestamp1: Long, timestamp2: Long): Boolean {
        val zoneId = ZoneId.systemDefault()
        val date1 = Instant.ofEpochMilli(timestamp1).atZone(zoneId).toLocalDate()
        val date2 = Instant.ofEpochMilli(timestamp2).atZone(zoneId).toLocalDate()
        return date1 == date2
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L

    }
}

data class TrackingUiState(
    val mealList: List<TrackedMealEntry> = listOf(),
    val totalCalories: Int = 0
)