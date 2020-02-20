package com.skichrome.oc.easyvgp.model.local

import androidx.lifecycle.LiveData
import com.skichrome.oc.easyvgp.model.CustomerSource
import com.skichrome.oc.easyvgp.model.Results
import com.skichrome.oc.easyvgp.model.Results.Error
import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.local.database.Customer
import com.skichrome.oc.easyvgp.model.local.database.CustomersDao
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LocalCustomerSource(private val customersDao: CustomersDao, private val dispatchers: CoroutineDispatcher = Dispatchers.IO) :
    CustomerSource
{
    override fun loadAllCustomers(): LiveData<List<Customer>> = customersDao.observeCustomers()

    override suspend fun getCustomerById(id: Long): Results<Customer> = withContext(dispatchers) {
        return@withContext try
        {
            Success(customersDao.getCustomerById(id))
        } catch (e: Exception)
        {
            Error(e)
        }
    }

    override suspend fun saveCustomers(customers: Array<Customer>): Results<List<Long>> = withContext(dispatchers) {
        val ids = customersDao.insertIgnore(*customers)
        return@withContext Success(ids)
    }

    override suspend fun saveCustomers(customer: Customer): Results<Long> = withContext(dispatchers) {
        val id = customersDao.insertIgnore(customer)
        return@withContext Success(id)
    }

    override suspend fun updateCustomers(customer: Customer): Results<Int> = withContext(dispatchers) {
        val id = customersDao.update(customer)
        return@withContext Success(id)
    }
}