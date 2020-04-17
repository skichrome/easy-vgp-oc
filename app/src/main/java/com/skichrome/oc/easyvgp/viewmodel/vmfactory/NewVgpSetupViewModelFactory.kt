package com.skichrome.oc.easyvgp.viewmodel.vmfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.skichrome.oc.easyvgp.model.base.NewVgpSetupRepository
import com.skichrome.oc.easyvgp.viewmodel.NewVgpSetupViewModel

class NewVgpSetupViewModelFactory(private val repository: NewVgpSetupRepository) : ViewModelProvider.NewInstanceFactory()
{
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = NewVgpSetupViewModel(repository) as T
}