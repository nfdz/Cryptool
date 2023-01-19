package io.github.nfdz.cryptool.shared.test

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*

@OptIn(ExperimentalCoroutinesApi::class)
fun runCoroutineTest(
    testBody: suspend TestScope.() -> Unit
): TestResult {
    return runTest(
        context = UnconfinedTestDispatcher(),
        dispatchTimeoutMs = 20_000L,
        testBody = testBody,
    )
}