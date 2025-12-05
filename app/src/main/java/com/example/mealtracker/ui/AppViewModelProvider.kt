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

/**
 * Provides the [ViewModelProvider.Factory] for the entire application.
 *
 * This object is responsible for defining how to create instances of the various ViewModels
 * used in the app. It uses the Service Locator pattern to inject dependencies (like the Repository)
 * from the [MealTrackerApplication] container into the ViewModels.
 */
object AppViewModelProvider {

    /**
     * A [ViewModelProvider.Factory] that defines how to create each ViewModel class.
     * It uses the [initializer] DSL to map ViewModel classes to their constructor calls.
     */
    val Factory = viewModelFactory {

        // Initializer for MealEditViewModel.
        // Requires 'savedStateHandle' to retrieve navigation arguments (meal ID)
        // and 'mealsRepository' to fetch/update data.
        initializer {
            MealEditViewModel(
                this.createSavedStateHandle(),
                mealTrackerApplication().container.mealsRepository
            )
        }

        // Initializer for MealDetailsViewModel.
        // Also requires 'savedStateHandle' for the meal ID and the repository.
        initializer {
            MealDetailsViewModel(
                this.createSavedStateHandle(),
                mealTrackerApplication().container.mealsRepository
            )
        }

        // Initializer for HomeViewModel.
        // Depends on the repository to stream the list of meals.
        initializer {
            HomeViewModel(mealTrackerApplication().container.mealsRepository)
        }

        // Initializer for FavouritesViewModel.
        // Depends on the repository to filter and show favourite meals.
        initializer {
            FavouritesViewModel(mealTrackerApplication().container.mealsRepository)
        }

        // Initializer for TrackingViewModel.
        // Depends on the repository to manage daily tracking logs.
        initializer {
            TrackingViewModel(mealTrackerApplication().container.mealsRepository)
        }

        // Initializer for AddMealViewModel.
        // Depends on the repository to insert new meals.
        initializer {
            AddMealViewModel(mealTrackerApplication().container.mealsRepository)
        }

        // Initializer for CameraViewModel.
        // Does not have data dependencies, only manages UI state for permissions.
        initializer {
            CameraViewModel()
        }
    }
}

/**
 * Extension function to retrieve the [MealTrackerApplication] instance from [CreationExtras].
 * * [CreationExtras] provides a way to access the Application context within the ViewModel factory,
 * allowing us to reach the AppContainer where the repository singleton lives.
 */
fun CreationExtras.mealTrackerApplication(): MealTrackerApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as MealTrackerApplication)