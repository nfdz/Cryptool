package io.github.nfdz.cryptool.screens.main

import android.R
import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import io.github.nfdz.cryptool.common.utils.PreferencesHelper
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher


fun cleanStoredData(context: Context) {
    PreferencesHelper(context).clearAllSync()
}
fun closeWelcome() {
    try {
        val closeWelcome = onView(
            allOf(
                withId(R.id.button2),
                withText("Close"),
                childAtPosition(
                    childAtPosition(
                        withId(io.github.nfdz.cryptool.R.id.buttonPanel),
                        0
                    ), 2
                )
            )
        )
        closeWelcome.perform(ViewActions.scrollTo(), click())
    } catch (e: NoMatchingViewException) {
        // Swallow -> There is no welcome
    }
}

fun clickNavigationOption(id: Int) {
    val bottomNavigationItemView = onView(allOf(withId(id), ViewMatchers.isDisplayed()))
    bottomNavigationItemView.perform(click())
}

fun childAtPosition(parentMatcher: Matcher<View>, position: Int): Matcher<View> {
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

