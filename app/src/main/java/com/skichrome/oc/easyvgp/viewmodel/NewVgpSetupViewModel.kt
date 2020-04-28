package com.skichrome.oc.easyvgp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.skichrome.oc.easyvgp.model.Results.Error
import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.base.NewVgpSetupRepository
import com.skichrome.oc.easyvgp.model.local.database.MachineControlPointDataExtra
import com.skichrome.oc.easyvgp.util.Event
import kotlinx.coroutines.launch

class NewVgpSetupViewModel(private val repository: NewVgpSetupRepository) : BaseViewModel()
{
    // =================================
    //              Fields
    // =================================

    private val _success = MutableLiveData<Event<Long>>()
    val success: LiveData<Event<Long>> = _success

    private val _machineWithControlPointsDataExtras = MutableLiveData<MachineControlPointDataExtra>()
    val machineWithControlPointsDataExtras: LiveData<MachineControlPointDataExtra> = _machineWithControlPointsDataExtras

    // =================================
    //              Methods
    // =================================

    fun loadPreviousNewVgpExtras(date: Long)
    {
        viewModelScope.launch {
            val result = repository.getPreviousCtrlPtDataExtraFromDate(date)
            if (result is Success)
                _machineWithControlPointsDataExtras.value = result.data
            else
                handleError(result as? Error)
        }
    }

    fun createNewVgpExtras(vgpExtra: MachineControlPointDataExtra)
    {
        viewModelScope.launch {
            val result = repository.insertMachineCtrlPtDataExtra(vgpExtra)
            if (result is Success)
                _success.value = Event(result.data)
            else
                handleError(result as? Error)
        }
    }

    fun updateNewVgpExtras(vgpExtra: MachineControlPointDataExtra)
    {
        viewModelScope.launch {
            val result = repository.updateMachineCtrlPtDataExtra(vgpExtra)
            if (result is Success)
                _success.value = Event(vgpExtra.id)
            else
                handleError(result as? Error)
        }
    }

    fun deleteVgpExtras(vgpExtraDate: Long)
    {
        viewModelScope.launch {
            val result = repository.deleteMachineCtrlPtDataExtra(vgpExtraDate)
            if (result !is Success)
                handleError(result as? Error)
        }
    }
}