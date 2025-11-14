package com.example.mealtracker.ui

import androidx.sqlite.db.SupportSQLiteOpenHelper
import com.example.mealtracker.ui.meal.EditMealViewModel
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.mealtracker.ui.home.HomeViewModel


object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            EditMealViewModel(
                this.createSavedStateHandle()
            )
        }
        initializer {
            HomeViewModel()
        }
    }
}