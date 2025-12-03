package com.example.mealtracker.ui.tracked

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mealtracker.data.Meal
import com.example.mealtracker.data.MealsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.Instant
import java.time.ZoneId

@RequiresApi(Build.VERSION_CODES.O)
class TrackingViewModel(private val mealsRepository: MealsRepository) : ViewModel() {

    private val _selectedDate = MutableStateFlow<Long>(System.currentTimeMillis())
    val selectedDate = _selectedDate.asStateFlow()

    val trackingUiState: StateFlow<TrackingUiState> =
        combine(mealsRepository.getTrackedMeals(), _selectedDate) { meals, date ->
            val filteredMeals = meals.filter {isSameDay(it.dateAdded, date) }
            TrackingUiState(filteredMeals)
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = TrackingUiState()
            )

    fun updateSelectedDate(date: Long) {
        _selectedDate.value = date

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

data class TrackingUiState(val mealList: List<Meal> = listOf())