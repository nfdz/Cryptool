package io.github.nfdz.cryptool.shared.test

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*

@OptIn(ExperimentalCoroutinesApi::class)
fun runCoroutineTest(
    testBody: suspend TestScope.() -> Unit
): TestResult {
    return runTest(
        context = StandardTestDispatcher(),
        dispatchTimeoutMs = 20_000L,
        testBody = testBody,
    )
}