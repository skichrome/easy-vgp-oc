package com.skichrome.oc.easyvgp.model

import androidx.lifecycle.LiveData
import com.skichrome.oc.easyvgp.model.local.database.Customers

interface CustomersRepository
{
    fun getAllCustomers(): LiveData<List<Customers>>

    suspend fun getCustomerById(id: Long): Results<Customers>

    // --- Save --- //

    suspend fun saveCustomers(customer: Customers): Results<Long>

    suspend fun saveCustomers(customers: Array<Customers>): Results<List<Long>>

    // --- Update --- //

    suspend fun updateCustomers(customer: Customers): Results<Int>
}