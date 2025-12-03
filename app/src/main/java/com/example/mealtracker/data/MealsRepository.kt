package com.example.mealtracker.data

import kotlinx.coroutines.flow.Flow

interface MealsRepository {

    fun getMealsOrderedByDateStream(): Flow<List<Meal>>

    fun getMealsOrderedByNameStream(): Flow<List<Meal>>

    fun getAllTrackedMeals(): Flow<List<TrackedMealEntry>>

    fun getFavouriteMeals(): Flow<List<Meal>>

    fun getMealStream(id: Int): Flow<Meal?>
    suspend fun insertTrackedMeal(mealId: Int, date: Long)
    suspend fun deleteTrackedMeal(trackId: Int, mealId: Int, date: Long)
    suspend fun upsertMeal(meal: Meal)

    suspend fun deleteMeal(meal: Meal)

}