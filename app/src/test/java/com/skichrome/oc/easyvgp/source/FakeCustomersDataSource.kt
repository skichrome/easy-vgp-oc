package com.skichrome.oc.easyvgp.source

import com.skichrome.oc.easyvgp.model.Results
import com.skichrome.oc.easyvgp.model.Results.Error
import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.local.CustomersDataSource
import com.skichrome.oc.easyvgp.model.local.database.Customers

class FakeCustomersDataSource(private var customers: MutableList<Customers>? = mutableListOf()) : CustomersDataSource
{
    override suspend fun loadAllCustomers(): Results<List<Customers>> =
        customers?.let { Success(ArrayList(it)) }
            ?: Error(Exception("Customers not found"))

    override suspend fun getCustomerById(id: Long): Results<Customers> =
        customers?.let {
            var foundCustomer: Customers? = null

            it.forEach { customer ->
                if (customer.id == id)
                {
                    println("Customer Item : ${customer.id}, id to match : $id")
                    foundCustomer = customer
                    return@forEach
                }
            }

            foundCustomer?.let { foundCustomerNotNull -> Success(foundCustomerNotNull) }
                ?: Error(Exception("Customer not found"))

        } ?: Error(Exception("List empty, customer not Found"))
}