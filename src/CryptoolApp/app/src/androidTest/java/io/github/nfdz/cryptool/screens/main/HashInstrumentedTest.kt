package io.github.nfdz.cryptool.screens.main

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import io.github.nfdz.cryptool.R
import org.hamcrest.Matchers.allOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@LargeTest
@RunWith(androidx.test.ext.junit.runners.AndroidJUnit4::class)
class HashInstrumentedTest {

    companion object {
        private const val DUMMY_ORIGIN = "hello world"
        private const val DUMMY_HASH =
            "b94d27b9934d3e08a52e52d7da7dabfac484efe37a5380ee9088f7ace2efcde9"
    }

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    /** Hash dummy text */
    @Test
    fun hash_text() {
        cleanStoredData(mActivityTestRule.activity)
        closeWelcome()
        clickNavigationOption(R.id.main_nav_hash)
        typeInputHash(DUMMY_ORIGIN)
        checkOutputText(DUMMY_HASH)
    }

    private fun typeInputHash(text: String) {
        val inputHash = getInputHash()
        inputHash.perform(replaceText(text), closeSoftKeyboard())
    }

    private fun getInputHash() = Espresso.onView(
        allOf(
            withId(R.id.itb_et),
            childAtPosition(childAtPosition(withId(R.id.hash_itb_origin), 0), 3),
            isDisplayed()
        )
    )

    private fun checkOutputText(text: String) {
        val output = getOutput()
        output.check(matches(withText(text)))
    }

    private fun getOutput() = Espresso.onView(
        allOf(
            withId(R.id.otb_tv),
            childAtPosition(childAtPosition(withId(R.id.hash_otb_processed), 0), 3),
            isDisplayed()
        )
    )

}