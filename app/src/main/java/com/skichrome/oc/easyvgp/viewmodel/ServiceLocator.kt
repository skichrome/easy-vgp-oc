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
import com.skichrome.oc.easyvgp.model.base.*
import com.skichrome.oc.easyvgp.model.local.*
import com.skichrome.oc.easyvgp.model.local.database.AppDatabase
import com.skichrome.oc.easyvgp.model.local.database.MIGRATION_1_2
import com.skichrome.oc.easyvgp.model.remote.CustomerRemoteRepository
import com.skichrome.oc.easyvgp.model.remote.RemoteAdminSource
import com.skichrome.oc.easyvgp.model.remote.RemoteHomeSource
import com.skichrome.oc.easyvgp.model.remote.RemoteVgpListSource

object ServiceLocator
{
    // =================================
    //              Fields
    // =================================

    @Volatile
    private var localDatabase: AppDatabase? = null

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

    @Volatile
    var vgpListRepository: VgpListRepository? = null
        @VisibleForTesting set

    @Volatile
    var newVgpSetupRepository: NewVgpSetupRepository? = null
        @VisibleForTesting set

    @Volatile
    var newVgpRepository: NewVgpRepository? = null
        @VisibleForTesting set

    // =================================
    //              Methods
    // =================================

    // --- Android Manager --- //

    private fun provideNetworkManager(context: Context) = netManager ?: DefaultNetManager(context).also { netManager = it }

    // --- Application Database --- //

    fun getLocalDatabaseInstance(app: Application) = localDatabase ?: synchronized(this) {
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
            .addMigrations(MIGRATION_1_2)
            .build()

    // --- Data Source --- //

    // --- Home

    private fun provideLocalHomeSource(app: Application): HomeSource
    {
        val db = getLocalDatabaseInstance(app)
        val companyDao = db.companiesDao()
        val userDao = db.usersDao()
        val ctrlPointDao = db.controlPointDao()
        val machineTypeDao = db.machinesTypeDao()
        val machineTypeCtrlPointDao = db.machineTypeControlPointCrossRefDao()
        val machineCtrlPtDataExtraDao = db.machineControlPointDataExtraDao()
        val machineCtrlPointDataDao = db.machineControlPointDataDao()
        return LocalHomeSource(
            companyDao,
            userDao,
            ctrlPointDao,
            machineTypeDao,
            machineTypeCtrlPointDao,
            machineCtrlPtDataExtraDao,
            machineCtrlPointDataDao
        )
    }

    private fun provideRemoteHomeSource(): HomeSource = RemoteHomeSource()

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

    private fun configureLocalAdminSource(app: Application): AdminSource
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

    private fun configureRemoteAdminSource(): AdminSource = RemoteAdminSource()

    // --- VGP

    private fun configureLocalVgpListSource(app: Application): VgpListSource
    {
        val db = getLocalDatabaseInstance(app)
        val machineControlPointDataDao = db.machineControlPointDataDao()
        val machineCtrlPtExtraDao = db.machineControlPointDataExtraDao()
        val customerDao = db.customersDao()

        return LocalVgpListSource(
            machineControlPointDataDao = machineControlPointDataDao,
            machineCtrlPtExtraDao = machineCtrlPtExtraDao,
            customerDao = customerDao
        )
    }

    private fun configureRemoteVgpListSource(): VgpListSource
    {
        return RemoteVgpListSource()
    }

    // --- New VGP Setup

    private fun configureLocalNewVgpSetupSource(app: Application): NewVgpSetupSource
    {
        val db = getLocalDatabaseInstance(app)
        val machineCtrlPtExtraDao = db.machineControlPointDataExtraDao()
        return LocalNewVgpSetupSource(machineCtrlPtExtraDao = machineCtrlPtExtraDao)
    }

    // --- New VGP

    private fun configureLocalVgpSource(app: Application): NewVgpSource
    {
        val db = getLocalDatabaseInstance(app)
        val machineTypeDao = db.machinesTypeDao()
        val ctrlPointDataDao = db.controlPointDataDao()
        val machineCtrlPtExtraDao = db.machineControlPointDataExtraDao()
        val machineCtrlPointDao = db.machineControlPointDataDao()
        return LocalNewVgpSource(
            machineTypeDao = machineTypeDao,
            ctrlPointDataDao = ctrlPointDataDao,
            machineCtrlPointDao = machineCtrlPointDao,
            machineCtrlPtExtraDao = machineCtrlPtExtraDao
        )
    }

    // --- Data Repository --- //

    // --- Home

    fun provideHomeRepository(app: Application) = homeRepository ?: synchronized(this) {
        homeRepository ?: configureDefaultHomeRepository(app).also { homeRepository = it }
    }

    private fun configureDefaultHomeRepository(app: Application): HomeRepository
    {
        val netManager = provideNetworkManager(app)
        val localSource = provideLocalHomeSource(app)
        val remoteSource = provideRemoteHomeSource()
        return DefaultHomeRepository(netManager, localSource, remoteSource)
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
        val localSource = configureLocalAdminSource(app)
        val remoteSource = configureRemoteAdminSource()
        return DefaultAdminRepository(netManager = netManager, localSource = localSource, remoteSource = remoteSource)
    }

    // --- VGP List

    fun provideVgpListRepository(app: Application) = vgpListRepository ?: synchronized(this) {
        vgpListRepository ?: configureVgpListRepository(app).also { vgpListRepository = it }
    }

    private fun configureVgpListRepository(app: Application): VgpListRepository
    {
        val netManager = provideNetworkManager(app.applicationContext)
        val localSource = configureLocalVgpListSource(app)
        val remoteSource = configureRemoteVgpListSource()
        return DefaultVgpListRepository(localSource = localSource, remoteSource = remoteSource, netManager = netManager)
    }

    // --- New VGP Setup

    fun provideNewVGPSetupRepository(app: Application): NewVgpSetupRepository = newVgpSetupRepository ?: synchronized(this) {
        newVgpSetupRepository ?: configureNewVGPSetupRepository(app).also { newVgpSetupRepository = it }
    }

    private fun configureNewVGPSetupRepository(app: Application): NewVgpSetupRepository
    {
        val localSource = configureLocalNewVgpSetupSource(app)
        return DefaultNewVgpSetupRepository(localSource = localSource)
    }

    // --- New VGP

    fun provideVgpRepository(app: Application) = newVgpRepository ?: synchronized(this) {
        newVgpRepository ?: configureVgpRepository(app).also { newVgpRepository = it }
    }

    private fun configureVgpRepository(app: Application): NewVgpRepository
    {
        val localSource = configureLocalVgpSource(app)
        return DefaultNewVgpRepository(localSource = localSource)
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