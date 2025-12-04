package com.example.mealtracker

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.mealtracker.data.Meal
import com.example.mealtracker.data.MealDatabase
import com.example.mealtracker.data.MealsDao
import com.example.mealtracker.data.TrackedMeal
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class MealsDaoTest {
    private lateinit var mealsDao: MealsDao
    private lateinit var mealDatabase: MealDatabase

    private val meal1 = Meal (
        name = "Apple",
        image = "",
        description = "A red Fruit",
        calories = 95,
        dateAdded = System.currentTimeMillis(),
        isFavourite = false,
        isTracked = false
    )

    private var meal2 = Meal (
        name = "Banana",
        image = "",
        description = "A yellow fruit",
        calories = 105,
        dateAdded = 123456789L,
        isFavourite = true,
        isTracked = false
    )

    @Before
    fun createDb() {
        val context: Context = ApplicationProvider.getApplicationContext()
        mealDatabase = Room.inMemoryDatabaseBuilder(context, MealDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        mealsDao = mealDatabase.mealDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        mealDatabase.close()
    }

    @Test
    @Throws(Exception::class)
    fun daoInsertMeal() = runBlocking {
        mealsDao.upsertMeal(meal1)
        val allMeals = mealsDao.getMealsOrderedByDate().first()

        assertEquals(1, allMeals.size)
        assertEquals(1, allMeals.first().id)
        assertEquals(meal1.name, allMeals.first().name)
        assertEquals(meal1.description, allMeals.first().description)
        assertEquals(meal1.calories, allMeals.first().calories)
    }

    @Test
    @Throws(Exception::class)
    fun daoUpdateMeal() = runBlocking{
        mealsDao.upsertMeal(meal1)

        val originalMeal = mealsDao.getMealsOrderedByName().first().first()

        val updatedMeal = originalMeal.copy(
            name = "Updated Apple",
            calories = 150,
            description = "Updated description")

        mealsDao.upsertMeal(updatedMeal)

        val fetchedMeal = mealsDao.getMeal(originalMeal.id).first()

        assertEquals(updatedMeal.name, fetchedMeal?.name)
        assertEquals(updatedMeal.calories, fetchedMeal?.calories)
        assertEquals(updatedMeal.description, fetchedMeal?.description)
    }

    @Test
    @Throws(Exception::class)
    fun daoDeleteMeal() = runBlocking {
        mealsDao.upsertMeal(meal1)

        val itemToDelete = mealsDao.getMealsOrderedByDate().first().first()

        mealsDao.deleteMeal(itemToDelete)
        val allMeals = mealsDao.getMealsOrderedByDate().first()
        assertEquals(0, allMeals.size)
    }

    @Test
    @Throws(Exception::class)
    fun daoGetMeal() = runBlocking {
        mealsDao.upsertMeal(meal1)

        val savedMeal = mealsDao.getMealsOrderedByDate().first().first()

        val fetchedMeal = mealsDao.getMeal(savedMeal.id).first()

        assertEquals(savedMeal, fetchedMeal)
    }

    @Test
    @Throws(Exception::class)
    fun daoGetAllMeals() = runBlocking {
        mealsDao.upsertMeal(meal1)
        mealsDao.upsertMeal(meal2)

        val allMeals = mealsDao.getMealsOrderedByDate().first()
        assertEquals(2, allMeals.size)
    }




    @Test
    @Throws(Exception::class)
    fun daoGetAllFavouriteMeals() = runBlocking {
        mealsDao.upsertMeal(meal1)

        val savedMeal = mealsDao.getMealsOrderedByDate().first().first()

        val favouriteMeal = savedMeal.copy(isFavourite = true)

        mealsDao.upsertMeal(favouriteMeal)

        val favouriteMeals = mealsDao.getFavouriteMeals().first()
        assertEquals(1, favouriteMeals.size)
    }

    @Test
    @Throws(Exception::class)
    fun daoTrackAndUntrackMeal() = runBlocking {
        mealsDao.upsertMeal(meal1)

        val savedMeal = mealsDao.getMealsOrderedByDate().first().first()

        val trackedMeal = TrackedMeal(
            mealId = savedMeal.id,
            dateConsumed = System.currentTimeMillis()
        )
        mealsDao.insertTrackedMeal(trackedMeal)

        val trackedList = mealsDao.getAllTrackedMeals().first()

        assertEquals(1, trackedList.size)
        assertEquals(savedMeal, trackedList[0].meal)

        val entryToDelete = trackedList[0]

        val trackedMealToRemove = TrackedMeal(
            id = entryToDelete.trackId,
            mealId = entryToDelete.meal.id,
            dateConsumed = entryToDelete.dateConsumed
        )

        mealsDao.deleteTrackedMeal(trackedMealToRemove)

        val emptyList = mealsDao.getAllTrackedMeals().first()
        assertEquals(0, emptyList.size)

        mealsDao.deleteTrackedMeal(trackedMeal)
    }
}