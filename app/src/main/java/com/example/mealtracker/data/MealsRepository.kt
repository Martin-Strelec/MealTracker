package com.example.mealtracker.data

import kotlinx.coroutines.flow.Flow

interface MealsRepository {

    fun getMealsOrderedByDate(): Flow<List<Meal>>

    fun getMealsOrderedByName(): Flow<List<Meal>>

    fun getItemStream(id: Int): Flow<Meal?>

    suspend fun upsertMeal(meal: Meal)

    suspend fun deleteMeal(meal: Meal)

}