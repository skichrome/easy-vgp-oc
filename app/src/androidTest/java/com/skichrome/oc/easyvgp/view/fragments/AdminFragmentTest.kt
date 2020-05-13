package com.skichrome.oc.easyvgp.view.fragments

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.skichrome.oc.easyvgp.R
import com.skichrome.oc.easyvgp.model.AndroidDataProvider
import com.skichrome.oc.easyvgp.model.FakeAndroidTestAdminRepository
import com.skichrome.oc.easyvgp.model.FakeAndroidTestNetManager
import com.skichrome.oc.easyvgp.viewmodel.ServiceLocator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@MediumTest
@RunWith(AndroidJUnit4::class)
class AdminFragmentTest
{
    // =================================
    //              Fields
    // =================================

    private lateinit var netManager: FakeAndroidTestNetManager
    private lateinit var repository: FakeAndroidTestAdminRepository

    // =================================
    //              Methods
    // =================================

    // --- Init --- //

    @Before
    fun setup()
    {
        repository = FakeAndroidTestAdminRepository()
        ServiceLocator.adminRepository = repository

        netManager = FakeAndroidTestNetManager(false)
        ServiceLocator.netManager = netManager

        repository.refresh()
    }

    @After
    fun tearDown() = runBlockingTest {
        ServiceLocator.resetRepository()
    }

    // --- Tests --- //

    @Test
    fun adminFragment_uiInfoDisplayed() = runBlockingTest {
        launchFragmentInContainer<AdminFragment>(null, R.style.AppTheme)

        onView(withId(R.id.adminFragmentFabNewMachineTypeTitle)).check(matches(isDisplayed()))
        onView(withId(R.id.adminFragmentSeparator)).check(matches(isDisplayed()))
        onView(withId(R.id.adminFragmentFabNewControlPointTitle)).check(matches(isDisplayed()))

        // Recycler views
        onView(withId(R.id.admin_fragment_machine_type_recycler_view)).check(matches(isDisplayed()))
        onView(withId(R.id.adminFragmentControlPointsRecyclerView)).check(matches(isDisplayed()))

        // FAB
        onView(withId(R.id.adminFragmentFabMain)).check(matches(isDisplayed()))
        onView(withId(R.id.adminFragmentFabMain)).check(matches(isFocusable()))
        onView(withId(R.id.adminFragmentFabMain)).check(matches(isClickable()))

        onView(withId(R.id.adminFragmentFabNewMachineType)).check(matches(isFocusable()))
        onView(withId(R.id.adminFragmentFabNewMachineType)).check(matches(isClickable()))
        onView(withId(R.id.adminFragmentFabNewCtrlPoint)).check(matches(isFocusable()))
        onView(withId(R.id.adminFragmentFabNewCtrlPoint)).check(matches(isClickable()))

        onView(withId(R.id.adminFragmentFabNewMachineType)).check(matches(not((isDisplayed()))))
        onView(withId(R.id.adminFragmentFabNewCtrlPoint)).check(matches(not(isDisplayed())))
        onView(withId(R.id.adminFragmentFabNewMachineTypeText)).check(matches(not((isDisplayed()))))
        onView(withId(R.id.adminFragmentFabNewCtrlPointText)).check(matches(not(isDisplayed())))

        // Open secondary fab menu
        onView(withId(R.id.adminFragmentFabMain)).perform(click())

        onView(withId(R.id.adminFragmentFabNewMachineType)).check(matches((isDisplayed())))
        onView(withId(R.id.adminFragmentFabNewCtrlPoint)).check(matches(isDisplayed()))
        onView(withId(R.id.adminFragmentFabNewMachineTypeText)).check(matches((isDisplayed())))
        onView(withId(R.id.adminFragmentFabNewCtrlPointText)).check(matches(isDisplayed()))
        onView(withId(R.id.adminFragmentFabMain)).check(matches(isDisplayed()))
    }

    @Test
    fun adminFragment_machineTypeListLoaded() = runBlockingTest {
        val machineType = AndroidDataProvider.machineType2

        repository.insertNewMachineType(machineType)
        repository.refresh()

        launchFragmentInContainer<AdminFragment>(null, R.style.AppTheme)

        onView(withId(R.id.rvItemMachineTypeRootFrameLayout)).check(matches(isDisplayed()))
        onView(withId(R.id.rvItemMachineTypeRootFrameLayout)).check(matches(isClickable()))
        onView(withId(R.id.rvItemMachineTypeRootFrameLayout)).check(matches(isFocusable()))

        onView(withId(R.id.rvItemMachineTypeName)).check(matches(withText(machineType.name)))
        onView(withId(R.id.rvItemMachineTypeLegalName)).check(matches(withText(machineType.legalName)))
    }

    @Test
    fun adminFragment_controlPointListLoaded() = runBlockingTest {
        val controlPoint = AndroidDataProvider.ctrlPoint1

        repository.insertNewControlPoint(controlPoint)
        repository.refresh()

        launchFragmentInContainer<AdminFragment>(null, R.style.AppTheme)

        onView(withId(R.id.rvItemCtrlPtRootFrameLayout)).check(matches(isDisplayed()))
        onView(withId(R.id.rvItemCtrlPtRootFrameLayout)).check(matches(isClickable()))
        onView(withId(R.id.rvItemCtrlPtRootFrameLayout)).check(matches(isFocusable()))

        onView(withId(R.id.rvItemCtrlPointName)).check(matches(withText(controlPoint.name)))
        onView(withId(R.id.rvItemCtrlPointCode)).check(matches(withText(controlPoint.code)))
    }
}