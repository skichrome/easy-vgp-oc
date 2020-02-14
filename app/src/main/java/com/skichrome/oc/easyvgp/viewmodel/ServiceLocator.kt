package com.skichrome.oc.easyvgp.viewmodel

import android.app.Application
import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.skichrome.oc.easyvgp.androidmanagers.DefaultNetManager
import com.skichrome.oc.easyvgp.androidmanagers.NetManager
import com.skichrome.oc.easyvgp.model.*
import com.skichrome.oc.easyvgp.model.local.LocalCustomerRepository
import com.skichrome.oc.easyvgp.model.local.LocalHomeSource
import com.skichrome.oc.easyvgp.model.local.LocalMachineSource
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
    var homeRepository: HomeRepository? = null
        @VisibleForTesting set

    @Volatile
    var customerRepository: CustomerRepository? = null
        @VisibleForTesting set

    @Volatile
    var machineRepository: MachineRepository? = null
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
        Room.databaseBuilder(app.applicationContext, AppDatabase::class.java, "easy-vgp-database.db")
            .addCallback(object : RoomDatabase.Callback()
            {
                override fun onCreate(db: SupportSQLiteDatabase)
                {
                    super.onCreate(db)
                    DatabaseDataDebug.prePopulateDatabase(db)
                }
            })
            .build()

    // --- Data Source --- //

    // --- Home

    private fun provideLocalHomeSource(app: Application): HomeSource
    {
        val db = getDatabaseInstance(app)
        val companyDao = db.companiesDao()
        val userDao = db.usersDao()
        return LocalHomeSource(companyDao, userDao)
    }

    // --- Customers

    private fun provideLocalCustomerSource(app: Application): CustomerDataSource
    {
        val db = getDatabaseInstance(app)
        val customerDao = db.customersDao()
        return LocalCustomerRepository(customerDao)
    }

    private fun provideRemoteCustomerSource() = CustomerRemoteRepository()

    // --- Machine

    private fun provideLocalMachineSource(app: Application): MachineSource
    {
        val db = getDatabaseInstance(app)
        val machinesDao = db.machinesDao()
        val machineTypeDao = db.machinesTypeDao()
        return LocalMachineSource(machinesDao, machineTypeDao)
    }

    // --- Data Repository --- //

    // --- Home

    fun provideHomeRepository(app: Application) = homeRepository ?: synchronized(this) {
        homeRepository ?: configureDefaultHomeRepository(app).also { homeRepository = it }
    }

    private fun configureDefaultHomeRepository(app: Application): HomeRepository
    {
        val localSource = provideLocalHomeSource(app)
        return DefaultHomeRepository(localSource)
    }

    // --- Customers

    fun provideCustomerRepository(app: Application): CustomerRepository = customerRepository ?: synchronized(this) {
        customerRepository ?: configureDefaultCustomerRepository(app).also { customerRepository = it }
    }

    private fun configureDefaultCustomerRepository(app: Application): CustomerRepository
    {
        val localSource = provideLocalCustomerSource(app)
        val remoteSource = provideRemoteCustomerSource()
        val netManager = provideNetworkManager(app.applicationContext)
        return DefaultCustomerRepository(netManager, localSource, remoteSource)
    }

    // --- Machine

    fun provideMachineRepository(app: Application) = machineRepository ?: synchronized(this) {
        machineRepository ?: configureMachinesRepository(app).also { machineRepository = it }
    }

    private fun configureMachinesRepository(app: Application): MachineRepository
    {
        val localSource = provideLocalMachineSource(app)
        return DefaultMachineRepository(localSource)
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