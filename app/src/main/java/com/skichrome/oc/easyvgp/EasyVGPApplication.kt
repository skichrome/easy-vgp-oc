package com.skichrome.oc.easyvgp

import android.content.Context
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.skichrome.oc.easyvgp.model.base.*
import com.skichrome.oc.easyvgp.model.local.database.AppDatabase
import com.skichrome.oc.easyvgp.viewmodel.ServiceLocator

class EasyVGPApplication : MultiDexApplication()
{
    val database: AppDatabase
        get() = ServiceLocator.getLocalDatabaseInstance(this)

    val homeRepository: HomeRepository
        get() = ServiceLocator.provideHomeRepository(this)

    val customerRepository: CustomerRepository
        get() = ServiceLocator.provideCustomerRepository(this)

    val machineRepository: MachineRepository
        get() = ServiceLocator.provideMachineRepository(this)

    val adminRepository: AdminRepository
        get() = ServiceLocator.provideAdminRepository(this)

    val vgpListRepository: VgpListRepository
        get() = ServiceLocator.provideVgpListRepository(this)

    val newVgpSetupRepository: NewVgpSetupRepository
        get() = ServiceLocator.provideNewVGPSetupRepository(this)

    val newVgpRepository: NewVgpRepository
        get() = ServiceLocator.provideVgpRepository(this)

    override fun attachBaseContext(base: Context?)
    {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}