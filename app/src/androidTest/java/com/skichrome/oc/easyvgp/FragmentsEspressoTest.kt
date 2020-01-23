package com.skichrome.oc.easyvgp

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.skichrome.oc.easyvgp.view.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FragmentsEspressoTest
{
    @get:Rule
    val activityRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun checkHomeFragmentIsDisplayed()
    {
        onView(withId(R.id.fragHomeBtnNewVGP)).check(matches(isDisplayed()))
    }

    @Test
    fun checkSettingsFragment()
    {
        onView(withId(R.id.settingsFragment)).perform(click())
        onView(withId(R.id.activityMainFrameLayout)).check(matches(isDisplayed()))
    }

    @Test
    fun checkSettingsFragmentAndBackNavigation()
    {
        onView(withId(R.id.settingsFragment)).perform(click())
        onView(withId(R.id.activityMainFrameLayout)).check(matches(isDisplayed()))
        pressBack()
        onView(withId(R.id.fragHomeBtnNewVGP)).check(matches(isDisplayed()))
    }
}