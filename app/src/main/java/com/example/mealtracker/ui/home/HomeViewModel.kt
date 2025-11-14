package com.example.mealtracker.ui.home

import androidx.lifecycle.ViewModel
import com.example.mealtracker.data.Meal

class HomeViewModel() : ViewModel() {
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

data class HomeUiState(val mealList: List<Meal> = listOf())