package io.github.nfdz.cryptool.screens.main

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import io.github.nfdz.cryptool.R
import io.github.nfdz.cryptool.common.utils.PreferencesHelper
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@LargeTest
@RunWith(androidx.test.ext.junit.runners.AndroidJUnit4::class)
class HashInstrumentedTest {

    companion object {
        private const val DUMMY_ORIGIN = "hello world"
        private const val DUMMY_HASH = "b94d27b9934d3e08a52e52d7da7dabfac484efe37a5380ee9088f7ace2efcde9"
    }

    @Before
    fun setup() {
        val context = InstrumentationRegistry.getInstrumentation().context
        PreferencesHelper(context).clearAllSync()
    }

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    /** Hash dummy text */
    @Test
    fun hash_text() {
        closeWelcome()
        clickNavigationOption(R.id.main_nav_hash)
        typeInputHash(DUMMY_ORIGIN)
        checkOutputText(DUMMY_HASH)
    }

    private fun typeInputHash(text: String) {
        val inputHash = getInputHash()
        inputHash.perform(ViewActions.replaceText(text), ViewActions.closeSoftKeyboard())
    }

    private fun getInputHash() = Espresso.onView(
        Matchers.allOf(
            ViewMatchers.withId(R.id.itb_et), childAtPosition(
                childAtPosition(ViewMatchers.withId(R.id.hash_itb_origin), 0), 3
            ), ViewMatchers.isDisplayed()
        )
    )

    private fun checkOutputText(text: String) {
        val output = getOutput()
        output.check(ViewAssertions.matches(ViewMatchers.withText(text)))
    }

    private fun getOutput() = Espresso.onView(
        Matchers.allOf(
            ViewMatchers.withId(R.id.otb_tv),
            childAtPosition(childAtPosition(ViewMatchers.withId(R.id.hash_otb_processed), 0), 3),
            ViewMatchers.isDisplayed()
        )
    )

}