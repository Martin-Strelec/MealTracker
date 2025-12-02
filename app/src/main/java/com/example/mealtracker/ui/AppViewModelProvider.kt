package com.example.mealtracker.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import com.example.mealtracker.ui.meal.MealEditViewModel
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.mealtracker.MealTrackerApplication
import com.example.mealtracker.ui.camera.CameraViewModel
import com.example.mealtracker.ui.favourites.FavouritesViewModel
import com.example.mealtracker.ui.home.HomeViewModel
import com.example.mealtracker.ui.meal.AddMealViewModel
import com.example.mealtracker.ui.meal.MealDetailsViewModel
import com.example.mealtracker.ui.tracked.TrackingViewModel


object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            MealEditViewModel(
                this.createSavedStateHandle(),
                mealTrackerApplication().container.mealsRepository
            )
        }
        initializer {
            MealDetailsViewModel(
                this.createSavedStateHandle(),
                mealTrackerApplication().container.mealsRepository
            )
        }
        initializer {
            HomeViewModel(mealTrackerApplication().container.mealsRepository)
        }
        initializer {
            FavouritesViewModel(mealTrackerApplication().container.mealsRepository)
        }
        initializer {
            TrackingViewModel(mealTrackerApplication().container.mealsRepository)
        }
        initializer {
            AddMealViewModel(mealTrackerApplication().container.mealsRepository)
        }
        initializer {
            CameraViewModel()
        }
    }
}

/**
 * Extension function to queries for [MealTrackerApplication] object and returns an instance of
 * [MealTrackerApplication].
 */
fun CreationExtras.mealTrackerApplication(): MealTrackerApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as MealTrackerApplication)