package com.skichrome.oc.easyvgp.model.remote

import androidx.lifecycle.LiveData
import com.skichrome.oc.easyvgp.model.Results
import com.skichrome.oc.easyvgp.model.Results.Error
import com.skichrome.oc.easyvgp.model.local.base.CustomerSource
import com.skichrome.oc.easyvgp.model.local.database.Customer
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CustomerRemoteRepository(private val dispatcher: CoroutineDispatcher = Dispatchers.IO) :
    CustomerSource
{
    override suspend fun saveCustomers(customers: Array<Customer>): Results<List<Long>> = withContext(dispatcher) {
        return@withContext Results.Success(listOf(customers.size.toLong()))
    }

    // Not Implemented for online sync

    override fun loadAllCustomers(): LiveData<List<Customer>> = throw IllegalStateException("Not implemented")

    override suspend fun getCustomerById(id: Long): Results<Customer> = Error(IllegalStateException("Not implemented"))

    override suspend fun saveCustomers(customer: Customer): Results<Long> = Error(IllegalStateException("Not implemented"))

    override suspend fun updateCustomers(customer: Customer): Results<Int> = Error(IllegalStateException("Not implemented"))
}