package com.skichrome.oc.easyvgp.model.local

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.skichrome.oc.easyvgp.getOrAwaitValue
import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.local.base.CustomerSource
import com.skichrome.oc.easyvgp.model.local.database.AppDatabase
import com.skichrome.oc.easyvgp.model.local.database.CustomerDao
import com.skichrome.oc.easyvgp.model.source.DataProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual
import org.hamcrest.core.IsNot
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class LocalCustomerSourceTest
{

    // =================================
    //              Fields
    // =================================

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: AppDatabase
    private lateinit var customerDao: CustomerDao
    private lateinit var customerSource: CustomerSource

    // =================================
    //              Methods
    // =================================

    // --- Configuration --- //

    @Before
    fun setUp()
    {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        customerDao = database.customersDao()
        customerSource = LocalCustomerSource(customerDao = customerDao, dispatchers = Dispatchers.Main)
    }

    @After
    fun tearDown() = database.close()

    // --- Tests --- //

    @Test
    fun loadAllCustomers() = runBlocking {
        // Insert data with database DAO
        val customers = DataProvider.localCustomers
        customerDao.insertReplace(*customers.toTypedArray())

        // Observe results from db with source
        val result = customerSource.loadAllCustomers().getOrAwaitValue()

        // Result must be equal to inserted list
        assertThat(result, IsNot(nullValue()))
        assertThat(result.size, `is`(customers.size))
        assertThat(result, IsEqual(customers))
    }

    @Test
    fun getCustomerById() = runBlocking {
        // Insert data with database DAO
        val customer = DataProvider.customer1
        customerDao.insertReplace(customer)

        // Get customer with source
        val result = customerSource.getCustomerById(customer.id)

        // Output must be equal to input
        assertThat(result, IsNot(nullValue()))
        assertThat(result, instanceOf(Success::class.java))
        assertThat((result as Success).data, IsEqual(customer))
    }

    @Test
    fun saveCustomers_insertCustomersList() = runBlocking {
        // Insert data with source
        val customers = DataProvider.localCustomers
        customerSource.saveCustomers(customers.toTypedArray())

        // Get customer with database DAO
        val result = customerDao.getAllCustomers()

        // Output must be equal to input
        assertThat(result, IsNot(nullValue()))
        assertThat(result.size, `is`(customers.size))
        assertThat(result, IsEqual(customers))
    }

    @Test
    fun saveCustomers_insertOneCustomer() = runBlocking {
        // Insert data with source
        val customers = listOf(DataProvider.customer1)
        customerSource.saveCustomers(customers.first())

        // Get customer with database DAO
        val result = customerDao.getAllCustomers()

        // Output must be equal to input
        assertThat(result, IsNot(nullValue()))
        assertThat(result.size, `is`(customers.size))
        assertThat(result, IsEqual(customers))
    }

    @Test
    fun updateCustomers() = runBlocking {
        // Insert data with database DAO
        customerDao.insertReplace(DataProvider.customer1)

        // Update data with source
        val customerToUpdate = DataProvider.customer1Edit
        customerSource.updateCustomers(customerToUpdate)

        // Get customer with database DAO
        val result = customerDao.getCustomerById(customerToUpdate.id)

        // Output must be equal to input
        assertThat(result, IsNot(nullValue()))
        assertThat(result, IsEqual(customerToUpdate))
    }
}