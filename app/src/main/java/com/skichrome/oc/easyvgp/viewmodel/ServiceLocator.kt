package com.skichrome.oc.easyvgp.viewmodel

import android.app.Application
import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.skichrome.oc.easyvgp.androidmanagers.DefaultNetManager
import com.skichrome.oc.easyvgp.androidmanagers.NetManager
import com.skichrome.oc.easyvgp.model.*
import com.skichrome.oc.easyvgp.model.local.LocalAdminSource
import com.skichrome.oc.easyvgp.model.local.LocalCustomerSource
import com.skichrome.oc.easyvgp.model.local.LocalHomeSource
import com.skichrome.oc.easyvgp.model.local.LocalMachineSource
import com.skichrome.oc.easyvgp.model.local.database.AppDatabase
import com.skichrome.oc.easyvgp.model.remote.CustomerRemoteRepository
import com.skichrome.oc.easyvgp.model.remote.RemoteAdminSource

object ServiceLocator
{
    // =================================
    //              Fields
    // =================================

    @Volatile
    private var localDatabase: AppDatabase? = null

    @Volatile
    private var remoteDatabase: FirebaseFirestore? = null

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

    @Volatile
    var adminRepository: AdminRepository? = null
        @VisibleForTesting set

    // =================================
    //              Methods
    // =================================

    // --- Android Manager --- //

    private fun provideNetworkManager(context: Context) = netManager ?: DefaultNetManager(context).also { netManager = it }

    // --- Application Database --- //

    private fun getLocalDatabaseInstance(app: Application) = localDatabase ?: synchronized(this) {
        localDatabase ?: buildLocalDatabase(app).also { localDatabase = it }
    }

    private fun buildLocalDatabase(app: Application): AppDatabase =
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

    // --- Cloud Firestore Database --- //

    private fun getRemoteDatabaseInstance(): FirebaseFirestore = remoteDatabase ?: synchronized(this) {
        remoteDatabase ?: FirebaseFirestore.getInstance().also { remoteDatabase = it }
    }

    // --- Data Source --- //

    // --- Home

    private fun provideLocalHomeSource(app: Application): HomeSource
    {
        val db = getLocalDatabaseInstance(app)
        val companyDao = db.companiesDao()
        val userDao = db.usersDao()
        return LocalHomeSource(companyDao, userDao)
    }

    // --- Customers

    private fun provideLocalCustomerSource(app: Application): CustomerSource
    {
        val db = getLocalDatabaseInstance(app)
        val customerDao = db.customersDao()
        return LocalCustomerSource(customerDao)
    }

    private fun provideRemoteCustomerSource() = CustomerRemoteRepository()

    // --- Machine

    private fun provideLocalMachineSource(app: Application): MachineSource
    {
        val db = getLocalDatabaseInstance(app)
        val machinesDao = db.machinesDao()
        val machineTypeDao = db.machinesTypeDao()
        return LocalMachineSource(machinesDao, machineTypeDao)
    }

    // --- Admin

    private fun configureLocalAdminRepository(app: Application): AdminSource
    {
        val db = getLocalDatabaseInstance(app)
        val machineTypeDao = db.machinesTypeDao()
        val controlPointDao = db.controlPointDao()
        val machineTypeControlPointDao = db.machineTypeControlPointCrossRefDao()
        return LocalAdminSource(
            machineTypeDao = machineTypeDao,
            controlPointDao = controlPointDao,
            machineTypeControlPointDao = machineTypeControlPointDao
        )
    }

    private fun configureRemoteAdminRepository(): AdminSource
    {
        val db = getRemoteDatabaseInstance()
        return RemoteAdminSource(db)
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

    // --- Admin

    fun provideAdminRepository(app: Application) = adminRepository ?: synchronized(this) {
        adminRepository ?: configureAdminRepository(app).also { adminRepository = it }
    }

    private fun configureAdminRepository(app: Application): AdminRepository
    {
        val netManager = provideNetworkManager(app.applicationContext)
        val localSource = configureLocalAdminRepository(app)
        val remoteSource = configureRemoteAdminRepository()
        return DefaultAdminRepository(netManager = netManager, localSource = localSource, remoteSource = remoteSource)
    }

    // --- Testing purposes --- //

    @VisibleForTesting
    fun resetRepository()
    {
        synchronized(this) {
            localDatabase?.apply {
                clearAllTables()
                close()
            }
        }
    }
}