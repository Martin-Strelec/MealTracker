package com.example.mealtracker.data

import kotlinx.coroutines.flow.Flow

interface MealsRepository {

    // Get meals ordered by date
    fun getMealsOrderedByDateStream(): Flow<List<Meal>>
    // Get meals ordered by name
    fun getMealsOrderedByNameStream(): Flow<List<Meal>>
    // Get all tracked meals
    fun getAllTrackedMeals(): Flow<List<TrackedMealEntry>>
    // Get favourite meals
    fun getFavouriteMeals(): Flow<List<Meal>>
    // Get single meal
    fun getMealStream(id: Int): Flow<Meal?>
    // Add meal to tracked
    suspend fun insertTrackedMeal(mealId: Int, date: Long)
    // Remove meal from tracked
    suspend fun deleteTrackedMeal(trackId: Int, mealId: Int, date: Long)
    // Update or insert meal
    suspend fun upsertMeal(meal: Meal)
    // Delete meal
    suspend fun deleteMeal(meal: Meal)

}