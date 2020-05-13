package com.skichrome.oc.easyvgp.model.local.database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.skichrome.oc.easyvgp.getOrAwaitValueFromAndroidTests
import com.skichrome.oc.easyvgp.model.AndroidDataProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual
import org.hamcrest.core.IsNot
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class CustomerDaoTest
{
    // =================================
    //              Fields
    // =================================

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var db: AppDatabase
    private lateinit var customerDao: CustomerDao

    // =================================
    //              Methods
    // =================================

    // --- Initialisation and post operations --- //

    @Before
    fun initDatabase()
    {
        db = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), AppDatabase::class.java).build()
        customerDao = db.customersDao()
    }

    @After
    @Throws(Exception::class)
    fun closeDatabase() = db.close()

    // --- INSERT Tests --- //

    @Test
    @Throws(Exception::class)
    fun insertIgnoreCustomer_ReturnSameCustomer() = runBlocking {
        val customer = AndroidDataProvider.customer1
        customerDao.insertIgnore(customer)

        val customers = customerDao.getAllCustomers()
        assertThat(customers, `is`(listOf(customer)))
    }

    @Test
    @Throws(Exception::class)
    fun insertIgnoreCustomerList_ReturnSameCustomerList() = runBlocking {
        val customers = AndroidDataProvider.customerList
        customerDao.insertIgnore(*customers.toTypedArray())

        val customersResult = customerDao.getAllCustomers()
        assertThat(customersResult, `is`(customers))
    }

    @Test
    @Throws(Exception::class)
    fun insertIgnoreTwoTimesSameCustomer_ReturnNotModifiedCustomer() = runBlocking {
        val customer = AndroidDataProvider.customer1
        val customerEdit = AndroidDataProvider.customer1Edit

        customerDao.insertIgnore(customer)
        customerDao.insertIgnore(customerEdit)

        val customers = customerDao.getAllCustomers()
        assertThat(customers, `is`(listOf(customer)))
    }

    @Test
    @Throws(Exception::class)
    fun insertReplaceCustomerList_ReturnSameCustomerList() = runBlocking {
        val customers = AndroidDataProvider.customerList
        customerDao.insertReplace(*customers.toTypedArray())

        val customersResult = customerDao.getAllCustomers()
        assertThat(customersResult, `is`(customers))
    }

    @Test
    @Throws(Exception::class)
    fun insertReplaceTwoTimesSameCustomer_ReturnModifiedCustomer() = runBlocking {
        val customer = AndroidDataProvider.customer1
        val customerEdit = AndroidDataProvider.customer1Edit

        customerDao.insertReplace(customer)
        customerDao.insertReplace(customerEdit)

        val customers = customerDao.getAllCustomers()
        assertThat(customers, `is`(listOf(customerEdit)))
    }

    // --- UPDATE Tests --- //

    @Test
    @Throws(Exception::class)
    fun updateCustomer_ReturnModifiedCustomer() = runBlocking {
        val customer = AndroidDataProvider.customer1
        val customerEdit = AndroidDataProvider.customer1Edit

        customerDao.insertIgnore(customer)
        customerDao.update(customerEdit)

        val customers = customerDao.getAllCustomers()
        assertThat(customers, `is`(listOf(customerEdit)))
    }

    @Test
    @Throws(Exception::class)
    fun updateCustomerList_ReturnUpdatedCustomerList() = runBlocking {
        val customers = AndroidDataProvider.customerList
        val customersEdit = AndroidDataProvider.customerListEdit

        customerDao.insertIgnore(*customers.toTypedArray())
        customerDao.update(*customersEdit.toTypedArray())

        val customersResult = customerDao.getAllCustomers()
        assertThat(customersResult, `is`(customersEdit))
    }

    // --- QUERY Tests --- //

    @Test
    @Throws(Exception::class)
    fun observeCustomers_shouldReturnEmptyList() = runBlocking {
        val result = customerDao.observeCustomers().getOrAwaitValueFromAndroidTests()

        assertThat(result, IsNot(nullValue()))
        assertThat(result, IsEqual(listOf()))
    }

    @Test
    @Throws(Exception::class)
    fun observeCustomers_shouldReturnCustomersList() = runBlocking {
        val customers = AndroidDataProvider.customerList
        customerDao.insertReplace(*customers.toTypedArray())

        val customerResult = customerDao.observeCustomers().getOrAwaitValueFromAndroidTests()

        assertThat(customerResult, IsNot(nullValue()))
        assertThat(customerResult, IsEqual(customers))
    }

    @Test
    @Throws(Exception::class)
    fun insertCustomersList_SelectOneByID() = runBlocking {
        val customers = AndroidDataProvider.customerList
        val customer = AndroidDataProvider.customer1

        customerDao.insertIgnore(*customers.toTypedArray())

        val customerResult = customerDao.getCustomerById(customer.id)
        assertThat(customerResult, `is`(customer))
    }

    @Test
    @Throws(Exception::class)
    fun insertCustomerListAndRetrieveAll() = runBlocking {
        val customersEdit = AndroidDataProvider.customerListEdit

        customerDao.insertIgnore(*customersEdit.toTypedArray())

        val customersResult = customerDao.getAllCustomers()
        assertThat(customersResult, `is`(customersEdit))
    }

    @Test
    @Throws(Exception::class)
    fun getCustomerEmail_shouldReturnCustomerEmail() = runBlocking {
        val customers = AndroidDataProvider.customerList
        val customer = AndroidDataProvider.customer1

        customerDao.insertIgnore(*customers.toTypedArray())

        val email = customerDao.getCustomerEmail(customerId = customer.id)

        assertThat(email, IsNot(nullValue()))
        assertThat(email, `is`(customer.email))
    }

    // --- DELETE Tests --- //

    @Test
    @Throws(Exception::class)
    fun insertCustomerList_deleteOne_AssertCustomerIsDeleted() = runBlocking {
        val customers = AndroidDataProvider.customerList
        val customer = AndroidDataProvider.customer1

        customerDao.insertIgnore(*customers.toTypedArray())
        customerDao.delete(customer)

        val customersResult = customerDao.getAllCustomers()
        assertThat(customersResult, `is`(customers.dropWhile { customer.id == it.id }))
    }

    @Test
    @Throws(Exception::class)
    fun insertCustomerList_deleteTwoCustomers_AssertCustomersAreDeleted() = runBlocking {
        val customers = AndroidDataProvider.customerList
        val customer1 = AndroidDataProvider.customer1
        val customer2 = AndroidDataProvider.customer1

        customerDao.insertIgnore(*customers.toTypedArray())
        customerDao.delete(customer1, customer2)

        val customersResult = customerDao.getAllCustomers()
        assertThat(customersResult, `is`(customers.dropWhile { it.id == customer1.id || it.id == customer2.id }))
    }
}