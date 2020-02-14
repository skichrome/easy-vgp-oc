package com.skichrome.oc.easyvgp.model.local

import androidx.lifecycle.LiveData
import com.skichrome.oc.easyvgp.model.CustomerDataSource
import com.skichrome.oc.easyvgp.model.Results
import com.skichrome.oc.easyvgp.model.Results.Error
import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.local.database.Customer
import com.skichrome.oc.easyvgp.model.local.database.CustomersDao
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LocalCustomerRepository(private val customersDao: CustomersDao, private val dispatcher: CoroutineDispatcher = Dispatchers.IO) :
    CustomerDataSource
{
    override fun loadAllCustomers(): LiveData<List<Customer>> = customersDao.observeCustomers()

    override suspend fun getCustomerById(id: Long): Results<Customer> = withContext(dispatcher) {
        return@withContext try
        {
            Success(customersDao.getCustomerById(id))
        } catch (e: Exception)
        {
            Error(e)
        }
    }

    override suspend fun saveCustomers(customers: Array<Customer>): Results<List<Long>> = withContext(dispatcher) {
        val ids = customersDao.insertIgnore(*customers)
        return@withContext Success(ids)
    }

    override suspend fun saveCustomers(customer: Customer): Results<Long> = withContext(dispatcher) {
        val id = customersDao.insertIgnore(customer)
        return@withContext Success(id)
    }

    override suspend fun updateCustomers(customer: Customer): Results<Int> = withContext(dispatcher) {
        val id = customersDao.update(customer)
        return@withContext Success(id)
    }
}