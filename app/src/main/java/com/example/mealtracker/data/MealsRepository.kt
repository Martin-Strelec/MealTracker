package com.example.mealtracker.data

import kotlinx.coroutines.flow.Flow

interface MealsRepository {

    fun getMealsOrderedByDateStream(): Flow<List<Meal>>

    fun getMealsOrderedByNameStream(): Flow<List<Meal>>

    fun getMealStream(id: Int): Flow<Meal?>

    suspend fun upsertMeal(meal: Meal)

    suspend fun deleteMeal(meal: Meal)

}