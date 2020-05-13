package com.skichrome.oc.easyvgp.viewmodel.vmfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.skichrome.oc.easyvgp.model.base.AdminRepository
import com.skichrome.oc.easyvgp.viewmodel.AdminViewModel

class AdminViewModelFactory(private val repository: AdminRepository) : ViewModelProvider.NewInstanceFactory()
{
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = AdminViewModel(repository) as T
}