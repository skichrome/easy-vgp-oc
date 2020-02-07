package com.skichrome.oc.easyvgp.view

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.platform.app.InstrumentationRegistry
import com.skichrome.oc.easyvgp.R
import com.skichrome.oc.easyvgp.model.CustomerAndroidDataProvider
import com.skichrome.oc.easyvgp.model.source.FakeAndroidTestNetManager
import com.skichrome.oc.easyvgp.view.fragments.AddEditCustomerFragment
import com.skichrome.oc.easyvgp.view.fragments.AddEditCustomerFragmentArgs
import com.skichrome.oc.easyvgp.viewmodel.ServiceLocator
import com.skichrome.oc.easyvgp.viewmodel.source.FakeAndroidTestCustomersRepository
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
class AddEditCustomerFragmentTest
{
    // =================================
    //              Fields
    // =================================

    private lateinit var customerRepo: FakeAndroidTestCustomersRepository
    private lateinit var netManager: FakeAndroidTestNetManager

    // =================================
    //              Methods
    // =================================

    // --- Init --- //

    @Before
    fun initRepoAndNetManager()
    {
        customerRepo = FakeAndroidTestCustomersRepository()
        ServiceLocator.customersRepository = customerRepo

        netManager = FakeAndroidTestNetManager(false)
        ServiceLocator.netManager = netManager
    }

    @After
    fun resetDataRepo() = runBlockingTest {
        ServiceLocator.resetRepository()
    }

    // --- Tests --- //

    @Test
    fun customerEdit_dataDisplayedInUI() = runBlockingTest {
        val customerToAdd = CustomerAndroidDataProvider.customer2
        customerRepo.saveCustomers(customerToAdd)

        val bundle = AddEditCustomerFragmentArgs(customerId = customerToAdd.id).toBundle()
        launchFragmentInContainer<AddEditCustomerFragment>(bundle, R.style.AppTheme)

        onView(withId(R.id.addEditCustomerFragFirstNameText)).check(matches(isDisplayed()))
        onView(withId(R.id.addEditCustomerFragFirstNameText)).check(matches(withText(customerToAdd.firstName)))

        onView(withId(R.id.addEditCustomerFragLastNameText)).check(matches(isDisplayed()))
        onView(withId(R.id.addEditCustomerFragLastNameText)).check(matches(withText(customerToAdd.lastName)))

        onView(withId(R.id.addEditCustomerFragSiretText)).check(matches(isDisplayed()))
        onView(withId(R.id.addEditCustomerFragSiretText)).check(matches(withText(customerToAdd.siret.toString())))

        onView(withId(R.id.addEditCustomerFragEmailText)).check(matches(isDisplayed()))
        onView(withId(R.id.addEditCustomerFragEmailText)).check(matches(withText(customerToAdd.email)))

        // Scroll up to display bottom fields
        onView(withId(R.id.addEditCustomerFragNotesText)).perform(scrollTo())

        onView(withId(R.id.addEditCustomerFragPhoneText)).check(matches(isDisplayed()))
        onView(withId(R.id.addEditCustomerFragPhoneText)).check(matches(withText(customerToAdd.phone.toString())))

        onView(withId(R.id.addEditCustomerFragMobilePhoneText)).check(matches(isDisplayed()))
        onView(withId(R.id.addEditCustomerFragMobilePhoneText)).check(matches(withText(customerToAdd.mobilePhone.toString())))

        onView(withId(R.id.addEditCustomerFragAddressText)).check(matches(isDisplayed()))
        onView(withId(R.id.addEditCustomerFragAddressText)).check(matches(withText(customerToAdd.address)))

        onView(withId(R.id.addEditCustomerFragPostCodeText)).check(matches(isDisplayed()))
        onView(withId(R.id.addEditCustomerFragPostCodeText)).check(matches(withText(customerToAdd.postCode.toString())))

        onView(withId(R.id.addEditCustomerFragCityText)).check(matches(isDisplayed()))
        onView(withId(R.id.addEditCustomerFragCityText)).check(matches(withText(customerToAdd.city)))

        onView(withId(R.id.addEditCustomerFragNotesText)).check(matches(isDisplayed()))
        onView(withId(R.id.addEditCustomerFragNotesText)).check(matches(withText(customerToAdd.notes)))
    }

    @Test
    fun customerAdd_NoDataDisplayed() = runBlockingTest {
        val bundle = AddEditCustomerFragmentArgs(customerId = -1L).toBundle()
        launchFragmentInContainer<AddEditCustomerFragment>(bundle, R.style.AppTheme)

        onView(withId(R.id.addEditCustomerFragFirstNameText)).check(matches(isDisplayed()))
        onView(withId(R.id.addEditCustomerFragFirstNameText)).check(matches(withText("")))

        onView(withId(R.id.addEditCustomerFragLastNameText)).check(matches(isDisplayed()))
        onView(withId(R.id.addEditCustomerFragLastNameText)).check(matches(withText("")))

        onView(withId(R.id.addEditCustomerFragSiretText)).check(matches(isDisplayed()))
        onView(withId(R.id.addEditCustomerFragSiretText)).check(matches(withText("")))

        onView(withId(R.id.addEditCustomerFragEmailText)).check(matches(isDisplayed()))
        onView(withId(R.id.addEditCustomerFragEmailText)).check(matches(withText("")))

        // Scroll up to display bottom fields
        onView(withId(R.id.addEditCustomerFragNotesText)).perform(scrollTo())

        onView(withId(R.id.addEditCustomerFragPhoneText)).check(matches(isDisplayed()))
        onView(withId(R.id.addEditCustomerFragPhoneText)).check(matches(withText("")))

        onView(withId(R.id.addEditCustomerFragMobilePhoneText)).check(matches(isDisplayed()))
        onView(withId(R.id.addEditCustomerFragMobilePhoneText)).check(matches(withText("")))

        onView(withId(R.id.addEditCustomerFragAddressText)).check(matches(isDisplayed()))
        onView(withId(R.id.addEditCustomerFragAddressText)).check(matches(withText("")))

        onView(withId(R.id.addEditCustomerFragPostCodeText)).check(matches(isDisplayed()))
        onView(withId(R.id.addEditCustomerFragPostCodeText)).check(matches(withText("")))

        onView(withId(R.id.addEditCustomerFragCityText)).check(matches(isDisplayed()))
        onView(withId(R.id.addEditCustomerFragCityText)).check(matches(withText("")))

        onView(withId(R.id.addEditCustomerFragNotesText)).check(matches(isDisplayed()))
        onView(withId(R.id.addEditCustomerFragNotesText)).check(matches(withText("")))
    }

    @Test
    fun customerAdd_NoDataDisplayed_ClickSave_ShouldPromptErrorOnlyOnRequiredTextFields() = runBlockingTest {
        val bundle = AddEditCustomerFragmentArgs(customerId = -1L).toBundle()
        launchFragmentInContainer<AddEditCustomerFragment>(bundle, R.style.AppTheme)

        onView(withId(R.id.addEditCustomerFragFab)).perform(click())

        val context = InstrumentationRegistry.getInstrumentation().targetContext

        onView(withId(R.id.addEditCustomerFragFirstNameText)).check(matches(hasErrorText(context.getString(R.string.test))))
        onView(withId(R.id.addEditCustomerFragLastNameText)).check(matches(hasErrorText(context.getString(R.string.test))))
        onView(withId(R.id.addEditCustomerFragSiretText)).check(matches(hasErrorText(context.getString(R.string.test))))
        onView(withId(R.id.addEditCustomerFragAddressText)).check(matches(hasErrorText(context.getString(R.string.test))))
        onView(withId(R.id.addEditCustomerFragPostCodeText)).check(matches(hasErrorText(context.getString(R.string.test))))
        onView(withId(R.id.addEditCustomerFragCityText)).check(matches(hasErrorText(context.getString(R.string.test))))

        // Scroll up to display bottom fields
        onView(withId(R.id.addEditCustomerFragNotesText)).perform(scrollTo())

        onView(withId(R.id.addEditCustomerFragEmailText)).check(matches(not(hasErrorText(context.getString(R.string.test)))))
        onView(withId(R.id.addEditCustomerFragPhoneText)).check(matches(not(hasErrorText(context.getString(R.string.test)))))
        onView(withId(R.id.addEditCustomerFragMobilePhoneText)).check(matches(not(hasErrorText(context.getString(R.string.test)))))
        onView(withId(R.id.addEditCustomerFragNotesText)).check(matches(not(hasErrorText(context.getString(R.string.test)))))
    }
}