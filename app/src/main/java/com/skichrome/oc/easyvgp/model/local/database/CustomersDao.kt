package com.skichrome.oc.easyvgp.model.local.database

import androidx.room.Dao
import androidx.room.Query

@Dao
interface CustomersDao : BaseDao<Customers>
{
    @Query("SELECT * FROM Customers")
    fun getAllCustomers(): List<Customers>

    @Query("SELECT * FROM Customers WHERE id == :customerId")
    fun getCustomerById(customerId: Long): Customers
}