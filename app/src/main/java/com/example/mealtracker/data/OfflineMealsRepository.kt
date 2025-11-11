package com.example.mealtracker.data

import kotlinx.coroutines.flow.Flow

class OfflineMealsRepository(private val mealDao: MealsDao) : MealsRepository {
    override fun getMealsOrderedByDateStream(): Flow<List<Meal>> = mealDao.getMealsOrderedByDate()

    override fun getMealsOrderedByNameStream(): Flow<List<Meal>> = mealDao.getMealsOrderedByName()

    override fun getMealStream(id: Int): Flow<Meal?> = mealDao.getMeal(id)

    override suspend fun upsertMeal(meal: Meal) = mealDao.upsertMeal(meal)

    override suspend fun deleteMeal(meal: Meal) = mealDao.deleteMeal(meal)


}