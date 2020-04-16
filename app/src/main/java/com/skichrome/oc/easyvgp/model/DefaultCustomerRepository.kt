package com.skichrome.oc.easyvgp.model

import androidx.lifecycle.LiveData
import com.skichrome.oc.easyvgp.androidmanagers.NetManager
import com.skichrome.oc.easyvgp.model.local.base.CustomerRepository
import com.skichrome.oc.easyvgp.model.local.base.CustomerSource
import com.skichrome.oc.easyvgp.model.local.database.Customer

class DefaultCustomerRepository(
    private val netManager: NetManager,
    private val localCustomerRepo: CustomerSource,
    private val remoteCustomerRepo: CustomerSource
) : CustomerRepository
{
    override fun getAllCustomers(): LiveData<List<Customer>> = localCustomerRepo.loadAllCustomers()

    override suspend fun getCustomerById(id: Long): Results<Customer> = localCustomerRepo.getCustomerById(id)

    override suspend fun saveCustomers(customer: Customer): Results<Long> = localCustomerRepo.saveCustomers(customer)

    override suspend fun saveCustomers(customers: Array<Customer>): Results<List<Long>> = localCustomerRepo.saveCustomers(customers)

    override suspend fun updateCustomers(customer: Customer): Results<Int> = localCustomerRepo.updateCustomers(customer)
}