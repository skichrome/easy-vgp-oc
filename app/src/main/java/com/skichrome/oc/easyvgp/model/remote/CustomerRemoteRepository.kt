package com.skichrome.oc.easyvgp.model.remote

import com.skichrome.oc.easyvgp.model.Results
import com.skichrome.oc.easyvgp.model.Results.Error
import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.local.CustomersDataSource
import com.skichrome.oc.easyvgp.model.local.database.Customers
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CustomerRemoteRepository(private val dispatcher: CoroutineDispatcher = Dispatchers.IO) : CustomersDataSource
{
    override suspend fun loadAllCustomers(): Results<List<Customers>> = withContext(dispatcher) {
        return@withContext try
        {
            Success(emptyList<Customers>())
        } catch (e: Exception)
        {
            Error(e)
        }
    }

    override suspend fun getCustomerById(id: Long): Results<Customers> = Error(Exception("Not implemented"))
}