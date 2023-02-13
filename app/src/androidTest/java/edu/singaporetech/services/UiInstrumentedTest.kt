package edu.singaporetech.services

import android.util.Log
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.Matchers.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by your teacher.
 */
@RunWith(AndroidJUnit4::class)
class UiInstrumentedTest {
    companion object {
        private const val TAG = "UiInstrumentedTest"

        // test data
        private const val PRIME_BUTTON_TEXT = "CHECK PRIME"
        private const val RANDOM_BUTTON_TEXT = "CURRENT RANDOM NUMBER"
    }

    @get:Rule
    var activityRule = activityScenarioRule<ServiceActivity>()

    @Test
    fun onLaunch_containsRequiredUI() {
        Log.i(
                TAG, "\n### UI elements all exist" +
                "\n- text elements textEditPrime, textViewResult exist" +
                "\n- buttons" + PRIME_BUTTON_TEXT + ", " + RANDOM_BUTTON_TEXT + "exist"
        )

//        onView(withId(R.id.textEditPrime))
//                .check(matches(isDisplayed()))
//
//        onView(withId(R.id.textViewResult))
//                .check(matches(isDisplayed()))

        onView(allOf(withClassName(endsWith("Button")), withText(PRIME_BUTTON_TEXT)))
                .check(matches(isDisplayed()))

        onView(allOf(withClassName(endsWith("Button")), withText(RANDOM_BUTTON_TEXT)))
                .check(matches(isDisplayed()))
    }
}