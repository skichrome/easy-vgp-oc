package com.skichrome.oc.easyvgp.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.skichrome.oc.easyvgp.getOrAwaitValue
import com.skichrome.oc.easyvgp.model.local.database.Customers
import com.skichrome.oc.easyvgp.viewmodel.source.FakeCustomerViewModelRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
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

    private val customer1Id = 1L
    private val customer1 = Customers(
        id = customer1Id,
        firstName = "first name $customer1Id",
        lastName = "last name $customer1Id",
        siret = 12345678910121L,
        postCode = 12345,
        address = "Address $customer1Id",
        city = "City$customer1Id",
        email = "test@email.com",
        mobilePhone = ("0101010110").toInt(),
        notes = "This is a note $customer1Id",
        phone = ("0404040404").toInt()
    )
    private val customer1Edit = Customers(
        id = customer1Id,
        firstName = "first edited name $customer1Id",
        lastName = "last edited name $customer1Id",
        siret = 12345678910121L,
        postCode = 12345,
        address = "edited Address $customer1Id",
        city = "edited City$customer1Id",
        email = "test2@email.com",
        mobilePhone = ("0202020220").toInt(),
        notes = "This is an edited note $customer1Id",
        phone = ("0505050505").toInt()
    )

    private val customer2Id = 2L
    private val customer2 = Customers(
        id = customer2Id,
        firstName = "first name $customer2Id",
        lastName = "last name $customer2Id",
        siret = 12345678910121L,
        postCode = 12345,
        address = "Address $customer2Id",
        city = "City$customer2Id",
        email = "test@email.com",
        mobilePhone = ("0101010110").toInt(),
        notes = "This is a note $customer2Id",
        phone = ("0404040404").toInt()
    )

    private val customer3Id = 3L
    private val customer3 = Customers(
        id = customer3Id,
        firstName = "first name $customer3Id",
        lastName = "last name $customer3Id",
        siret = 12345678910121L,
        postCode = 12345,
        address = "Address $customer3Id",
        city = "City$customer3Id",
        email = "test@email.com",
        mobilePhone = ("0101010110").toInt(),
        notes = "This is a note $customer3Id",
        phone = ("0404040404").toInt()
    )

    private val customersList = arrayOf(customer1, customer2, customer3)

    // =================================
    //              Methods
    // =================================

    @Before
    fun initTests() = runBlockingTest {
        customerRepository = FakeCustomerViewModelRepository()
        customerRepository.saveCustomers(customersList)
        customerRepository.refreshLiveData()
        customerViewModel = CustomerViewModel(customerRepository)

        Dispatchers.setMain(Dispatchers.Unconfined)
    }

    @Test
    fun getCustomers()
    {
        val value = customerViewModel.customers.getOrAwaitValue()
        assertThat(value, CoreMatchers.not(CoreMatchers.nullValue()))
    }

    @Test
    fun loadAllCustomers()
    {
        val value = customerViewModel.customers.getOrAwaitValue()
        assertThat(value, `is`(customersList.toList()))
    }
}