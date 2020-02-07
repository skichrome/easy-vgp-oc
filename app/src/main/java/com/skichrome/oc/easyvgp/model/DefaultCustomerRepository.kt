package com.skichrome.oc.easyvgp.model

import androidx.lifecycle.LiveData
import com.skichrome.oc.easyvgp.androidmanagers.NetManager
import com.skichrome.oc.easyvgp.model.local.database.Customers

class DefaultCustomerRepository(
    private val netManager: NetManager,
    private val localCustomerRepo: CustomersDataSource,
    private val remoteCustomerRepo: CustomersDataSource
) : CustomerRepository
{
    override fun getAllCustomers(): LiveData<List<Customers>> = localCustomerRepo.loadAllCustomers()

    override suspend fun getCustomerById(id: Long): Results<Customers> = localCustomerRepo.getCustomerById(id)

    override suspend fun saveCustomers(customer: Customers): Results<Long> = localCustomerRepo.saveCustomers(customer)

    override suspend fun saveCustomers(customers: Array<Customers>): Results<List<Long>> = localCustomerRepo.saveCustomers(customers)

    override suspend fun updateCustomers(customer: Customers): Results<Int> = localCustomerRepo.updateCustomers(customer)
}