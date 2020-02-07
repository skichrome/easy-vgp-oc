package com.skichrome.oc.easyvgp

import android.app.Application
import com.skichrome.oc.easyvgp.model.CustomersRepository
import com.skichrome.oc.easyvgp.viewmodel.ServiceLocator

class EasyVGPApplication : Application()
{
    val customersRepository: CustomersRepository
        get() = ServiceLocator.provideCustomerRepository(this)
}