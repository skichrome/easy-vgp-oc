package com.skichrome.oc.easyvgp.model.local

import androidx.lifecycle.LiveData
import com.skichrome.oc.easyvgp.model.CustomersDataSource
import com.skichrome.oc.easyvgp.model.Results
import com.skichrome.oc.easyvgp.model.Results.Error
import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.local.database.Customers
import com.skichrome.oc.easyvgp.model.local.database.CustomersDao
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CustomerLocalRepository(private val customersDao: CustomersDao, private val dispatcher: CoroutineDispatcher = Dispatchers.IO) :
    CustomersDataSource
{
    override fun loadAllCustomers(): LiveData<List<Customers>> = customersDao.observeCustomers()

    override suspend fun getCustomerById(id: Long): Results<Customers> = withContext(dispatcher) {
        return@withContext try
        {
            Success(customersDao.getCustomerById(id))
        } catch (e: Exception)
        {
            Error(e)
        }
    }

    override suspend fun saveCustomers(customers: Array<Customers>): Results<List<Long>> = withContext(dispatcher) {
        val ids = customersDao.insertIgnore(*customers)
        return@withContext Success(ids)
    }

    override suspend fun saveCustomers(customer: Customers): Results<Long> = withContext(dispatcher) {
        val id = customersDao.insertIgnore(customer)
        return@withContext Success(id)
    }

    override suspend fun updateCustomers(customer: Customers): Results<Int> = withContext(dispatcher) {
        val id = customersDao.update(customer)
        return@withContext Success(id)
    }
}