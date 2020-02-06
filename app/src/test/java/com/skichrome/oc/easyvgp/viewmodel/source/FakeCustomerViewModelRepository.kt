package com.skichrome.oc.easyvgp.viewmodel.source

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.skichrome.oc.easyvgp.model.CustomerRepository
import com.skichrome.oc.easyvgp.model.Results
import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.local.database.Customers

class FakeCustomerViewModelRepository : CustomerRepository
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

    // =================================
    //              Methods
    // =================================

    fun refreshLiveData()
    {
        val newList = mutableListOf<Customers>()
        customerServiceData.forEach {
            newList.add(it.component2())
        }
        observableData.value = newList
    }
}