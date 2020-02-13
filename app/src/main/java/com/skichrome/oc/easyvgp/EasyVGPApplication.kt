package com.skichrome.oc.easyvgp

import android.app.Application
import com.skichrome.oc.easyvgp.model.CustomerRepository
import com.skichrome.oc.easyvgp.model.MachineRepository
import com.skichrome.oc.easyvgp.viewmodel.ServiceLocator

class EasyVGPApplication : Application()
{
    val customerRepository: CustomerRepository
        get() = ServiceLocator.provideCustomerRepository(this)

    val machineRepository: MachineRepository
        get() = ServiceLocator.provideMachineRepository(this)
}