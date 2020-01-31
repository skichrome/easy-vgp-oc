package com.skichrome.oc.easyvgp.viewmodel.vmfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.skichrome.oc.easyvgp.model.DefaultCustomerRepository
import com.skichrome.oc.easyvgp.viewmodel.CustomerViewModel

class CustomerViewModelFactory(private val defaultCustomerRepo: DefaultCustomerRepository) : ViewModelProvider.NewInstanceFactory()
{
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = CustomerViewModel(defaultCustomerRepo) as T
}