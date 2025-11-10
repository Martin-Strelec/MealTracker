package com.example.mealtracker.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MealDAO {
    @Insert
    fun insertMeal(meal: Meal)

    @Query("SELECT * FROM meals ORDER BY name")
    fun getAllMeals(): Flow<List<Meal>>
}