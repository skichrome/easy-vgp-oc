package com.skichrome.oc.easyvgp.model

import com.skichrome.oc.easyvgp.model.local.database.Customers

interface CustomerRepository
{
    suspend fun getAllCustomers(): Results<List<Customers>>

    suspend fun getCustomerById(id: Long): Results<Customers>
}