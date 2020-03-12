package com.skichrome.oc.easyvgp.viewmodel.vmfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.skichrome.oc.easyvgp.model.VgpListRepository
import com.skichrome.oc.easyvgp.viewmodel.VgpListViewModel

class VgpListViewModelFactory(private val repository: VgpListRepository) : ViewModelProvider.NewInstanceFactory()
{
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = VgpListViewModel(repository) as T
}