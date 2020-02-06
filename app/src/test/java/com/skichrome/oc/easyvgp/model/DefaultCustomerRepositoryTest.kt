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
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
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

        val customers = customerRepository.getCustomerById(customer3Id)

        if (customers is Success)
            assertThat(customers.data, IsEqual(customer3))
        else
        {
            System.err.println(" ERROR : ${(customers as Results.Error).exception.localizedMessage}}")
            assertThat("Check error exception message", customers, `is`(nullValue()))
        }
    }
}