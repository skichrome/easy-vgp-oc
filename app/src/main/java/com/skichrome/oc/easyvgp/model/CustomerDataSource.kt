package com.skichrome.oc.easyvgp.model

import androidx.lifecycle.LiveData
import com.skichrome.oc.easyvgp.model.local.database.Customer

interface CustomerDataSource
{
    fun loadAllCustomers(): LiveData<List<Customer>>

    suspend fun getCustomerById(id: Long): Results<Customer>

    // --- Save --- //

    suspend fun saveCustomers(customer: Customer): Results<Long>

    suspend fun saveCustomers(customers: Array<Customer>): Results<List<Long>>

    // --- Update --- //

    suspend fun updateCustomers(customer: Customer): Results<Int>
}