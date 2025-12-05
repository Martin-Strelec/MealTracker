package com.example.mealtracker.data

import kotlinx.coroutines.flow.Flow

class OfflineMealsRepository(private val mealDao: MealsDao) : MealsRepository {
    // Get meals ordered by date
    override fun getMealsOrderedByDateStream(): Flow<List<Meal>> = mealDao.getMealsOrderedByDate()
    // Get meals ordered by name
    override fun getMealsOrderedByNameStream(): Flow<List<Meal>> = mealDao.getMealsOrderedByName()
    // Get favourite meals
    override fun getFavouriteMeals(): Flow<List<Meal>> = mealDao.getFavouriteMeals()
    // Get tracked meals
    override fun getAllTrackedMeals(): Flow<List<TrackedMealEntry>> = mealDao.getAllTrackedMeals()
    // Get single meal
    override fun getMealStream(id: Int): Flow<Meal?> = mealDao.getMeal(id)
    // Add meal to tracked
    override suspend fun insertTrackedMeal (mealId: Int, date: Long) {
        mealDao.insertTrackedMeal(TrackedMeal(mealId = mealId, dateConsumed = date))
    }
    // Remove meal from tracked
    override suspend fun deleteTrackedMeal (trackId: Int, mealId: Int, date: Long) {
        mealDao.deleteTrackedMeal(TrackedMeal(id = trackId, mealId = mealId, dateConsumed = date))
    }
    // Update or insert meal
    override suspend fun upsertMeal(meal: Meal) = mealDao.upsertMeal(meal)
    // Delete meal
    override suspend fun deleteMeal(meal: Meal) = mealDao.deleteMeal(meal)


}