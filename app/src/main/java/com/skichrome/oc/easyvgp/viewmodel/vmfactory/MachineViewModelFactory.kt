package com.skichrome.oc.easyvgp.viewmodel.vmfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.skichrome.oc.easyvgp.model.base.MachineRepository
import com.skichrome.oc.easyvgp.viewmodel.MachineViewModel

class MachineViewModelFactory(private val repository: MachineRepository) : ViewModelProvider.NewInstanceFactory()
{
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = MachineViewModel(repository) as T
}