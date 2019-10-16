package io.github.nfdz.cryptool.screens.main

import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import io.github.nfdz.cryptool.R
import io.github.nfdz.cryptool.common.utils.ERROR_TEXT
import io.github.nfdz.cryptool.common.utils.PreferencesHelper
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(androidx.test.ext.junit.runners.AndroidJUnit4::class)
class CipherInstrumentedTest {

    companion object {
        const val DUMMY_PASS = "hello"
        const val DUMMY_ORIGIN = "world"
        const val DUMMY_PROCESSED = "0/YT6mWR8CvyOaCunsI6iA=="
    }

    @Before
    fun setup() {
        val context = InstrumentationRegistry.getInstrumentation().context
        PreferencesHelper(context).clearAllSync()
    }

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    /** Encrypt and decrypt text */
    @Test
    fun encrypt_decrypt() {
        closeWelcome()
        typeInputPass(DUMMY_PASS)
        typeInputOrigin(DUMMY_ORIGIN)
        checkOutputText(DUMMY_PROCESSED)
        clickReverse()
        checkOutputText(DUMMY_ORIGIN)
        clickReverse()
    }

    /** Try to decrypt a wrong encrypted text */
    @Test
    fun decrypt_error() {
        closeWelcome()
        clickReverse()
        typeInputPass(DUMMY_PASS)
        typeInputOrigin("not encrypted text")
        checkOutputText(ERROR_TEXT)
        clickReverse()
    }

    private fun clickReverse() = onView(withId(R.id.cipher_btn_reverse)).perform(click())

    private fun closeWelcome() {
        try {
            val closeWelcome = onView(
                allOf(
                    withId(android.R.id.button2),
                    withText("Close"),
                    childAtPosition(childAtPosition(withId(R.id.buttonPanel), 0), 2)
                )
            )
            closeWelcome.perform(scrollTo(), click())
        } catch (e: NoMatchingViewException) {
            // Swallow -> There is no welcome
        }
    }

    private fun typeInputPass(text: String) {
        val inputPass = getInputPass()
        inputPass.perform(replaceText(text), closeSoftKeyboard())
    }

    private fun getInputPass() = onView(
        allOf(
            withId(R.id.itb_et), childAtPosition(
                childAtPosition(withId(R.id.cipher_itb_pass), 0), 3
            ), isDisplayed()
        )
    )

    private fun typeInputOrigin(text: String) {
        val inputOrigin = getInputOrigin()
        inputOrigin.perform(replaceText(text), closeSoftKeyboard())
    }

    private fun getInputOrigin() = onView(
        allOf(
            withId(R.id.itb_et),
            childAtPosition(childAtPosition(withId(R.id.cipher_itb_origin), 0), 3),
            isDisplayed()
        )
    )

    private fun checkOutputText(text: String) {
        val output = getOutput()
        output.check(matches(withText(text)))
    }

    private fun getOutput() = onView(
        allOf(
            withId(R.id.otb_tv),
            childAtPosition(childAtPosition(withId(R.id.cipher_otb_processed), 0), 3),
            isDisplayed()
        )
    )

    private fun childAtPosition(parentMatcher: Matcher<View>, position: Int): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }

}
