package com.example.mealtracker.rules

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * A JUnit Rule that swaps the [Dispatchers.Main] with a [TestDispatcher].
 *
 * Unit tests run on the JVM, which does not have an Android Main thread.
 * Any ViewModel that launches coroutines on `viewModelScope` (which defaults to Main)
 * will crash without this rule.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class TestDispatcherRule(
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
) : TestWatcher() {

    /**
     * Called before the test starts. Sets the Main dispatcher to the test dispatcher.
     */
    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    /**
     * Called after the test finishes. Resets the Main dispatcher to the original.
     */
    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}