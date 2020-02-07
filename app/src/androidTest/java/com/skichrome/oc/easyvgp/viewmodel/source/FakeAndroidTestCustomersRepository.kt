package com.skichrome.oc.easyvgp.viewmodel.source

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.skichrome.oc.easyvgp.model.CustomersRepository
import com.skichrome.oc.easyvgp.model.Results
import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.local.database.Customers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

class FakeAndroidTestCustomersRepository : CustomersRepository
{
    // =================================
    //              Fields
    // =================================

    private var customerServiceData: LinkedHashMap<Long, Customers> = LinkedHashMap()

    private var observableData = MutableLiveData<List<Customers>>()

    // =================================
    //        Superclass Methods
    // =================================

    override fun getAllCustomers(): LiveData<List<Customers>> = observableData

    override suspend fun getCustomerById(id: Long): Results<Customers> =
        customerServiceData[id]?.let { Success(it) } ?: Results.Error(Exception("Customer not found"))

    override suspend fun saveCustomers(customers: Array<Customers>): Results<List<Long>>
    {
        customers.forEach { customerServiceData[it.id] = it }
        return Success(listOf(customerServiceData.size.toLong()))
    }

    override suspend fun saveCustomers(customer: Customers): Results<Long>
    {
        customerServiceData[customer.id] = customer
        return Success(customer.id)
    }

    override suspend fun updateCustomers(customer: Customers): Results<Int>
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

    fun refreshLiveData() = runBlocking(Dispatchers.Main) {
        observableData.value = customerServiceData.values.toList().sortedBy { it.id }
    }
}