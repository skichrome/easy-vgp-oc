package com.skichrome.oc.easyvgp.model

import com.skichrome.oc.easyvgp.androidmanagers.NetManager
import com.skichrome.oc.easyvgp.model.local.CustomersDataSource
import com.skichrome.oc.easyvgp.model.local.database.Customers

class DefaultCustomerRepository(
    private val netManager: NetManager,
    private val localCustomerRepo: CustomersDataSource,
    private val remoteCustomerRepo: CustomersDataSource
)
{
    suspend fun getAllCustomers(): Results<List<Customers>> =
        if (netManager.isConnectedToInternet)
            remoteCustomerRepo.loadAllCustomers()
        else
            localCustomerRepo.loadAllCustomers()

    suspend fun getCustomerById(id: Long): Results<Customers> =
        if (netManager.isConnectedToInternet)
            remoteCustomerRepo.getCustomerById(id)
        else
            localCustomerRepo.getCustomerById(id)
}