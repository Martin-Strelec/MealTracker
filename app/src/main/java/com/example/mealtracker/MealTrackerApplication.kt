package com.example.mealtracker

import android.app.Application
import com.example.mealtracker.data.AppContainer
import com.example.mealtracker.data.AppDataContainer

class MealTrackerApplication : Application() {

    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}