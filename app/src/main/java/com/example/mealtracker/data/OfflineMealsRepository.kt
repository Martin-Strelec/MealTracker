package com.example.mealtracker.data

import kotlinx.coroutines.flow.Flow

class OfflineMealsRepository(private val mealDao: MealsDao) : MealsRepository {
    override fun getMealsOrderedByDateStream(): Flow<List<Meal>> = mealDao.getMealsOrderedByDate()

    override fun getMealsOrderedByNameStream(): Flow<List<Meal>> = mealDao.getMealsOrderedByName()

    override fun getFavouriteMeals(): Flow<List<Meal>> = mealDao.getFavouriteMeals()

    override fun getAllTrackedMeals(): Flow<List<TrackedMealEntry>> = mealDao.getAllTrackedMeals()

    override fun getMealStream(id: Int): Flow<Meal?> = mealDao.getMeal(id)

    override suspend fun insertTrackedMeal (mealId: Int, date: Long) {
        mealDao.insertTrackedMeal(TrackedMeal(mealId = mealId, dateConsumed = date))
    }

    override suspend fun deleteTrackedMeal (trackId: Int, mealId: Int, date: Long) {
        mealDao.deleteTrackedMeal(TrackedMeal(id = trackId, mealId = mealId, dateConsumed = date))
    }

    override suspend fun upsertMeal(meal: Meal) = mealDao.upsertMeal(meal)

    override suspend fun deleteMeal(meal: Meal) = mealDao.deleteMeal(meal)


}