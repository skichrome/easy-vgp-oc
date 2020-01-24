package com.skichrome.oc.easyvgp.viewmodel

import androidx.lifecycle.ViewModel
import com.skichrome.oc.easyvgp.util.AppCoroutinesConfiguration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

class CustomerViewModel : ViewModel()
{
    // =================================
    //              Fields
    // =================================

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(AppCoroutinesConfiguration.uiDispatchers + viewModelJob)

    // =================================
    //        Superclass Methods
    // =================================

    override fun onCleared()
    {
        super.onCleared()
        viewModelJob.cancel()
    }
}