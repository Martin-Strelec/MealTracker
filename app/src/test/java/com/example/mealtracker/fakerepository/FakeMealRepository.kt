package com.example.mealtracker.fakerepository

import com.example.mealtracker.data.Meal
import com.example.mealtracker.data.MealsRepository
import com.example.mealtracker.data.TrackedMealEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * A "Fake" implementation of the repository for Unit Tests.
 * It uses a simple in-memory list to simulate a database, allowing tests to run quickly
 * without needing an Android emulator or a real Room database instance.
 */
class FakeMealsRepository : MealsRepository {

    // In-memory data store acting as the "database"
    private val meals = mutableListOf<Meal>()

    /**
     * Helper method strictly for tests to pre-populate the "database".
     */
    fun addMeal(meal: Meal) {
        meals.add(meal)
    }

    // Returns a Flow emitting the specific meal if found
    override fun getMealStream(id: Int): Flow<Meal?> = flow {
        emit(meals.find { it.id == id })
    }

    /**
     * Simulates the @Upsert behavior of Room.
     * If the ID exists, replace the item. If not, add it.
     */
    override suspend fun upsertMeal(meal: Meal) {
        val index = meals.indexOfFirst { it.id == meal.id }
        if (index != -1) {
            // Update existing
            meals[index] = meal
        } else {
            // Insert new
            meals.add(meal)
        }
    }

    // --- Unused methods for current ViewModel tests ---
    // These are implemented as empty flows or empty functions to satisfy the interface.

    override fun getMealsOrderedByDateStream(): Flow<List<Meal>> = flow {}
    override fun getMealsOrderedByNameStream(): Flow<List<Meal>> = flow {}
    override fun getAllTrackedMeals(): Flow<List<TrackedMealEntry>> = flow {}
    override fun getFavouriteMeals(): Flow<List<Meal>> = flow {}
    override suspend fun insertTrackedMeal(mealId: Int, date: Long) {}
    override suspend fun deleteTrackedMeal(trackId: Int, mealId: Int, date: Long) {}
    override suspend fun deleteMeal(meal: Meal) {}
}