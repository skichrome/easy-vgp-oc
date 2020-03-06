package com.skichrome.oc.easyvgp.viewmodel.vmfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.skichrome.oc.easyvgp.model.CustomerRepository
import com.skichrome.oc.easyvgp.viewmodel.CustomerViewModel

class CustomerViewModelFactory(private val repository: CustomerRepository) : ViewModelProvider.NewInstanceFactory()
{
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = CustomerViewModel(repository) as T
}