package com.skichrome.oc.easyvgp.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.skichrome.oc.easyvgp.getOrAwaitValue
import com.skichrome.oc.easyvgp.model.local.database.Customers
import com.skichrome.oc.easyvgp.viewmodel.source.FakeCustomerViewModelRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.setMain
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
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
    private lateinit var customerRepository: FakeCustomerViewModelRepository

    private val customer1 = Customers(id = 0, name = "toto", siret = "sdergdrgirfhq7")
    private val customer2 = Customers(id = 1, name = "titi", siret = "gfsfg4g6rsg4rg")
    private val customer3 = Customers(id = 2, name = "tata", siret = "efef4efe5fefef")

    private val customersList = arrayOf(customer1, customer2, customer3)

    // =================================
    //              Methods
    // =================================

    @Before
    fun initTests()
    {
        customerRepository = FakeCustomerViewModelRepository()

        customerRepository.addCustomers(*customersList)

        customerViewModel = CustomerViewModel(customerRepository)

        Dispatchers.setMain(Dispatchers.Unconfined)
    }

    @Test
    fun getCustomers()
    {
        customerViewModel.loadAllCustomers()
        val value = customerViewModel.customers.getOrAwaitValue()
        assertThat(value, CoreMatchers.not(CoreMatchers.nullValue()))
    }

    @Test
    fun loadAllCustomers()
    {
        customerViewModel.loadAllCustomers()
        val value = customerViewModel.customers.getOrAwaitValue()
        assertThat(value, `is`(customersList.toList()))
    }
}