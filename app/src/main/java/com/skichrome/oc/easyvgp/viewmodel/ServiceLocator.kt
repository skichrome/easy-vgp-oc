package com.skichrome.oc.easyvgp.viewmodel

import android.app.Application
import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.room.Room
import com.skichrome.oc.easyvgp.androidmanagers.DefaultNetManager
import com.skichrome.oc.easyvgp.androidmanagers.NetManager
import com.skichrome.oc.easyvgp.model.CustomersDataSource
import com.skichrome.oc.easyvgp.model.CustomersRepository
import com.skichrome.oc.easyvgp.model.DefaultCustomersRepository
import com.skichrome.oc.easyvgp.model.local.CustomerLocalRepository
import com.skichrome.oc.easyvgp.model.local.database.AppDatabase
import com.skichrome.oc.easyvgp.model.remote.CustomerRemoteRepository

object ServiceLocator
{
    // =================================
    //              Fields
    // =================================

    @Volatile
    private var database: AppDatabase? = null

    var netManager: NetManager? = null
        @VisibleForTesting set

    @Volatile
    var customersRepository: CustomersRepository? = null
        @VisibleForTesting set

    // =================================
    //              Methods
    // =================================

    // --- Android Manager --- //

    private fun provideNetworkManager(context: Context) = netManager ?: DefaultNetManager(context).also { netManager = it }

    // --- Application Database --- //

    private fun getDatabaseInstance(app: Application) = database ?: synchronized(this) {
        database ?: buildDatabase(app).also { database = it }
    }

    private fun buildDatabase(app: Application): AppDatabase =
        Room.databaseBuilder(app.applicationContext, AppDatabase::class.java, "easy-vgp-database.db").build()

    // --- Customers injection --- //

    private fun provideCustomerLocalRepository(app: Application): CustomersDataSource
    {
        val db = getDatabaseInstance(app)
        val customerDao = db.customersDao()
        return CustomerLocalRepository(customerDao)
    }

    private fun provideCustomerRemoteRepository() = CustomerRemoteRepository()

    private fun configureDefaultCustomerRepository(app: Application): CustomersRepository
    {
        val localRepo = provideCustomerLocalRepository(app)
        val remoteRepo = provideCustomerRemoteRepository()
        val netManager = provideNetworkManager(app.applicationContext)
        return DefaultCustomersRepository(netManager, localRepo, remoteRepo)
    }

    // --- Provide Repository to Application --- //

    fun provideCustomerRepository(app: Application): CustomersRepository = customersRepository ?: synchronized(this) {
        customersRepository ?: configureDefaultCustomerRepository(app).also { customersRepository = it }
    }

    // --- Testing purposes --- //

    @VisibleForTesting
    fun resetRepository()
    {
        synchronized(this) {
            database?.apply {
                clearAllTables()
                close()
            }
        }
    }
}