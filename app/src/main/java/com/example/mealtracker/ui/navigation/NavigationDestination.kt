package com.example.mealtracker.ui.navigation

import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Interface describing a navigation destination in the app.
 * Used to ensure type safety and consistency for routes, titles, and drawer icons.
 */
interface NavigationDestination {
    /**
     * Unique string defining the path to this screen (e.g., "home", "add_meal").
     */
    val route: String

    /**
     * Resource ID for the screen title displayed in the TopAppBar or Drawer.
     */
    val titleRes: Int

    /**
     * Icon vector displayed in the Navigation Drawer.
     */
    val icon: ImageVector

    /**
     * Flag indicating if this destination should appear in the main Navigation Drawer menu.
     * (e.g., Home is true, AddMeal is false).
     */
    val showInDrawer: Boolean
}