package com.skichrome.oc.easyvgp.model.source

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.skichrome.oc.easyvgp.model.Results
import com.skichrome.oc.easyvgp.model.Results.Error
import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.local.base.CustomerSource
import com.skichrome.oc.easyvgp.model.local.database.Customer

class FakeCustomerSource(private var customerServiceData: LinkedHashMap<Long, Customer> = LinkedHashMap()) :
    CustomerSource
{
    // =================================
    //              Fields
    // =================================

    private var observableData = MutableLiveData<List<Customer>>()

    override fun loadAllCustomers(): LiveData<List<Customer>> = observableData

    override suspend fun getCustomerById(id: Long): Results<Customer> =
        customerServiceData.let {
            val foundCustomer: Customer? = it[id]

            foundCustomer?.let { foundCustomerNotNull -> Success(foundCustomerNotNull) }
                ?: Error(Exception("Customer not found"))
        }

    override suspend fun saveCustomers(customers: Array<Customer>): Results<List<Long>>
    {
        customers.forEach { customerServiceData[it.id] = it }
        return Success(listOf(customers.size.toLong()))
    }

    override suspend fun saveCustomers(customer: Customer): Results<Long>
    {
        this.customerServiceData[customer.id] = customer
        return Success(customer.id)
    }

    override suspend fun updateCustomers(customer: Customer): Results<Int>
    {
        val customerToUpdate = customerServiceData[customer.id]
        return if (customerToUpdate != null)
        {
            customerServiceData[customer.id] = customer
            Success(1)
        } else
            Error(java.lang.Exception("Customer to update not found"))
    }

    // =================================
    //              Methods
    // =================================

    fun refresh()
    {
        observableData.value = customerServiceData.values.toList().sortedBy { it.id }
    }
}