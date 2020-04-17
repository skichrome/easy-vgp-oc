package com.skichrome.oc.easyvgp.viewmodel.vmfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.skichrome.oc.easyvgp.model.base.HomeRepository
import com.skichrome.oc.easyvgp.viewmodel.HomeViewModel

class HomeViewModelFactory(private val repository: HomeRepository) : ViewModelProvider.NewInstanceFactory()
{
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = HomeViewModel(repository) as T
}