package com.skichrome.oc.easyvgp.model.local

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
    override suspend fun loadAllCustomers(): Results<List<Customers>> = withContext(dispatcher) {
        return@withContext try
        {
            Success(customersDao.getAllCustomers())
        } catch (e: Exception)
        {
            Error(e)
        }
    }

    override suspend fun getCustomerById(id: Long): Results<Customers> = withContext(dispatcher) {
        return@withContext try
        {
            Success(customersDao.getCustomerById(id))
        } catch (e: Exception)
        {
            Error(e)
        }
    }
}