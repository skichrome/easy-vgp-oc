package com.skichrome.oc.easyvgp.model.local.database

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class CustomerDaoTest
{
    // =================================
    //              Fields
    // =================================

    private val customer1 = Customers(id = 0, name = "toto", siret = "sdergdrgirfhq7")
    private val customer2 = Customers(id = 1, name = "titi", siret = "gfsfg4g6rsg4rg")
    private val customer3 = Customers(id = 2, name = "tata", siret = "efef4efe5fefef")

    private val customer1Edit = Customers(id = 0, name = "toto - edited", siret = "sdergdrgirfhq7-notthesame")

    private val customerList = arrayOf(customer1, customer2, customer3)
    private val customerListEdited = arrayOf(customer1Edit, customer2, customer3)

    private lateinit var db: AppDatabase
    private lateinit var customerDao: CustomersDao

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
    fun insertIgnoreCustomer_ReturnSameCustomer() = runBlockingTest {
        customerDao.insertIgnore(customer1)

        val customers = customerDao.getAllCustomers()
        assertThat(customers, `is`(listOf(customer1)))
    }

    @Test
    @Throws(Exception::class)
    fun insertIgnoreCustomerList_ReturnSameCustomerList() = runBlockingTest {
        customerDao.insertIgnore(*customerList)

        val customers = customerDao.getAllCustomers()
        assertThat(customers, `is`(customerList.toList()))
    }

    @Test
    @Throws(Exception::class)
    fun insertIgnoreTwoTimesSameCustomer_ReturnNotModifiedCustomer() = runBlockingTest {
        customerDao.insertIgnore(customer1)
        customerDao.insertIgnore(customer1Edit)

        val customers = customerDao.getAllCustomers()
        assertThat(customers, `is`(listOf(customer1)))
    }

    @Test
    @Throws(Exception::class)
    fun insertReplaceCustomer_ReturnSameCustomer() = runBlockingTest {
        customerDao.insertReplace(customer1)

        val customers = customerDao.getAllCustomers()
        assertThat(customers, `is`(listOf(customer1)))
    }

    @Test
    @Throws(Exception::class)
    fun insertReplaceCustomerList_ReturnSameCustomerList() = runBlockingTest {
        customerDao.insertReplace(*customerList)

        val customers = customerDao.getAllCustomers()
        assertThat(customers, `is`(customerList.toList()))
    }

    @Test
    @Throws(Exception::class)
    fun insertReplaceTwoTimesSameCustomer_ReturnModifiedCustomer() = runBlockingTest {
        customerDao.insertReplace(customer1)
        customerDao.insertReplace(customer1Edit)

        val customers = customerDao.getAllCustomers()
        assertThat(customers, `is`(listOf(customer1Edit)))
    }

    // --- UPDATE Tests --- //

    @Test
    @Throws(Exception::class)
    fun updateCustomer_ReturnModifiedCustomer() = runBlockingTest {
        customerDao.insertIgnore(customer1)
        customerDao.update(customer1Edit)

        val customers = customerDao.getAllCustomers()
        assertThat(customers, `is`(listOf(customer1Edit)))
    }

    @Test
    @Throws(Exception::class)
    fun updateCustomerList_ReturnUpdatedCustomerList() = runBlockingTest {
        customerDao.insertIgnore(*customerList)
        customerDao.update(*customerListEdited)

        val customers = customerDao.getAllCustomers()
        assertThat(customers, `is`(customerListEdited.toList()))
    }

    // --- QUERY Tests --- //

    @Test
    @Throws(Exception::class)
    fun insertCustomersList_SelectOneByID() = runBlockingTest {
        customerDao.insertIgnore(*customerList)

        val customer = customerDao.getCustomerById(customer1.id)
        assertThat(customer, `is`(customer1))
    }

    @Test
    @Throws(Exception::class)
    fun insertCustomerListAndRetriveAll() = runBlockingTest {
        customerDao.insertIgnore(*customerListEdited)

        val customers = customerDao.getAllCustomers()
        assertThat(customers, `is`(customerListEdited.toList()))
    }

    // --- DELETE Tests --- //

    @Test
    @Throws(Exception::class)
    fun insertCustomerList_deleteOne_AssertCustomerIsDeleted() = runBlockingTest {
        customerDao.insertIgnore(*customerList)
        customerDao.delete(customer1)

        val customers = customerDao.getAllCustomers()
        assertThat(customers, `is`(listOf(customer2, customer3)))
    }

    @Test
    @Throws(Exception::class)
    fun insertCustomerList_deleteTwoCustomers_AssertCustomersAreDeleted() = runBlockingTest {
        customerDao.insertIgnore(*customerList)
        customerDao.delete(customer1, customer2)

        val customers = customerDao.getAllCustomers()
        assertThat(customers, `is`(listOf(customer3)))
    }
}