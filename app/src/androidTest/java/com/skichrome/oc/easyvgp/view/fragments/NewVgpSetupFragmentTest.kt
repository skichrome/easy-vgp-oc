package com.skichrome.oc.easyvgp.view.fragments

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.skichrome.oc.easyvgp.R
import com.skichrome.oc.easyvgp.model.AndroidDataProvider
import com.skichrome.oc.easyvgp.model.FakeAndroidTestNewVgpSetupRepository
import com.skichrome.oc.easyvgp.model.local.ControlType
import com.skichrome.oc.easyvgp.util.getDateFormatted
import com.skichrome.oc.easyvgp.viewmodel.ServiceLocator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@MediumTest
@RunWith(AndroidJUnit4::class)
class NewVgpSetupFragmentTest
{
    // =================================
    //              Fields
    // =================================

    private lateinit var repository: FakeAndroidTestNewVgpSetupRepository

    // =================================
    //              Methods
    // =================================

    // --- Configuration --- //

    @Before
    fun setUp()
    {
        repository = FakeAndroidTestNewVgpSetupRepository()
        ServiceLocator.newVgpSetupRepository = repository
    }

    @After
    fun tearDown() = runBlockingTest {
        ServiceLocator.resetRepository()
    }

    // --- Tests --- //

    @Test
    fun newVgpSetupFragment_testUIDateDisplayed() = runBlockingTest {
        val currentTime = System.currentTimeMillis()

        val bundle = NewVgpSetupFragmentArgs(
            machineTypeId = -1L,
            reportDateToEdit = -1L,
            reportDateFromDatePicker = currentTime,
            customerId = -1L,
            machineId = -1L
        ).toBundle()

        launchFragmentInContainer<NewVgpSetupFragment>(bundle, R.style.AppTheme)

        onView(withId(R.id.fragmentNewVgpSetupReportDate)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(getDateFormatted(currentTime))))
        }
    }

    @Test
    fun newVgpSetupFragment_uiData_newVgp() = runBlockingTest {
        val bundle = NewVgpSetupFragmentArgs().toBundle()

        launchFragmentInContainer<NewVgpSetupFragment>(bundle, R.style.AppTheme)

        onView(withId(R.id.fragmentNewVgpSetupReportDateSetBtn)).check(matches(isDisplayed()))
        onView(withId(R.id.fragmentNewVgpSetupReportDateSetBtn)).check(matches(isEnabled()))

        onView(withId(R.id.fragmentNewVgpSetupTitle)).check(matches(isDisplayed()))
        onView(withId(R.id.fragmentNewVgpSetupMachineHours)).apply {
            check(matches(isDisplayed()))
            check(matches(withText("")))
        }
        onView(withId(R.id.fragmentNewVgpSetupReportDate)).apply {
            check(matches(isDisplayed()))
            check(matches(not(withText(""))))
        }
        onView(withId(R.id.fragmentNewVgpSetupInterventionPlace)).apply {
            check(matches(isDisplayed()))
            check(matches(withText("")))
        }

        onView(withId(R.id.fragmentNewVgpSetupIsMachineLiftingEquiped)).perform(scrollTo())

        onView(withId(R.id.fragmentNewVgpSetupIsMachineClean)).apply {
            check(matches(isDisplayed()))
            check(matches(isNotChecked()))
        }

        onView(withId(R.id.fragmentNewVgpSetupIsMachineMarkedCE)).apply {
            check(matches(isDisplayed()))
            check(matches(isNotChecked()))
        }
        onView(withId(R.id.fragmentNewVgpSetupIsMachineNoticeAvailable)).apply {
            check(matches(isDisplayed()))
            check(matches(isNotChecked()))
        }
        onView(withId(R.id.fragmentNewVgpSetupIsMachineLiftingEquiped)).apply {
            check(matches(isDisplayed()))
            check(matches(isNotChecked()))
        }

        onView(withId(R.id.fragmentNewVgpSetupControlLoadType)).perform(scrollTo())

        onView(withId(R.id.fragmentNewVgpSetupIsMachineControlWithLoad)).apply {
            check(matches(isDisplayed()))
            check(matches(isNotChecked()))
        }
        onView(withId(R.id.fragmentNewVgpSetupIsMachineControlWithNominalLoad)).apply {
            check(matches(isDisplayed()))
            check(matches(not(isEnabled())))
            check(matches(isNotChecked()))
        }
        onView(withId(R.id.fragmentNewVgpSetupIsMachineControlTriggeredSensors)).apply {
            check(matches(isDisplayed()))
            check(matches(not(isEnabled())))
            check(matches(isNotChecked()))
        }
        onView(withId(R.id.fragmentNewVgpSetupControlLoadType)).apply {
            check(matches(isDisplayed()))
            check(matches(not(isEnabled())))
            check(matches(withText("")))
        }

        onView(withId(R.id.fragmentNewVgpSetupMachineControlType)).perform(scrollTo())

        onView(withId(R.id.fragmentNewVgpSetupControlLoadValue)).apply {
            check(matches(isDisplayed()))
            check(matches(not(isEnabled())))
            check(matches(withText("")))
        }
        onView(withId(R.id.fragmentNewVgpSetupMachineControlTypeTitle)).check(matches(isDisplayed()))
        onView(withId(R.id.fragmentNewVgpSetupMachineControlType)).apply {
            check(matches(isDisplayed()))
            check(matches(withSpinnerText(ControlType.PUT_INTO_SERVICE.type)))
        }

        onView(withId(R.id.fragmentNewVgpSetupFab)).apply {
            check(matches(isDisplayed()))
            check(matches(isClickable()))
            check(matches(isFocusable()))
        }
    }

    @Test
    fun newVgpSetupFragment_uiData_editVgp() = runBlockingTest {
        val extras = AndroidDataProvider.extraList
        val expectedExtra = AndroidDataProvider.extra2

        repository.insertExtrasForTests(extras)

        val bundle = NewVgpSetupFragmentArgs(
            machineTypeId = AndroidDataProvider.machineType1Id,
            reportDateToEdit = expectedExtra.reportDate,
            reportDateFromDatePicker = -1L,
            customerId = AndroidDataProvider.customer1Id,
            machineId = AndroidDataProvider.machine1Id
        ).toBundle()

        launchFragmentInContainer<NewVgpSetupFragment>(bundle, R.style.AppTheme)

        onView(withId(R.id.fragmentNewVgpSetupReportDateSetBtn)).check(matches(isDisplayed()))
        onView(withId(R.id.fragmentNewVgpSetupReportDateSetBtn)).check(matches(not(isEnabled())))

        onView(withId(R.id.fragmentNewVgpSetupTitle)).check(matches(isDisplayed()))
        onView(withId(R.id.fragmentNewVgpSetupMachineHours)).apply {
            check(matches(isDisplayed()))
            check(matches(withText("${expectedExtra.machineHours}")))
        }
        onView(withId(R.id.fragmentNewVgpSetupReportDate)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(getDateFormatted(expectedExtra.reportDate))))
        }
        onView(withId(R.id.fragmentNewVgpSetupInterventionPlace)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(expectedExtra.interventionPlace)))
        }

        onView(withId(R.id.fragmentNewVgpSetupIsMachineLiftingEquiped)).perform(scrollTo())

        onView(withId(R.id.fragmentNewVgpSetupIsMachineClean)).apply {
            check(matches(isDisplayed()))
            check(matches(if (expectedExtra.isMachineClean) isChecked() else isNotChecked()))
        }

        onView(withId(R.id.fragmentNewVgpSetupIsMachineMarkedCE)).apply {
            check(matches(isDisplayed()))
            check(matches(if (expectedExtra.isMachineCE) isChecked() else isNotChecked()))
        }
        onView(withId(R.id.fragmentNewVgpSetupIsMachineNoticeAvailable)).apply {
            check(matches(isDisplayed()))
            check(matches(if (expectedExtra.machineNotice) isChecked() else isNotChecked()))
        }
        onView(withId(R.id.fragmentNewVgpSetupIsMachineLiftingEquiped)).apply {
            check(matches(isDisplayed()))
            check(matches(if (expectedExtra.isLiftingEquip) isChecked() else isNotChecked()))
        }

        onView(withId(R.id.fragmentNewVgpSetupControlLoadType)).perform(scrollTo())

        onView(withId(R.id.fragmentNewVgpSetupIsMachineControlWithLoad)).apply {
            check(matches(isDisplayed()))
            check(matches(if (expectedExtra.isTestsWithLoad) isChecked() else isNotChecked()))
        }
        onView(withId(R.id.fragmentNewVgpSetupIsMachineControlWithNominalLoad)).apply {
            check(matches(isDisplayed()))
            check(matches(if (expectedExtra.isTestsWithLoad) isEnabled() else not(isEnabled())))
            check(matches(if (expectedExtra.isTestsWithNominalLoad == true) isChecked() else isNotChecked()))
        }

        onView(withId(R.id.fragmentNewVgpSetupIsMachineControlTriggeredSensors)).apply {
            check(matches(isDisplayed()))
            check(matches(if (expectedExtra.isTestsWithLoad) isEnabled() else not(isEnabled())))
            check(matches(if (expectedExtra.testsHasTriggeredSensors == true) isChecked() else isNotChecked()))
        }
        onView(withId(R.id.fragmentNewVgpSetupControlLoadType)).apply {
            check(matches(isDisplayed()))
            check(matches(if (expectedExtra.isTestsWithLoad) isEnabled() else not(isEnabled())))
            check(matches(withText(if (expectedExtra.loadType != null) expectedExtra.loadType else "")))
        }

        onView(withId(R.id.fragmentNewVgpSetupMachineControlType)).perform(scrollTo())

        onView(withId(R.id.fragmentNewVgpSetupControlLoadValue)).apply {
            check(matches(isDisplayed()))
            check(matches(if (expectedExtra.isTestsWithLoad) isEnabled() else not(isEnabled())))
            check(matches(withText(if (expectedExtra.loadMass != null) "${expectedExtra.loadMass}" else "")))
        }
        onView(withId(R.id.fragmentNewVgpSetupMachineControlTypeTitle)).check(matches(isDisplayed()))
        onView(withId(R.id.fragmentNewVgpSetupMachineControlType)).apply {
            check(matches(isDisplayed()))
            check(matches(withSpinnerText(expectedExtra.controlType.type)))
        }

        onView(withId(R.id.fragmentNewVgpSetupFab)).apply {
            check(matches(isDisplayed()))
            check(matches(isClickable()))
            check(matches(isFocusable()))
        }
    }
}