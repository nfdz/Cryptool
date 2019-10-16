package io.github.nfdz.cryptool.screens.main

import android.R
import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.hamcrest.TypeSafeMatcher

fun closeWelcome() {
    try {
        val closeWelcome = Espresso.onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.button2), ViewMatchers.withText("Close"), childAtPosition(
                    childAtPosition(
                        ViewMatchers.withId(io.github.nfdz.cryptool.R.id.buttonPanel),
                        0
                    ), 2
                )
            )
        )
        closeWelcome.perform(ViewActions.scrollTo(), ViewActions.click())
    } catch (e: NoMatchingViewException) {
        // Swallow -> There is no welcome
    }
}

fun clickNavigationOption(id: Int) {
    val bottomNavigationItemView = Espresso.onView(
        Matchers.allOf(
            ViewMatchers.withId(id),
            ViewMatchers.isDisplayed()
        )
    )
    bottomNavigationItemView.perform(ViewActions.click())
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

