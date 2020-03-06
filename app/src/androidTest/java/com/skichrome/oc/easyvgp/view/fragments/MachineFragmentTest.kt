package com.skichrome.oc.easyvgp.view.fragments

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.skichrome.oc.easyvgp.R
import com.skichrome.oc.easyvgp.model.AndroidDataProvider
import com.skichrome.oc.easyvgp.model.FakeAndroidTestMachineRepository
import com.skichrome.oc.easyvgp.viewmodel.ServiceLocator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class MachineFragmentTest
{
    // =================================
    //              Fields
    // =================================

    private lateinit var machineRepo: FakeAndroidTestMachineRepository

    // =================================
    //              Methods
    // =================================

    // --- Configuration --- //

    @Before
    fun setUp()
    {
        machineRepo = FakeAndroidTestMachineRepository()
        ServiceLocator.machineRepository = machineRepo
    }

    @After
    fun tearDown() = runBlockingTest {
        ServiceLocator.resetRepository()
    }

    // --- Tests --- //

    @Test
    fun machinesList_assertThatDataIsDisplayedInUI() = runBlockingTest {
        // Provide data to repository and refresh liveData
        machineRepo.insertNewMachine(AndroidDataProvider.machine1)
        machineRepo.insertNewMachine(AndroidDataProvider.machine2)
        machineRepo.insertMachineTypes(AndroidDataProvider.machineTypeList)
        machineRepo.refresh()

        // Launch MachineFragment in test container
        val bundle = MachineFragmentArgs(customerId = AndroidDataProvider.machine1.customer).toBundle()
        launchFragmentInContainer<MachineFragment>(bundle, R.style.AppTheme)

        // Assert that only the machine that match the customer Id in bundle is displayed
        onView(withId(R.id.rvItemMachineCardView)).check(matches(isDisplayed()))
        onView(withId(R.id.rvItemMachineCardView)).check(matches(isClickable()))
        onView(withId(R.id.rvItemMachineCardView)).check(matches(isFocusable()))

        onView(withId(R.id.rvItemMachineName)).check(matches(isDisplayed()))
        onView(withId(R.id.rvItemMachineName)).check(matches(withText(AndroidDataProvider.machine1.name)))
        onView(withId(R.id.rvItemMachineBrand)).check(matches(isDisplayed()))
        onView(withId(R.id.rvItemMachineBrand)).check(matches(withText(AndroidDataProvider.machine1.brand)))
        onView(withId(R.id.rvItemMachineSerial)).check(matches(isDisplayed()))
        onView(withId(R.id.rvItemMachineSerial)).check(matches(withText(AndroidDataProvider.machine1.serial)))

        onView(withId(R.id.rvItemMachineName)).check(matches(isDisplayed()))
        onView(withId(R.id.rvItemMachineName)).check(matches(not(withText(AndroidDataProvider.machine2.name))))
        onView(withId(R.id.rvItemMachineBrand)).check(matches(isDisplayed()))
        onView(withId(R.id.rvItemMachineBrand)).check(matches(not(withText(AndroidDataProvider.machine2.brand))))
        onView(withId(R.id.rvItemMachineSerial)).check(matches(isDisplayed()))
        onView(withId(R.id.rvItemMachineSerial)).check(matches(not(withText(AndroidDataProvider.machine2.serial))))
    }

    @Test
    fun machineFab_isDisplayed()
    {
        // Launch MachineFragment in test container
        val bundle = MachineFragmentArgs(customerId = AndroidDataProvider.machine1.customer).toBundle()
        launchFragmentInContainer<MachineFragment>(bundle, R.style.AppTheme)

        onView(withId(R.id.fragMachineFab)).check(matches(isDisplayed()))
        onView(withId(R.id.fragMachineFab)).check(matches(isClickable()))
        onView(withId(R.id.fragMachineFab)).check(matches(isFocusable()))
    }
}