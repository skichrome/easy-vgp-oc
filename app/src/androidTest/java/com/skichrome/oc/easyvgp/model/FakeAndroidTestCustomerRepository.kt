package com.skichrome.oc.easyvgp.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.base.CustomerRepository
import com.skichrome.oc.easyvgp.model.local.database.Customer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

class FakeAndroidTestCustomerRepository(private val customerDataService: LinkedHashMap<Long, Customer> = LinkedHashMap()) : CustomerRepository
{
    // =================================
    //              Fields
    // =================================

    private val observableData = MutableLiveData<List<Customer>>()

    // =================================
    //        Superclass Methods
    // =================================

    override fun getAllCustomers(): LiveData<List<Customer>> = observableData

    override suspend fun getCustomerById(id: Long): Results<Customer> =
        customerDataService[id]?.let { Success(it) } ?: Results.Error(Exception("Customer not found"))

    override suspend fun saveCustomers(customers: Array<Customer>): Results<List<Long>>
    {
        customers.forEach { customerDataService[it.id] = it }
        return Success(listOf(customerDataService.size.toLong()))
    }

    override suspend fun saveCustomers(customer: Customer): Results<Long>
    {
        customerDataService[customer.id] = customer
        return Success(customer.id)
    }

    override suspend fun updateCustomers(customer: Customer): Results<Int>
    {
        val customerToUpdate = customerDataService[customer.id]
        return if (customerToUpdate != null)
        {
            customerDataService[customer.id] = customer
            Success(1)
        }
        else
            Results.Error(java.lang.Exception("Customer to update not found"))
    }

    // =================================
    //              Methods
    // =================================

    fun refreshLiveData() = runBlocking(Dispatchers.Main) {
        observableData.value = customerDataService.values.toList().sortedBy { it.id }
    }
}