package com.example.mealtracker.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface MealsDao {
    @Upsert
    suspend fun upsertMeal(meal: Meal)

    @Delete
    suspend fun deleteMeal(meal: Meal)

    @Query("SELECT * FROM meal ORDER BY dateAdded")
    fun getMealsOrderedByDate(): Flow<List<Meal>>

    @Query("SELECT * FROM meal ORDER BY name")
    fun getMealsOrderedByName(): Flow<List<Meal>>

    @Query("SELECT * FROM meal WHERE id = :id")
    fun getMeal(id: Int): Flow<Meal?>
}