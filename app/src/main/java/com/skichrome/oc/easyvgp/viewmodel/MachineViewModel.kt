package com.skichrome.oc.easyvgp.viewmodel

import androidx.lifecycle.*
import com.skichrome.oc.easyvgp.R
import com.skichrome.oc.easyvgp.model.MachineRepository
import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.local.database.MachineType
import com.skichrome.oc.easyvgp.model.local.database.Machines
import com.skichrome.oc.easyvgp.util.Event
import kotlinx.coroutines.launch

class MachineViewModel(private val repository: MachineRepository) : ViewModel()
{
    // =================================
    //              Fields
    // =================================

    private val _customerId = MutableLiveData<Long>()

    private val _machines: LiveData<List<Machines>> = repository.observeMachines().switchMap { machinesList ->
        if (machinesList is Success)
        {
            return@switchMap filterMachines(machinesList.data)
        } else
        {
            showMessage(R.string.view_model_machine_machines_list)
            return@switchMap MutableLiveData<List<Machines>>(emptyList())
        }
    }
    val machines: LiveData<List<Machines>> = _machines

    private val _machinesTypes: LiveData<List<MachineType>> = repository.observeMachineTypes().map { machineTypeList ->
        if (machineTypeList is Success)
            return@map machineTypeList.data
        else
        {
            showMessage(R.string.view_model_machine_machines_type_list)
            return@map emptyList<MachineType>()
        }
    }
    val machineTypes: LiveData<List<MachineType>> = _machinesTypes

    private val _machine = MutableLiveData<Machines>()
    val machine: LiveData<Machines> = _machine

    private val _machineSaved = MutableLiveData<Event<Boolean>>()
    val machineSaved: LiveData<Event<Boolean>> = _machineSaved

    private val _machineClicked = MutableLiveData<Event<Long>>()
    val machineClicked: LiveData<Event<Long>> = _machineClicked

    private val _machineLongClicked = MutableLiveData<Event<Long>>()
    val machineLongClicked: LiveData<Event<Long>> = _machineLongClicked

    private val _errorMessage = MutableLiveData<Event<Int>>()
    val errorMessage: LiveData<Event<Int>> = _errorMessage

    // =================================
    //              Methods
    // =================================

    // --- Events

    fun onClickMachine(machineId: Long)
    {
        _machineClicked.value = Event(machineId)
    }

    fun onLongClickMachine(machineId: Long)
    {
        _machineLongClicked.value = Event(machineId)
    }

    private fun showMessage(msg: Int)
    {
        _errorMessage.value = Event(msg)
    }

    // --- Configuration

    fun changeCustomerId(newCustomer: Long)
    {
        _customerId.value = newCustomer
    }

    private fun filterMachines(machines: List<Machines>): LiveData<List<Machines>>
    {
        val machinesFiltered = MutableLiveData<List<Machines>>()

        viewModelScope.launch {
            machinesFiltered.value = machines.filter { _customerId.value == it.customer }
        }

        return machinesFiltered
    }

    // --- Data

    fun loadMachineToEdit(machineId: Long)
    {
        viewModelScope.launch {
            val result = repository.getMachineById(machineId)
            if (result is Success)
                _machine.value = result.data
            else
                showMessage(R.string.view_model_machine_machine_by_id_error)
        }
    }

    fun saveMachine(machines: Machines)
    {
        viewModelScope.launch {
            val result = repository.insertNewMachine(machines)
            if (result is Success)
                _machineSaved.value = Event(true)
            else
                showMessage(R.string.view_model_machine_insert_error)
        }
    }

    fun updateMachine(machines: Machines)
    {
        viewModelScope.launch {
            val result = repository.updateMachine(machines)
            if (result is Success)
                _machineSaved.value = Event(true)
            else
                showMessage(R.string.view_model_machine_update_error)
        }
    }
}