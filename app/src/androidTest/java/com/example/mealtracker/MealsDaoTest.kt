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

/**
 * Instrumentation tests for the [MealsDao].
 * Uses an in-memory Room database to verify database operations on an Android device/emulator.
 */
@RunWith(AndroidJUnit4::class)
class MealsDaoTest {
    private lateinit var mealsDao: MealsDao
    private lateinit var mealDatabase: MealDatabase

    // Sample data for testing
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

    /**
     * Sets up the database before each test.
     * Uses [Room.inMemoryDatabaseBuilder] so data is not persisted on disk.
     */
    @Before
    fun createDb() {
        val context: Context = ApplicationProvider.getApplicationContext()
        mealDatabase = Room.inMemoryDatabaseBuilder(context, MealDatabase::class.java)
            .allowMainThreadQueries() // Allow queries on main thread for testing simplicity
            .build()
        mealsDao = mealDatabase.mealDao()
    }

    /**
     * Closes the database after each test to free resources.
     */
    @After
    @Throws(IOException::class)
    fun closeDb() {
        mealDatabase.close()
    }

    /**
     * Test: Verifies inserting a meal and retrieving it.
     */
    @Test
    @Throws(Exception::class)
    fun daoInsertMeal() = runBlocking {
        // Act: Insert a meal
        mealsDao.upsertMeal(meal1)

        // Assert: Retrieve the list and check if the meal exists with correct data
        val allMeals = mealsDao.getMealsOrderedByDate().first()

        assertEquals(1, allMeals.size)
        assertEquals(1, allMeals.first().id) // ID should be auto-generated starting at 1
        assertEquals(meal1.name, allMeals.first().name)
        assertEquals(meal1.description, allMeals.first().description)
        assertEquals(meal1.calories, allMeals.first().calories)
    }

    /**
     * Test: Verifies updating an existing meal.
     */
    @Test
    @Throws(Exception::class)
    fun daoUpdateMeal() = runBlocking{
        // Arrange: Insert initial meal
        mealsDao.upsertMeal(meal1)
        val originalMeal = mealsDao.getMealsOrderedByName().first().first()

        // Act: Create a copy with modified values and upsert it
        val updatedMeal = originalMeal.copy(
            name = "Updated Apple",
            calories = 150,
            description = "Updated description")

        mealsDao.upsertMeal(updatedMeal)

        // Assert: Fetch the meal again and verify changes are reflected
        val fetchedMeal = mealsDao.getMeal(originalMeal.id).first()

        assertEquals(updatedMeal.name, fetchedMeal?.name)
        assertEquals(updatedMeal.calories, fetchedMeal?.calories)
        assertEquals(updatedMeal.description, fetchedMeal?.description)
    }

    /**
     * Test: Verifies deleting a meal.
     */
    @Test
    @Throws(Exception::class)
    fun daoDeleteMeal() = runBlocking {
        // Arrange: Insert a meal
        mealsDao.upsertMeal(meal1)
        val itemToDelete = mealsDao.getMealsOrderedByDate().first().first()

        // Act: Delete the meal
        mealsDao.deleteMeal(itemToDelete)

        // Assert: List should be empty
        val allMeals = mealsDao.getMealsOrderedByDate().first()
        assertEquals(0, allMeals.size)
    }

    /**
     * Test: Verifies retrieving a single meal by ID.
     */
    @Test
    @Throws(Exception::class)
    fun daoGetMeal() = runBlocking {
        mealsDao.upsertMeal(meal1)
        val savedMeal = mealsDao.getMealsOrderedByDate().first().first()

        val fetchedMeal = mealsDao.getMeal(savedMeal.id).first()

        assertEquals(savedMeal, fetchedMeal)
    }

    /**
     * Test: Verifies retrieving all inserted meals.
     */
    @Test
    @Throws(Exception::class)
    fun daoGetAllMeals() = runBlocking {
        mealsDao.upsertMeal(meal1)
        mealsDao.upsertMeal(meal2)

        val allMeals = mealsDao.getMealsOrderedByDate().first()
        assertEquals(2, allMeals.size)
    }

    /**
     * Test: Verifies retrieving only meals marked as favourite.
     */
    @Test
    @Throws(Exception::class)
    fun daoGetAllFavouriteMeals() = runBlocking {
        // Arrange: Insert one normal meal
        mealsDao.upsertMeal(meal1)
        val savedMeal = mealsDao.getMealsOrderedByDate().first().first()

        // Act: Mark it as favourite and update
        val favouriteMeal = savedMeal.copy(isFavourite = true)
        mealsDao.upsertMeal(favouriteMeal)

        // Assert: Filtered query should return it
        val favouriteMeals = mealsDao.getFavouriteMeals().first()
        assertEquals(1, favouriteMeals.size)
    }

    /**
     * Test: Verifies the foreign-key relationship functionality for Tracking.
     * Inserts a Tracking entry linked to a Meal, verifies it exists, then deletes it.
     */
    @Test
    @Throws(Exception::class)
    fun daoTrackAndUntrackMeal() = runBlocking {
        // Arrange: Insert the base meal
        mealsDao.upsertMeal(meal1)
        val savedMeal = mealsDao.getMealsOrderedByDate().first().first()

        // Act 1: Insert a tracking entry linked to that meal
        val trackedMeal = TrackedMeal(
            mealId = savedMeal.id,
            dateConsumed = System.currentTimeMillis()
        )
        mealsDao.insertTrackedMeal(trackedMeal)

        // Assert 1: Verify the joined query returns the meal
        val trackedList = mealsDao.getAllTrackedMeals().first()
        assertEquals(1, trackedList.size)
        assertEquals(savedMeal, trackedList[0].meal)

        // Act 2: Delete the tracking entry
        val entryToDelete = trackedList[0]
        val trackedMealToRemove = TrackedMeal(
            id = entryToDelete.trackId,
            mealId = entryToDelete.meal.id,
            dateConsumed = entryToDelete.dateConsumed
        )
        mealsDao.deleteTrackedMeal(trackedMealToRemove)

        // Assert 2: Tracking list should be empty
        val emptyList = mealsDao.getAllTrackedMeals().first()
        assertEquals(0, emptyList.size)
    }
}