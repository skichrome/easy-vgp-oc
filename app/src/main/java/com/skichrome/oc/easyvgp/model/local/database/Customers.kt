package com.skichrome.oc.easyvgp.model.local.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Entity
data class Customers(
    @ColumnInfo(name = "customer_id") @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    @ColumnInfo(name = "first_name") val firstName: String,
    @ColumnInfo(name = "last_name") val lastName: String,
    @ColumnInfo(name = "siret") val siret: String,
    @ColumnInfo(name = "email") val email: String?,
    @ColumnInfo(name = "phone") val phone: Int?,
    @ColumnInfo(name = "mobile_phone") val mobilePhone: Int?,
    @ColumnInfo(name = "address") val address: String,
    @ColumnInfo(name = "post_code") val postCode: Int,
    @ColumnInfo(name = "city") val city: String,
    @ColumnInfo(name = "notes") val notes: String?
)

@Dao
interface CustomersDao : BaseDao<Customers>
{
    @Query("SELECT * FROM Customers")
    fun observeCustomers(): LiveData<List<Customers>>

    @Query("SELECT * FROM Customers")
    suspend fun getAllCustomers(): List<Customers>

    @Query("SELECT * FROM Customers WHERE customer_id == :customerId")
    suspend fun getCustomerById(customerId: Long): Customers
}