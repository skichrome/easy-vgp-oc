package com.skichrome.oc.easyvgp.model

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.skichrome.oc.easyvgp.androidmanagers.NetManager
import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.local.database.Customers
import com.skichrome.oc.easyvgp.model.source.FakeCustomersDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class DefaultCustomerRepositoryTest
{
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private val customer1 = Customers(id = 0, name = "toto", siret = "sdergdrgirfhq7")
    private val customer2 = Customers(id = 1, name = "titi", siret = "gfsfg4g6rsg4rg")
    private val customer3 = Customers(id = 2, name = "tata", siret = "efef4efe5fefef")

    private val remoteCustomers = listOf(customer1, customer2).sortedBy { it.id }
    private val localCustomers = listOf(customer3).sortedBy { it.id }

    private lateinit var customersLocalDataSource: FakeCustomersDataSource
    private lateinit var customersRemoteDataSource: FakeCustomersDataSource
    private lateinit var netManager: NetManager

    private lateinit var customerRepository: DefaultCustomerRepository

    @Before
    fun initRepo()
    {
        netManager = NetManager(ApplicationProvider.getApplicationContext())

        customersLocalDataSource = FakeCustomersDataSource(localCustomers.toMutableList())
        customersRemoteDataSource = FakeCustomersDataSource(remoteCustomers.toMutableList())

        customerRepository = DefaultCustomerRepository(netManager, customersLocalDataSource, customersRemoteDataSource)
    }

    @Test
    fun getAllCustomers_requestAllCustomersFromDataSource() = runBlockingTest {
        val customers = customerRepository.getAllCustomers() as Success
        assertThat(customers.data, IsEqual(localCustomers))
    }

    @Test
    fun getCustomerById_returnCustomerFromItsId() = runBlockingTest {
        val customers = customerRepository.getCustomerById(2) as Success
        assertThat(customers.data, IsEqual(customer3))
    }
}