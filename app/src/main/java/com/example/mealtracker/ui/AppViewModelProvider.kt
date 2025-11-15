package com.example.mealtracker.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import com.example.mealtracker.ui.meal.EditMealViewModel
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.mealtracker.MealTrackerApplication
import com.example.mealtracker.ui.home.HomeViewModel
import com.example.mealtracker.ui.meal.AddMealViewModel


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
        initializer {
            AddMealViewModel(MealTrackerApplication().container.mealsRepository)
        }
    }
}

/**
 * Extension function to queries for [Application] object and returns an instance of
 * [MealTrackerApplication].
 */
fun CreationExtras.MealTrackerApplication(): MealTrackerApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as MealTrackerApplication)