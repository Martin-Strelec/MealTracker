package com.example.mealtracker

import android.Manifest
import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.testing.TestNavHostController
import androidx.test.rule.GrantPermissionRule
import com.example.mealtracker.ui.navigation.MealTrackerNavHost
import com.example.mealtracker.ui.theme.AppTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * UI Integration Test for MealTracker Navigation.
 * Verifies that the app starts on the correct screen and that the Navigation Drawer works.
 */
class MealTrackerNavigationTest {

    // Rule to access the Compose content and perform UI actions
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    // Rule to automatically grant Camera permission, preventing dialogs from blocking tests
    @get:Rule
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.CAMERA
    )

    private lateinit var navController: NavHostController

    /**
     * Sets up the test environment by loading the main App Composable.
     */
    @Before
    fun setupMealTrackerNavHost() {
        composeTestRule.setContent {
            navController = rememberNavController()
            AppTheme {
                MealTrackerApp(navController = navController)
            }
        }
    }

    /**
     * Test: Verifies that the app launches directly to the Home screen.
     * Checks for the presence of the "Meals" title (which is non-clickable).
     */
    @Test
    fun mealTracker_verifyStartDestination() {
        composeTestRule
            .onNode(
                hasText("Meals")
                        and !hasClickAction()) // Distinguishes Title from Drawer Item
            .assertIsDisplayed()
    }

    /**
     * Test: Verifies navigation via the Drawer Menu.
     * 1. Opens the Drawer.
     * 2. Clicks on "Tracking".
     * 3. Verifies "Tracking" screen is displayed.
     * 4. Repeats for "Favourites" and then back to "Tracking".
     */
    @Test
    fun mealTracker_verifyDrawerNavigation() {
        // Open Drawer
        composeTestRule
            .onNodeWithContentDescription("Menu")
            .performClick()

        // Navigate to Tracking
        composeTestRule
            .onNode(
                hasText("Tracking")
                        and hasClickAction())
            .performClick()

        // Check Tracking Screen Title
        composeTestRule
            .onNode(
                hasText("Tracking")
                        and !hasClickAction())
            .assertIsDisplayed()

        // Open Drawer again
        composeTestRule
            .onNodeWithContentDescription("Menu")
            .performClick()

        // Navigate to Favourites
        composeTestRule
            .onNode(
                hasText("Favourites")
                        and hasClickAction())
            .performClick()

        // Check Favourites Screen Title
        composeTestRule
            .onNode(
                hasText("Favourites")
                        and !hasClickAction())
            .assertIsDisplayed()

        // Open Drawer again
        composeTestRule
            .onNodeWithContentDescription("Menu")
            .performClick()

        // Navigate back to Tracking
        composeTestRule
            .onNode(
                hasText("Tracking")
                        and hasClickAction())
            .performClick()

        // Check Tracking Screen Title
        composeTestRule
            .onNode(
                hasText("Tracking")
                        and !hasClickAction())
            .assertIsDisplayed()
    }
}