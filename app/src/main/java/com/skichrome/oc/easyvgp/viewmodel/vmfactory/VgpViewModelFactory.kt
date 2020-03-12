package com.skichrome.oc.easyvgp.viewmodel.vmfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.skichrome.oc.easyvgp.model.VgpRepository
import com.skichrome.oc.easyvgp.viewmodel.VgpViewModel

class VgpViewModelFactory(private val repository: VgpRepository) : ViewModelProvider.NewInstanceFactory()
{
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = VgpViewModel(repository) as T
}