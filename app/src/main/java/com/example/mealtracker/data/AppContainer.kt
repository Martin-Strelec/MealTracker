package com.example.mealtracker.data

import android.content.Context

/**
 * App container for Dependency injection.
 */
interface AppContainer {
    val mealsRepository: MealsRepository
}

/**
 * [AppContainer] implementation that provides instance of [OfflineMealsRepository]
 */
class AppDataContainer(private val context: Context) : AppContainer {
    /**
     * Implementation for [MealsRepository]
     */
    override val mealsRepository: MealsRepository by lazy {
        OfflineMealsRepository(MealDatabase.getDatabase(context).mealDao())
    }
}