package com.skichrome.oc.easyvgp.model

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.skichrome.oc.easyvgp.getOrAwaitValue
import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.source.DataProvider
import com.skichrome.oc.easyvgp.model.source.FakeCustomerDataSource
import com.skichrome.oc.easyvgp.model.source.FakeNetManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.core.IsEqual
import org.hamcrest.core.IsNot
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

    private lateinit var customersLocalDataSource: FakeCustomerDataSource
    private lateinit var customersRemoteDataSource: FakeCustomerDataSource
    private lateinit var netManager: FakeNetManager

    private lateinit var customerRepository: DefaultCustomerRepository

    @Before
    fun initRepo()
    {
        netManager = FakeNetManager(isFakeConnected = false)

        customersLocalDataSource = FakeCustomerDataSource(DataProvider.localCustomersMap)

        customersRemoteDataSource = FakeCustomerDataSource(DataProvider.remoteCustomersMap)

        customersLocalDataSource.refresh()
        customersRemoteDataSource.refresh()

        customerRepository = DefaultCustomerRepository(netManager, customersLocalDataSource, customersRemoteDataSource)
    }

    @Test
    fun getAllCustomers_requestAllCustomersFromDataSource_OfflineMode() = runBlockingTest {
        netManager.setIsFakeConnected(false)

        val customers = customerRepository.getAllCustomers().getOrAwaitValue()
        assertThat(customers, IsEqual(DataProvider.localCustomers))
    }

    @Test
    fun getAllCustomers_requestAllCustomersFromDataSource_OnlineMode() = runBlockingTest {
        netManager.setIsFakeConnected(true)

        val customers = customerRepository.getAllCustomers().getOrAwaitValue()
        assertThat(customers, IsEqual(DataProvider.localCustomers))
    }

    @Test
    fun getCustomerById_returnCustomerFromItsId() = runBlockingTest {
        netManager.setIsFakeConnected(false)

        val customers = customerRepository.getCustomerById(DataProvider.customer3Id) as Success
        assertThat(customers.data, IsEqual(DataProvider.customer3))
    }

    @Test
    fun updateCustomers_InsertCustomerAndUpdateIt_ShouldReturnUpdatedCustomer() = runBlockingTest {
        netManager.setIsFakeConnected(false)

        val result = customerRepository.updateCustomers(DataProvider.customer1Edit) as Success

        assertThat(result.data, IsNot(nullValue()))
        assertThat(result.data, `is`(1))
    }
}