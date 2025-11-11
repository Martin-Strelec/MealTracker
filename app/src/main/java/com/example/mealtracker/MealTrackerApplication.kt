package com.example.mealtracker

import android.app.Application
import com.example.mealtracker.data.MealDatabase

class MealTrackerApplication : Application() {

    val database: MealDatabase by lazy { MealDatabase.getDatabase(this) }

}