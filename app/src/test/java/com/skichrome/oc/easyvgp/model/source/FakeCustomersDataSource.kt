package com.skichrome.oc.easyvgp.model.source

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.skichrome.oc.easyvgp.model.CustomersDataSource
import com.skichrome.oc.easyvgp.model.Results
import com.skichrome.oc.easyvgp.model.Results.Error
import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.local.database.Customers

class FakeCustomersDataSource(private var customersServiceData: LinkedHashMap<Long, Customers> = LinkedHashMap()) :
    CustomersDataSource
{
    // =================================
    //              Fields
    // =================================

    private var observableData = MutableLiveData<List<Customers>>()

    override fun loadAllCustomers(): LiveData<List<Customers>> = observableData

    override suspend fun getCustomerById(id: Long): Results<Customers> =
        customersServiceData.let {
            val foundCustomer: Customers? = it[id]

            foundCustomer?.let { foundCustomerNotNull -> Success(foundCustomerNotNull) }
                ?: Error(Exception("Customer not found"))
        }

    override suspend fun saveCustomers(customers: Array<Customers>): Results<List<Long>>
    {
        customers.forEach { customersServiceData[it.id] = it }
        return Success(listOf(customers.size.toLong()))
    }

    override suspend fun saveCustomers(customer: Customers): Results<Long>
    {
        this.customersServiceData[customer.id] = customer
        return Success(customer.id)
    }

    override suspend fun updateCustomers(customer: Customers): Results<Int>
    {
        val customerToUpdate = customersServiceData[customer.id]
        return if (customerToUpdate != null)
        {
            customersServiceData[customer.id] = customer
            Success(1)
        } else
            Error(java.lang.Exception("Customer to update not found"))
    }

    // =================================
    //              Methods
    // =================================

    fun refresh()
    {
        observableData.value = customersServiceData.values.toList().sortedBy { it.id }
    }
}