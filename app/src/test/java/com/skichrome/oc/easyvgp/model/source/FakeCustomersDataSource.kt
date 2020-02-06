package com.skichrome.oc.easyvgp.model.source

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.skichrome.oc.easyvgp.model.CustomersDataSource
import com.skichrome.oc.easyvgp.model.Results
import com.skichrome.oc.easyvgp.model.Results.Error
import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.local.database.Customers

class FakeCustomersDataSource(private var customers: MutableList<Customers>? = mutableListOf()) :
    CustomersDataSource
{
    // =================================
    //              Fields
    // =================================

    private var observableData = MutableLiveData<List<Customers>>()

    override fun loadAllCustomers(): LiveData<List<Customers>> = observableData

    override suspend fun getCustomerById(id: Long): Results<Customers> =
        customers?.let {
            var foundCustomer: Customers? = null

            it.forEach { customer ->
                if (customer.id == id)
                {
                    foundCustomer = customer
                    return@forEach
                }
            }

            foundCustomer?.let { foundCustomerNotNull -> Success(foundCustomerNotNull) }
                ?: Error(Exception("Customer not found"))

        } ?: Error(Exception("List empty, customer not Found"))

    override suspend fun saveCustomers(customers: Array<Customers>): Results<List<Long>>
    {
        this.customers = customers.toMutableList()
        return Success(listOf(customers.size.toLong()))
    }

    override suspend fun saveCustomers(customer: Customers): Results<Long>
    {
        this.customers = mutableListOf(customer)
        return Success(customer.id)
    }

    // =================================
    //              Methods
    // =================================

    fun refresh()
    {
        observableData.value = customers
    }
}