package com.skichrome.oc.easyvgp.model.local.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query

@Dao
interface CustomersDao : BaseDao<Customers>
{
    @Query("SELECT * FROM Customers")
    fun observeCustomers(): LiveData<List<Customers>>

    @Query("SELECT * FROM Customers")
    suspend fun getAllCustomers(): List<Customers>

    @Query("SELECT * FROM Customers WHERE id == :customerId")
    suspend fun getCustomerById(customerId: Long): Customers
}