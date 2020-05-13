package com.skichrome.oc.easyvgp.view.fragments

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.skichrome.oc.easyvgp.R
import com.skichrome.oc.easyvgp.model.AndroidDataProvider
import com.skichrome.oc.easyvgp.model.FakeAndroidTestCustomerRepository
import com.skichrome.oc.easyvgp.model.FakeAndroidTestNetManager
import com.skichrome.oc.easyvgp.viewmodel.ServiceLocator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@MediumTest
@RunWith(AndroidJUnit4::class)
class CustomerFragmentTest
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
    fun customersList_dataDisplayedInUI() = runBlockingTest {
        // Save a customer to database
        val customerToAdd = AndroidDataProvider.customer2
        customerRepo.saveCustomers(customerToAdd)
        customerRepo.refreshLiveData()

        launchFragmentInContainer<CustomerFragment>(null, R.style.AppTheme)

        onView(withId(R.id.rvItemCustomerFrameLayout)).check(matches(isDisplayed()))
        onView(withId(R.id.rvItemCustomerFrameLayout)).check(matches(isClickable()))
        onView(withId(R.id.rvItemCustomerFrameLayout)).check(matches(isFocusable()))

        onView(withId(R.id.rvItemCustomerEditImg)).check(matches(isDisplayed()))
        onView(withId(R.id.rvItemCustomerEditImg)).check(matches(isClickable()))
        onView(withId(R.id.rvItemCustomerEditImg)).check(matches(isFocusable()))

        onView(withId(R.id.rvItemCustomerFirstLetter)).check(matches(isDisplayed()))
        onView(withId(R.id.rvItemCustomerName)).check(matches(withText(`is`(customerToAdd.companyName))))
        onView(withId(R.id.rvItemCustomerCity)).check(matches(withText(`is`(customerToAdd.city))))
    }

    @Test
    fun customersFab_FabIsShown()
    {
        launchFragmentInContainer<CustomerFragment>(null, R.style.AppTheme)

        onView(withId(R.id.fragCustomerFab)).check(matches(isDisplayed()))
        onView(withId(R.id.fragCustomerFab)).check(matches(isClickable()))
        onView(withId(R.id.fragCustomerFab)).check(matches(isFocusable()))
    }
}