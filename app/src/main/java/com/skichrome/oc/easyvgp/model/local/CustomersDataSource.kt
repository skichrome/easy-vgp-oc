package com.skichrome.oc.easyvgp.model.local

import com.skichrome.oc.easyvgp.model.Results
import com.skichrome.oc.easyvgp.model.local.database.Customers

interface CustomersDataSource
{
    suspend fun loadAllCustomers(): Results<List<Customers>>

    suspend fun getCustomerById(id: Long): Results<Customers>
}