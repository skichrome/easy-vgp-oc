package com.skichrome.oc.easyvgp.model

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.skichrome.oc.easyvgp.getOrAwaitValue
import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.local.database.Customers
import com.skichrome.oc.easyvgp.model.source.FakeCustomersDataSource
import com.skichrome.oc.easyvgp.model.source.FakeNetManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
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
    private lateinit var netManager: FakeNetManager

    private lateinit var customerRepository: DefaultCustomerRepository

    @Before
    fun initRepo()
    {
        netManager = FakeNetManager(isFakeConnected = false)

        customersLocalDataSource = FakeCustomersDataSource(localCustomers.toMutableList())
        customersRemoteDataSource = FakeCustomersDataSource(remoteCustomers.toMutableList())

        customersLocalDataSource.refresh()
        customersRemoteDataSource.refresh()

        customerRepository = DefaultCustomerRepository(netManager, customersLocalDataSource, customersRemoteDataSource)
    }

    @Test
    fun getAllCustomers_requestAllCustomersFromDataSource_OfflineMode() = runBlockingTest {
        netManager.setIsFakeConnected(false)

        val customers = customerRepository.getAllCustomers().getOrAwaitValue()
        assertThat(customers, IsEqual(localCustomers))
    }

    @Test
    fun getAllCustomers_requestAllCustomersFromDataSource_OnlineMode() = runBlockingTest {
        netManager.setIsFakeConnected(true)

        val customers = customerRepository.getAllCustomers().getOrAwaitValue()
        assertThat(customers, IsEqual(localCustomers))
    }

    @Test
    fun getCustomerById_returnCustomerFromItsId() = runBlockingTest {
        netManager.setIsFakeConnected(false)

        val customers = customerRepository.getCustomerById(2) as Success
        assertThat(customers.data, IsEqual(customer3))
    }
}