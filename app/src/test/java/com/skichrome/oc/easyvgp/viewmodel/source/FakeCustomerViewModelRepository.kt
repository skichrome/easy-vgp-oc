package com.skichrome.oc.easyvgp.viewmodel.source

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

    // =================================
    //        Superclass Methods
    // =================================

    override suspend fun getAllCustomers(): Results<List<Customers>> = Success(customerServiceData.values.toList())

    override suspend fun getCustomerById(id: Long): Results<Customers> =
        customerServiceData[id]?.let { Success(it) } ?: Results.Error(Exception("Customer not found"))

    // =================================
    //              Methods
    // =================================

    fun addCustomers(vararg customers: Customers) = customers.forEach { customerServiceData[it.id] = it }
}