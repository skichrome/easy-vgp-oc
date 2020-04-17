package com.skichrome.oc.easyvgp.viewmodel.source

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.skichrome.oc.easyvgp.model.Results
import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.base.CustomerRepository
import com.skichrome.oc.easyvgp.model.local.database.Customer

class FakeCustomerRepository : CustomerRepository
{
    // =================================
    //              Fields
    // =================================

    private var customerServiceData: LinkedHashMap<Long, Customer> = LinkedHashMap()

    private var observableData = MutableLiveData<List<Customer>>()

    // =================================
    //        Superclass Methods
    // =================================

    override fun getAllCustomers(): LiveData<List<Customer>> = observableData

    override suspend fun getCustomerById(id: Long): Results<Customer> =
        customerServiceData[id]?.let { Success(it) } ?: Results.Error(Exception("Customer not found"))

    override suspend fun saveCustomers(customers: Array<Customer>): Results<List<Long>>
    {
        customers.forEach { customerServiceData[it.id] = it }
        return Success(listOf(customerServiceData.size.toLong()))
    }

    override suspend fun saveCustomers(customer: Customer): Results<Long>
    {
        customerServiceData[customer.id] = customer
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
            Results.Error(java.lang.Exception("Customer to update not found"))
    }

    // =================================
    //              Methods
    // =================================

    fun refreshLiveData()
    {
        observableData.value = customerServiceData.values.toList().sortedBy { it.id }
    }
}