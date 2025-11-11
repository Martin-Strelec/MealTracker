package com.example.mealtracker.data

import kotlinx.coroutines.flow.Flow

interface MealRepository {

    fun getAllMealsStream(): Flow<List<Meal>>

    suspend fun insertMeal(meal: Meal)

}