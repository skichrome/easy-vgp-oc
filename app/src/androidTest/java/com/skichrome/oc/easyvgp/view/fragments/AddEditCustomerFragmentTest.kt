package com.skichrome.oc.easyvgp.view.fragments

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
import com.skichrome.oc.easyvgp.model.AndroidDataProvider
import com.skichrome.oc.easyvgp.model.FakeAndroidTestCustomerRepository
import com.skichrome.oc.easyvgp.model.FakeAndroidTestNetManager
import com.skichrome.oc.easyvgp.viewmodel.ServiceLocator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
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

    private lateinit var customerRepo: FakeAndroidTestCustomerRepository
    private lateinit var netManager: FakeAndroidTestNetManager

    // =================================
    //              Methods
    // =================================

    // --- Init --- //

    @Before
    fun initRepoAndNetManager()
    {
        customerRepo = FakeAndroidTestCustomerRepository()
        ServiceLocator.customerRepository = customerRepo

        netManager = FakeAndroidTestNetManager(false)
        ServiceLocator.netManager = netManager
    }

    @After
    fun resetDataRepo() = runBlockingTest {
        ServiceLocator.resetRepository()
    }

    // --- Tests --- //

    @Test
    fun customerEdit_dataDisplayedInUI() = runBlocking {
        val customerToAdd = AndroidDataProvider.customer2
        customerRepo.saveCustomers(customerToAdd)

        val bundle = AddEditCustomerFragmentArgs(customerId = customerToAdd.id).toBundle()
        launchFragmentInContainer<AddEditCustomerFragment>(bundle, R.style.AppTheme)

        onView(withId(R.id.addEditCustomerFragmentFirstNameEditText)).check(matches(isDisplayed()))
        onView(withId(R.id.addEditCustomerFragmentFirstNameEditText)).check(matches(withText(customerToAdd.firstName)))

        onView(withId(R.id.addEditCustomerFragmentLastNameEditText)).check(matches(isDisplayed()))
        onView(withId(R.id.addEditCustomerFragmentLastNameEditText)).check(matches(withText(customerToAdd.lastName)))

        onView(withId(R.id.addEditCustomerFragmentCompanyNameEditText)).check(matches(isDisplayed()))
        onView(withId(R.id.addEditCustomerFragmentCompanyNameEditText)).check(matches(withText(customerToAdd.companyName)))

        onView(withId(R.id.addEditCustomerFragmentSiretEditText)).check(matches(isDisplayed()))
        onView(withId(R.id.addEditCustomerFragmentSiretEditText)).check(matches(withText(customerToAdd.siret)))

        // Scroll up to display bottom fields
        onView(withId(R.id.addEditCustomerFragmentEmailEditText)).perform(scrollTo())

        onView(withId(R.id.addEditCustomerFragmentEmailEditText)).check(matches(isDisplayed()))
        onView(withId(R.id.addEditCustomerFragmentEmailEditText)).check(matches(withText(customerToAdd.email)))

        // Scroll up to display bottom fields
        onView(withId(R.id.addEditCustomerFragmentPhoneEditText)).perform(scrollTo())

        onView(withId(R.id.addEditCustomerFragmentPhoneEditText)).check(matches(isDisplayed()))
        onView(withId(R.id.addEditCustomerFragmentPhoneEditText)).check(matches(withText(customerToAdd.phone.toString())))

        onView(withId(R.id.addEditCustomerFragmentMobilePhoneEditText)).check(matches(isDisplayed()))
        onView(withId(R.id.addEditCustomerFragmentMobilePhoneEditText)).check(matches(withText(customerToAdd.mobilePhone.toString())))

        // Scroll up to display bottom fields
        onView(withId(R.id.addEditCustomerFragmentAddressEditText)).perform(scrollTo())

        onView(withId(R.id.addEditCustomerFragmentAddressEditText)).check(matches(isDisplayed()))
        onView(withId(R.id.addEditCustomerFragmentAddressEditText)).check(matches(withText(customerToAdd.address)))

        // Scroll up to display bottom fields
        onView(withId(R.id.addEditCustomerFragmentNotesEditText)).perform(scrollTo())

        onView(withId(R.id.addEditCustomerFragmentPostCodeEditText)).check(matches(isDisplayed()))
        onView(withId(R.id.addEditCustomerFragmentPostCodeEditText)).check(matches(withText(customerToAdd.postCode.toString())))

        onView(withId(R.id.addEditCustomerFragmentCityEditText)).check(matches(isDisplayed()))
        onView(withId(R.id.addEditCustomerFragmentCityEditText)).check(matches(withText(customerToAdd.city)))

        onView(withId(R.id.addEditCustomerFragmentNotesEditText)).check(matches(isDisplayed()))
        onView(withId(R.id.addEditCustomerFragmentNotesEditText)).check(matches(withText(customerToAdd.notes)))
        return@runBlocking Unit
    }

    @Test
    fun customerAdd_NoDataDisplayed() = runBlockingTest {
        val bundle = AddEditCustomerFragmentArgs(customerId = -1L).toBundle()
        launchFragmentInContainer<AddEditCustomerFragment>(bundle, R.style.AppTheme)

        onView(withId(R.id.addEditCustomerFragmentFirstNameEditText)).check(matches(isDisplayed()))
        onView(withId(R.id.addEditCustomerFragmentFirstNameEditText)).check(matches(withText("")))

        onView(withId(R.id.addEditCustomerFragmentLastNameEditText)).check(matches(isDisplayed()))
        onView(withId(R.id.addEditCustomerFragmentLastNameEditText)).check(matches(withText("")))

        onView(withId(R.id.addEditCustomerFragmentCompanyNameEditText)).check(matches(isDisplayed()))
        onView(withId(R.id.addEditCustomerFragmentCompanyNameEditText)).check(matches(withText("")))

        onView(withId(R.id.addEditCustomerFragmentSiretEditText)).check(matches(isDisplayed()))
        onView(withId(R.id.addEditCustomerFragmentSiretEditText)).check(matches(withText("")))

        // Scroll up to display bottom fields
        onView(withId(R.id.addEditCustomerFragmentEmailEditText)).perform(scrollTo())

        onView(withId(R.id.addEditCustomerFragmentEmailEditText)).check(matches(isDisplayed()))
        onView(withId(R.id.addEditCustomerFragmentEmailEditText)).check(matches(withText("")))

        // Scroll up to display bottom fields
        onView(withId(R.id.addEditCustomerFragmentPhoneEditText)).perform(scrollTo())

        onView(withId(R.id.addEditCustomerFragmentPhoneEditText)).check(matches(isDisplayed()))
        onView(withId(R.id.addEditCustomerFragmentPhoneEditText)).check(matches(withText("")))

        onView(withId(R.id.addEditCustomerFragmentMobilePhoneEditText)).check(matches(isDisplayed()))
        onView(withId(R.id.addEditCustomerFragmentMobilePhoneEditText)).check(matches(withText("")))

        // Scroll up to display bottom fields
        onView(withId(R.id.addEditCustomerFragmentAddressEditText)).perform(scrollTo())

        onView(withId(R.id.addEditCustomerFragmentAddressEditText)).check(matches(isDisplayed()))
        onView(withId(R.id.addEditCustomerFragmentAddressEditText)).check(matches(withText("")))

        // Scroll up to display bottom fields
        onView(withId(R.id.addEditCustomerFragmentNotesEditText)).perform(scrollTo())

        onView(withId(R.id.addEditCustomerFragmentPostCodeEditText)).check(matches(isDisplayed()))
        onView(withId(R.id.addEditCustomerFragmentPostCodeEditText)).check(matches(withText("")))

        onView(withId(R.id.addEditCustomerFragmentCityEditText)).check(matches(isDisplayed()))
        onView(withId(R.id.addEditCustomerFragmentCityEditText)).check(matches(withText("")))

        onView(withId(R.id.addEditCustomerFragmentNotesEditText)).check(matches(isDisplayed()))
        onView(withId(R.id.addEditCustomerFragmentNotesEditText)).check(matches(withText("")))
    }

    @Test
    fun customerAdd_NoDataDisplayed_ClickSave_ShouldPromptErrorOnlyOnRequiredTextFields() = runBlockingTest {
        val bundle = AddEditCustomerFragmentArgs(customerId = -1L).toBundle()
        launchFragmentInContainer<AddEditCustomerFragment>(bundle, R.style.AppTheme)

        onView(withId(R.id.addEditCustomerFragFab)).perform(click())

        val context = InstrumentationRegistry.getInstrumentation().targetContext

        onView(withId(R.id.addEditCustomerFragmentFirstNameEditText)).check(matches(hasErrorText(context.getString(R.string.frag_add_edit_customer_error_input))))
        onView(withId(R.id.addEditCustomerFragmentLastNameEditText)).check(matches(hasErrorText(context.getString(R.string.frag_add_edit_customer_error_input))))
        onView(withId(R.id.addEditCustomerFragmentSiretEditText)).check(matches(hasErrorText(context.getString(R.string.frag_add_edit_customer_error_input))))
        onView(withId(R.id.addEditCustomerFragmentAddressEditText)).check(matches(hasErrorText(context.getString(R.string.frag_add_edit_customer_error_input))))
        onView(withId(R.id.addEditCustomerFragmentPostCodeEditText)).check(matches(hasErrorText(context.getString(R.string.frag_add_edit_customer_error_input))))
        onView(withId(R.id.addEditCustomerFragmentCityEditText)).check(matches(hasErrorText(context.getString(R.string.frag_add_edit_customer_error_input))))

        // Scroll up to display bottom fields
        onView(withId(R.id.addEditCustomerFragmentNotesEditText)).perform(scrollTo())

        onView(withId(R.id.addEditCustomerFragmentEmailEditText)).check(matches(hasErrorText(context.getString(R.string.frag_add_edit_customer_error_input))))
        onView(withId(R.id.addEditCustomerFragmentPhoneEditText)).check(matches(not(hasErrorText(context.getString(R.string.frag_add_edit_customer_error_input)))))
        onView(withId(R.id.addEditCustomerFragmentMobilePhoneEditText)).check(matches(not(hasErrorText(context.getString(R.string.frag_add_edit_customer_error_input)))))
        onView(withId(R.id.addEditCustomerFragmentNotesEditText)).check(matches(not(hasErrorText(context.getString(R.string.frag_add_edit_customer_error_input)))))
    }
}