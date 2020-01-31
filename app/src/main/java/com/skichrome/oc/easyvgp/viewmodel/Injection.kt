package com.skichrome.oc.easyvgp.viewmodel

import android.app.Application
import android.content.Context
import com.skichrome.oc.easyvgp.androidmanagers.NetManager
import com.skichrome.oc.easyvgp.model.DefaultCustomerRepository
import com.skichrome.oc.easyvgp.model.local.CustomerLocalRepository
import com.skichrome.oc.easyvgp.model.local.database.AppDatabase
import com.skichrome.oc.easyvgp.model.local.database.CustomersDao
import com.skichrome.oc.easyvgp.model.remote.CustomerRemoteRepository
import com.skichrome.oc.easyvgp.viewmodel.vmfactory.CustomerViewModelFactory

object Injection
{
    // --- Android Manager --- //

    private fun provideNetworkManager(context: Context) = NetManager(context)

    // --- Application Database --- //

    private fun provideAppDatabase(app: Application) = AppDatabase.getInstance(app)

    // --- Customers injection --- //

    private fun provideCustomersDao(db: AppDatabase) = db.customersDao()

    private fun provideCustomerLocalRepository(dao: CustomersDao) =
        CustomerLocalRepository(dao)

    private fun provideCustomerRemoteRepository() = CustomerRemoteRepository()

    private fun provideDefaultCustomerRepository(
        netManager: NetManager,
        localRepo: CustomerLocalRepository,
        remoteRepository: CustomerRemoteRepository
    ) = DefaultCustomerRepository(netManager, localRepo, remoteRepository)

    fun provideCustomerViewModelFactory(app: Application): CustomerViewModelFactory
    {
        val db = provideAppDatabase(app)
        val dao = provideCustomersDao(db)
        val localRepo = provideCustomerLocalRepository(dao)
        val remoteRepo = provideCustomerRemoteRepository()
        val netManager = provideNetworkManager(app.applicationContext)
        val defaultRepo = provideDefaultCustomerRepository(netManager, localRepo, remoteRepo)
        return CustomerViewModelFactory(defaultRepo)
    }
}