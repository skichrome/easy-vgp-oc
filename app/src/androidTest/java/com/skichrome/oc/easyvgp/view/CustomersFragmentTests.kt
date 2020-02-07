package com.skichrome.oc.easyvgp.view

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.skichrome.oc.easyvgp.R
import com.skichrome.oc.easyvgp.model.CustomerAndroidDataProvider
import com.skichrome.oc.easyvgp.model.source.FakeAndroidTestNetManager
import com.skichrome.oc.easyvgp.view.fragments.CustomerFragment
import com.skichrome.oc.easyvgp.view.fragments.CustomerFragmentArgs
import com.skichrome.oc.easyvgp.viewmodel.ServiceLocator
import com.skichrome.oc.easyvgp.viewmodel.source.FakeAndroidTestCustomersRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class CustomersFragmentTests
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
    fun customersList_dataDisplayedInUI() = runBlockingTest {
        // Save a customer to database
        val customerToAdd = CustomerAndroidDataProvider.customer2
        customerRepo.saveCustomers(customerToAdd)
        customerRepo.refreshLiveData()

        val bundle = CustomerFragmentArgs(false).toBundle()
        launchFragmentInContainer<CustomerFragment>(bundle, R.style.AppTheme)

        onView(withId(R.id.rvItemCustomerCardView)).check(matches(isDisplayed()))
        onView(withId(R.id.rvItemCustomerCardView)).check(matches(isClickable()))
        onView(withId(R.id.rvItemCustomerCardView)).check(matches(isFocusable()))

        onView(withId(R.id.rvItemCustomerFirstLetter)).check(matches(isDisplayed()))
        onView(withId(R.id.rvItemCustomerName)).check(matches(withText(`is`("${customerToAdd.lastName} ${customerToAdd.firstName}"))))
    }

    @Test
    fun customersFab_IfArgsIsTrue_FabIsHidden()
    {
        val bundle = CustomerFragmentArgs(true).toBundle()
        launchFragmentInContainer<CustomerFragment>(bundle, R.style.AppTheme)

        onView(withId(R.id.fragCustomerFab)).check(matches(not(isDisplayed())))
    }

    @Test
    fun customersFab_IfArgsIsFalse_FabIsShown()
    {
        val bundle = CustomerFragmentArgs(false).toBundle()
        launchFragmentInContainer<CustomerFragment>(bundle, R.style.AppTheme)

        onView(withId(R.id.fragCustomerFab)).check(matches(isDisplayed()))
    }
}