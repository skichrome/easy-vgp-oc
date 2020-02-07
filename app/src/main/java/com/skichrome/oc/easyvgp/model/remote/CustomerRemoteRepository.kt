package com.skichrome.oc.easyvgp.model.remote

import androidx.lifecycle.LiveData
import com.skichrome.oc.easyvgp.model.CustomersDataSource
import com.skichrome.oc.easyvgp.model.Results
import com.skichrome.oc.easyvgp.model.Results.Error
import com.skichrome.oc.easyvgp.model.local.database.Customers
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CustomerRemoteRepository(private val dispatcher: CoroutineDispatcher = Dispatchers.IO) : CustomersDataSource
{
    override suspend fun saveCustomers(customers: Array<Customers>): Results<List<Long>> = withContext(dispatcher) {
        return@withContext Results.Success(listOf(customers.size.toLong()))
    }

    // Not Implemented for online sync

    override fun loadAllCustomers(): LiveData<List<Customers>> = throw IllegalStateException("Not implemented")

    override suspend fun getCustomerById(id: Long): Results<Customers> = Error(IllegalStateException("Not implemented"))

    override suspend fun saveCustomers(customer: Customers): Results<Long> = Error(IllegalStateException("Not implemented"))

    override suspend fun updateCustomers(customer: Customers): Results<Int> = Error(IllegalStateException("Not implemented"))
}