package com.skichrome.oc.easyvgp.viewmodel.vmfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.skichrome.oc.easyvgp.model.CustomersRepository
import com.skichrome.oc.easyvgp.viewmodel.CustomerViewModel

class CustomerViewModelFactory(private val defaultCustomersRepo: CustomersRepository) : ViewModelProvider.NewInstanceFactory()
{
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = CustomerViewModel(defaultCustomersRepo) as T
}