package com.example.mealtracker.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface MealsDao {
    // Insert and Update query
    @Upsert
    suspend fun upsertMeal(meal: Meal)
    // Delete Query
    @Delete
    suspend fun deleteMeal(meal: Meal)
    // Get query ordered by date added
    @Query("SELECT * FROM meal ORDER BY dateAdded")
    fun getMealsOrderedByDate(): Flow<List<Meal>>
    // Get query ordered by name
    @Query("SELECT * FROM meal ORDER BY name")
    fun getMealsOrderedByName(): Flow<List<Meal>>
    // Get query for single Meal
    @Query("SELECT * FROM meal WHERE id = :id")
    fun getMeal(id: Int): Flow<Meal?>
    //Get query for favourite meals
    @Query("SELECT * FROM meal WHERE isFavourite = 1 ORDER BY name ASC")
    fun getFavouriteMeals(): Flow<List<Meal>>
    //Insert query for tracking Meals
    @Insert
    suspend fun insertTrackedMeal(trackedMeal: TrackedMeal)
    //Delete query for removing from tracked
    @Delete
    suspend fun deleteTrackedMeal(trackedMeal: TrackedMeal)
    // Get query for all tracked meals
    @Query("""
        SELECT meal.*, tracked_meals.id AS trackId, tracked_meals.dateConsumed AS dateConsumed
        FROM meal
        INNER JOIN tracked_meals ON meal.id = tracked_meals.mealId
        ORDER BY tracked_meals.dateConsumed DESC
    """)
    fun getAllTrackedMeals(): Flow<List<TrackedMealEntry>>
}