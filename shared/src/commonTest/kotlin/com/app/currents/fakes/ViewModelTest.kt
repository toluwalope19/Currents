package com.app.currents.fakes

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

/**
 * Runs [block] with `Dispatchers.Main` swapped for a [kotlinx.coroutines.test.TestDispatcher] so
 * ViewModels (which launch coroutines via `viewModelScope`, backed by `Dispatchers.Main.immediate`)
 * can be driven deterministically with `advanceUntilIdle()`.
 *
 * `resetMain()` must run only *after* `runTest` has fully returned — `runTest` cancels/joins
 * `backgroundScope` jobs as part of its own teardown, and those jobs can still touch
 * `Dispatchers.Main` (e.g. a StateFlow subscription-count callback) while unwinding. Resetting
 * Main from inside the test body (before that teardown happens) races with it.
 */
fun runViewModelTest(block: suspend TestScope.() -> Unit): TestResult {
    val dispatcher = StandardTestDispatcher()
    Dispatchers.setMain(dispatcher)
    try {
        return runTest(dispatcher, testBody = block)
    } finally {
        Dispatchers.resetMain()
    }
}
