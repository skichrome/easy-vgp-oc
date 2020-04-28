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
import org.hamcrest.CoreMatchers.containsString
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class AddEditMachineFragmentTest
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
    fun machineAddEdit_newMachine_fieldsMustBeEmpty() = runBlockingTest {
        // Launch MachineFragment in test container
        val bundle = AddEditMachineFragmentArgs(
            customerId = AndroidDataProvider.machine1.customer
        ).toBundle()
        launchFragmentInContainer<AddEditMachineFragment>(bundle, R.style.AppTheme)

        onView(withId(R.id.addEditMachineFragmentNameEditText)).check(matches(isDisplayed()))
        onView(withId(R.id.addEditMachineFragmentBrandEditText)).check(matches(isDisplayed()))
        onView(withId(R.id.addEditMachineFragmentSerialEditText)).check(matches(isDisplayed()))
        onView(withId(R.id.addEditMachineFragmentNameEditText)).check(matches(withText("")))
        onView(withId(R.id.addEditMachineFragmentBrandEditText)).check(matches(withText("")))
        onView(withId(R.id.addEditMachineFragmentSerialEditText)).check(matches(withText("")))
    }

    @Test
    fun machineAddEdit_editMachine_fieldsMustBeNotEmpty() = runBlockingTest {
        // Provide data to repository and refresh liveData
        machineRepo.insertNewMachine(AndroidDataProvider.machine1)
        machineRepo.insertNewMachine(AndroidDataProvider.machine2)
        machineRepo.insertMachineTypes(AndroidDataProvider.machineTypeList)
        machineRepo.refresh()

        // Launch MachineFragment in test container
        val bundle = AddEditMachineFragmentArgs(
            customerId = AndroidDataProvider.machine1.customer,
            machineId = AndroidDataProvider.machine1.machineId
        ).toBundle()
        launchFragmentInContainer<AddEditMachineFragment>(bundle, R.style.AppTheme)

        // Assert that data is pre filled in all text fields
        onView(withId(R.id.addEditMachineFragmentNameEditText)).check(matches(isDisplayed()))
        onView(withId(R.id.addEditMachineFragmentBrandEditText)).check(matches(isDisplayed()))
        onView(withId(R.id.addEditMachineFragmentSerialEditText)).check(matches(isDisplayed()))
        onView(withId(R.id.addEditMachineFragmentNameEditText)).check(matches(withText(AndroidDataProvider.machine1.name)))
        onView(withId(R.id.addEditMachineFragmentBrandEditText)).check(matches(withText(AndroidDataProvider.machine1.brand)))
        onView(withId(R.id.addEditMachineFragmentSerialEditText)).check(matches(withText(AndroidDataProvider.machine1.serial)))
    }

    @Test
    fun machineAddEdit_EditMachine_spinnerMustContainPreselectedDataFromRepo() = runBlockingTest {
        // Provide data to repository and refresh liveData
        machineRepo.insertNewMachine(AndroidDataProvider.machine1)
        machineRepo.insertNewMachine(AndroidDataProvider.machine2)
        machineRepo.insertMachineTypes(AndroidDataProvider.machineTypeList)
        machineRepo.refresh()

        // Launch MachineFragment in test container
        val bundle = AddEditMachineFragmentArgs(
            customerId = AndroidDataProvider.machine1.customer,
            machineId = AndroidDataProvider.machine1.machineId
        ).toBundle()
        launchFragmentInContainer<AddEditMachineFragment>(bundle, R.style.AppTheme)

        // get the type of machine of the machine1 object
        val machineType = AndroidDataProvider.machineTypeList.filter { it.id == AndroidDataProvider.machine1Id }

        // Assert that the spinner contains the value stored in machine object, not the default value
        onView(withId(R.id.addEditMachineFragmentMachineTypeSpinner)).check(matches(withSpinnerText(containsString(machineType.first().name))))
    }
}