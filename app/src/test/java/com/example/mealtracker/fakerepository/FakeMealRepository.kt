package com.example.mealtracker.fakerepository

import com.example.mealtracker.data.Meal
import com.example.mealtracker.data.MealsRepository
import com.example.mealtracker.data.TrackedMealEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


// Add this to your test/java/.../FakeMealsRepository.kt or inside the test file
class FakeMealsRepository : MealsRepository {
    private val meals = mutableListOf<Meal>()

    // Helper for testing
    fun addMeal(meal: Meal) {
        meals.add(meal)
    }

    override fun getMealStream(id: Int): Flow<Meal?> = flow {
        emit(meals.find { it.id == id })
    }

    override suspend fun upsertMeal(meal: Meal) {
        // Remove existing meal with same ID if it exists (simulate update)
        val index = meals.indexOfFirst { it.id == meal.id }
        if (index != -1) {
            meals[index] = meal
        } else {
            meals.add(meal)
        }
    }

    // ... Implement other members with empty bodies or TODO() if not used in this test ...
    override fun getMealsOrderedByDateStream(): Flow<List<Meal>> = flow {}
    override fun getMealsOrderedByNameStream(): Flow<List<Meal>> = flow {}
    override fun getAllTrackedMeals(): Flow<List<TrackedMealEntry>> = flow {}
    override fun getFavouriteMeals(): Flow<List<Meal>> = flow {}
    override suspend fun insertTrackedMeal(mealId: Int, date: Long) {}
    override suspend fun deleteTrackedMeal(trackId: Int, mealId: Int, date: Long) {}
    override suspend fun deleteMeal(meal: Meal) {}
}