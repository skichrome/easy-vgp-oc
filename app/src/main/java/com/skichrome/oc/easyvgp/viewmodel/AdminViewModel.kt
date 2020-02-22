package com.skichrome.oc.easyvgp.viewmodel

import androidx.lifecycle.*
import com.skichrome.oc.easyvgp.R
import com.skichrome.oc.easyvgp.model.AdminRepository
import com.skichrome.oc.easyvgp.model.Results
import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.local.database.MachineType
import com.skichrome.oc.easyvgp.util.Event
import com.skichrome.oc.easyvgp.util.NetworkException
import com.skichrome.oc.easyvgp.util.RemoteRepositoryException
import com.skichrome.oc.easyvgp.util.uiJob

class AdminViewModel(private val repository: AdminRepository) : ViewModel()
{
    // =================================
    //              Fields
    // =================================

    // --- Events

    private val _message = MutableLiveData<Event<Int>>()
    val message: LiveData<Event<Int>> = _message

    private val _onClickMachineType = MutableLiveData<Event<Long>>()
    val onClickMachineType: LiveData<Event<Long>> = _onClickMachineType

    private val _onLongClickMachineType = MutableLiveData<Event<MachineType>>()
    val onLongClickMachineType: LiveData<Event<MachineType>> = _onLongClickMachineType

    private val _forceRefresh = MutableLiveData<Boolean>()

    // --- Data

    private val _machineTypes: LiveData<List<MachineType>> = _forceRefresh.switchMap { refresh ->
        if (refresh)
            viewModelScope.uiJob {
                val result = repository.getAllMachineType()
                if (result is Success)
                    showMessage(R.string.admin_view_model_machine_type_list_refresh_success)
                else
                    showMessage(R.string.admin_view_model_machine_type_list_refresh_error)
                _forceRefresh.value = false
            }

        repository.observeMachineType().map {
            if (it is Success)
            {
                return@map it.data
            } else
            {
                showMessage(R.string.admin_view_model_machine_type_list_error)
                return@map emptyList<MachineType>()
            }
        }
    }
    val machineTypes: LiveData<List<MachineType>> = _machineTypes

    // =================================
    //              Methods
    // =================================

    init
    {
        forceRefresh()
    }

    // --- Events

    private fun showMessage(msgRef: Int)
    {
        _message.value = Event(msgRef)
    }

    fun onClickMachineType(machineTypeId: Long)
    {
        _onClickMachineType.value = Event(machineTypeId)
    }

    fun onLongClickMachineType(machineType: MachineType)
    {
        _onLongClickMachineType.value = Event(machineType)
    }

    private fun forceRefresh()
    {
        _forceRefresh.value = true
    }

    // --- Data

    fun insertMachineType(machineType: MachineType)
    {
        viewModelScope.uiJob {
            val result = repository.insertNewMachineType(machineType)

            if (result is Success)
                showMessage(R.string.admin_view_model_machine_type_insert_success)
            else
                when ((result as? Results.Error)?.exception)
                {
                    is NetworkException -> showMessage(R.string.admin_view_model_machine_type_network_error)
                    is RemoteRepositoryException -> showMessage(R.string.admin_view_model_machine_type_remote_repo_error)
                    else -> showMessage(R.string.admin_view_model_machine_type_insert_error)
                }
        }
    }

    fun updateMachineType(machineType: MachineType)
    {
        viewModelScope.uiJob {
            val result = repository.updateMachineType(machineType)
            if (result is Success)
                showMessage(R.string.admin_view_model_machine_type_update_success)
            else
                when ((result as? Results.Error)?.exception)
                {
                    is NetworkException -> showMessage(R.string.admin_view_model_machine_type_network_error)
                    is RemoteRepositoryException -> showMessage(R.string.admin_view_model_machine_type_remote_repo_error)
                    else -> showMessage(R.string.admin_view_model_machine_type_update_error)
                }
        }
    }
}