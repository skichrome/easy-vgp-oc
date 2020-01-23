package com.skichrome.oc.easyvgp

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.skichrome.oc.easyvgp.view.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ActivityTest
{
    @get:Rule
    val activityScenarioRule = activityScenarioRule<MainActivity>()

    @Test
    fun launchActivityAndGoToSettings()
    {
        onView(withId(R.id.toolbar)).check(matches(isDisplayed()))
        onView(withId(R.id.settingsFragment)).perform(click())
        onView(withId(R.id.activityMainFrameLayout)).check(matches(isDisplayed()))
    }
}