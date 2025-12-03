package com.example.mealtracker.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
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

    @Query("SELECT * FROM meal WHERE isFavourite = 1 ORDER BY name ASC")
    fun getFavouriteMeals(): Flow<List<Meal>>

    @Insert
    suspend fun insertTrackedMeal(trackedMeal: TrackedMeal)

    @Delete
    suspend fun deleteTrackedMeal(trackedMeal: TrackedMeal)

    @Query("""
        SELECT meal.*, tracked_meals.id AS trackId, tracked_meals.dateConsumed AS dateConsumed
        FROM meal
        INNER JOIN tracked_meals ON meal.id = tracked_meals.mealId
        ORDER BY tracked_meals.dateConsumed DESC
    """)
    fun getAllTrackedMeals(): Flow<List<TrackedMealEntry>>
}