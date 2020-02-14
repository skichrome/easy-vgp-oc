package com.skichrome.oc.easyvgp.model.local.database

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
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

    private val customer1Id = 1L
    private val customer1 = Customer(
        id = customer1Id,
        firstName = "first name $customer1Id",
        lastName = "last name $customer1Id",
        siret = "12345678910121L",
        postCode = 12345,
        address = "Address $customer1Id",
        city = "City$customer1Id",
        email = "test@email.com",
        mobilePhone = ("0101010110").toInt(),
        notes = "This is a note $customer1Id",
        phone = ("0404040404").toInt()
    )
    private val customer1Edit = Customer(
        id = customer1Id,
        firstName = "first edited name $customer1Id",
        lastName = "last edited name $customer1Id",
        siret = "12345678910121L",
        postCode = 12345,
        address = "edited Address $customer1Id",
        city = "edited City$customer1Id",
        email = "test2@email.com",
        mobilePhone = ("0202020220").toInt(),
        notes = "This is an edited note $customer1Id",
        phone = ("0505050505").toInt()
    )

    private val customer2Id = 2L
    private val customer2 = Customer(
        id = customer2Id,
        firstName = "first name $customer2Id",
        lastName = "last name $customer2Id",
        siret = "12345678910121L",
        postCode = 12345,
        address = "Address $customer2Id",
        city = "City$customer2Id",
        email = "test@email.com",
        mobilePhone = ("0101010110").toInt(),
        notes = "This is a note $customer2Id",
        phone = ("0404040404").toInt()
    )

    private val customer3Id = 3L
    private val customer3 = Customer(
        id = customer3Id,
        firstName = "first name $customer3Id",
        lastName = "last name $customer3Id",
        siret = "12345678910121L",
        postCode = 12345,
        address = "Address $customer3Id",
        city = "City$customer3Id",
        email = "test@email.com",
        mobilePhone = ("0101010110").toInt(),
        notes = "This is a note $customer3Id",
        phone = ("0404040404").toInt()
    )

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
    fun insertIgnoreCustomer_ReturnSameCustomer() = runBlocking {
        customerDao.insertIgnore(customer1)

        val customers = customerDao.getAllCustomers()
        assertThat(customers, `is`(listOf(customer1)))
    }

    @Test
    @Throws(Exception::class)
    fun insertIgnoreCustomerList_ReturnSameCustomerList() = runBlocking {
        customerDao.insertIgnore(*customerList)

        val customers = customerDao.getAllCustomers()
        assertThat(customers, `is`(customerList.toList()))
    }

    @Test
    @Throws(Exception::class)
    fun insertIgnoreTwoTimesSameCustomer_ReturnNotModifiedCustomer() = runBlocking {
        customerDao.insertIgnore(customer1)
        customerDao.insertIgnore(customer1Edit)

        val customers = customerDao.getAllCustomers()
        assertThat(customers, `is`(listOf(customer1)))
    }

    @Test
    @Throws(Exception::class)
    fun insertReplaceCustomer_ReturnSameCustomer() = runBlocking {
        customerDao.insertReplace(customer1)

        val customers = customerDao.getAllCustomers()
        assertThat(customers, `is`(listOf(customer1)))
    }

    @Test
    @Throws(Exception::class)
    fun insertReplaceCustomerList_ReturnSameCustomerList() = runBlocking {
        customerDao.insertReplace(*customerList)

        val customers = customerDao.getAllCustomers()
        assertThat(customers, `is`(customerList.toList()))
    }

    @Test
    @Throws(Exception::class)
    fun insertReplaceTwoTimesSameCustomer_ReturnModifiedCustomer() = runBlocking {
        customerDao.insertReplace(customer1)
        customerDao.insertReplace(customer1Edit)

        val customers = customerDao.getAllCustomers()
        assertThat(customers, `is`(listOf(customer1Edit)))
    }

    // --- UPDATE Tests --- //

    @Test
    @Throws(Exception::class)
    fun updateCustomer_ReturnModifiedCustomer() = runBlocking {
        customerDao.insertIgnore(customer1)
        customerDao.update(customer1Edit)

        val customers = customerDao.getAllCustomers()
        assertThat(customers, `is`(listOf(customer1Edit)))
    }

    @Test
    @Throws(Exception::class)
    fun updateCustomerList_ReturnUpdatedCustomerList() = runBlocking {
        customerDao.insertIgnore(*customerList)
        customerDao.update(*customerListEdited)

        val customers = customerDao.getAllCustomers()
        assertThat(customers, `is`(customerListEdited.toList()))
    }

    // --- QUERY Tests --- //

    @Test
    @Throws(Exception::class)
    fun insertCustomersList_SelectOneByID() = runBlocking {
        customerDao.insertIgnore(*customerList)

        val customer = customerDao.getCustomerById(customer1.id)
        assertThat(customer, `is`(customer1))
    }

    @Test
    @Throws(Exception::class)
    fun insertCustomerListAndRetriveAll() = runBlocking {
        customerDao.insertIgnore(*customerListEdited)

        val customers = customerDao.getAllCustomers()
        assertThat(customers, `is`(customerListEdited.toList()))
    }

    // --- DELETE Tests --- //

    @Test
    @Throws(Exception::class)
    fun insertCustomerList_deleteOne_AssertCustomerIsDeleted() = runBlocking {
        customerDao.insertIgnore(*customerList)
        customerDao.delete(customer1)

        val customers = customerDao.getAllCustomers()
        assertThat(customers, `is`(listOf(customer2, customer3)))
    }

    @Test
    @Throws(Exception::class)
    fun insertCustomerList_deleteTwoCustomers_AssertCustomersAreDeleted() = runBlocking {
        customerDao.insertIgnore(*customerList)
        customerDao.delete(customer1, customer2)

        val customers = customerDao.getAllCustomers()
        assertThat(customers, `is`(listOf(customer3)))
    }
}