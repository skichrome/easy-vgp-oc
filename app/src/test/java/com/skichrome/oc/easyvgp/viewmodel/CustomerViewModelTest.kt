package com.skichrome.oc.easyvgp.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.skichrome.oc.easyvgp.getOrAwaitValue
import com.skichrome.oc.easyvgp.model.source.DataProvider
import com.skichrome.oc.easyvgp.viewmodel.source.FakeCustomerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsNot
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class CustomerViewModelTest
{
    // =================================
    //              Fields
    // =================================

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var customerViewModel: CustomerViewModel
    private lateinit var customerRepository: FakeCustomerRepository

    // =================================
    //              Methods
    // =================================

    @Before
    fun initTests() = runBlockingTest {
        customerRepository = FakeCustomerRepository()
        customerRepository.saveCustomers(DataProvider.localCustomers.toTypedArray())
        customerRepository.refreshLiveData()
        customerViewModel = CustomerViewModel(customerRepository)

        Dispatchers.setMain(Dispatchers.Unconfined)
    }

    // --- LiveData --- //

    @Test
    fun getCustomers()
    {
        val value = customerViewModel.customers.getOrAwaitValue()
        assertThat(value, not(nullValue()))
    }

    // --- Data Changes --- //

    @Test
    fun loadAllCustomers()
    {
        val value = customerViewModel.customers.getOrAwaitValue()
        assertThat(value, `is`(DataProvider.localCustomers))
    }

    @Test
    fun loadCustomerById()
    {
        customerViewModel.saveCustomer(DataProvider.customer1)

        val event = customerViewModel.customersSaved.getOrAwaitValue()
        assertThat(event, IsNot(nullValue()))
        assertThat(event.getContentIfNotHandled(), `is`(true))
    }

    @Test
    fun saveCustomer()
    {
        customerViewModel.saveCustomer(DataProvider.customer1)

        val event = customerViewModel.customersSaved.getOrAwaitValue()
        assertThat(event, IsNot(nullValue()))
        assertThat(event.getContentIfNotHandled(), `is`(true))
    }

    @Test
    fun updateCustomer()
    {
        customerViewModel.updateCustomer(DataProvider.customer1)

        val event = customerViewModel.customersSaved.getOrAwaitValue()
        assertThat(event, IsNot(nullValue()))
        assertThat(event.getContentIfNotHandled(), `is`(true))
    }

    // --- Events trigger --- //

    @Test
    fun onClickCustomer()
    {
        val customerId = 2L
        // When user click on a customer
        customerViewModel.onClickCustomer(customerId)

        // Event should be triggered
        val event = customerViewModel.customerClick.getOrAwaitValue()
        val eventData = event.getContentIfNotHandled()
        assertThat(eventData, IsNot(nullValue()))
        assertThat(eventData, `is`(customerId))
    }
}