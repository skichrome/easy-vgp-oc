package com.skichrome.oc.easyvgp

import android.app.Application
import com.skichrome.oc.easyvgp.model.*
import com.skichrome.oc.easyvgp.viewmodel.ServiceLocator

class EasyVGPApplication : Application()
{
    val homeRepository: HomeRepository
        get() = ServiceLocator.provideHomeRepository(this)

    val customerRepository: CustomerRepository
        get() = ServiceLocator.provideCustomerRepository(this)

    val machineRepository: MachineRepository
        get() = ServiceLocator.provideMachineRepository(this)

    val adminRepository: AdminRepository
        get() = ServiceLocator.provideAdminRepository(this)

    val newVgpRepository: NewVgpRepository
        get() = ServiceLocator.provideVgpRepository(this)

    val vgpListRepository: VgpListRepository
        get() = ServiceLocator.provideVgpListRepository(this)
}