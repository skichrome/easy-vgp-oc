package com.skichrome.oc.easyvgp.viewmodel

import androidx.lifecycle.*
import com.skichrome.oc.easyvgp.R
import com.skichrome.oc.easyvgp.model.MachineRepository
import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.local.database.Machine
import com.skichrome.oc.easyvgp.model.local.database.MachineType
import com.skichrome.oc.easyvgp.util.Event
import kotlinx.coroutines.launch

class MachineViewModel(private val repository: MachineRepository) : ViewModel()
{
    // =================================
    //              Fields
    // =================================

    private val _customerId = MutableLiveData<Long>()

    private val _machines: LiveData<List<Machine>> = repository.observeMachines().switchMap { machinesList ->
        if (machinesList is Success)
        {
            return@switchMap filterMachines(machinesList.data)
        } else
        {
            showMessage(R.string.view_model_machine_machines_list)
            return@switchMap MutableLiveData<List<Machine>>(emptyList())
        }
    }
    val machines: LiveData<List<Machine>> = _machines

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

    private val _machine = MutableLiveData<Machine>()
    val machine: LiveData<Machine> = _machine

    private val _machineSaved = MutableLiveData<Event<Boolean>>()
    val machineSaved: LiveData<Event<Boolean>> = _machineSaved

    private val _machineClicked = MutableLiveData<Event<Machine>>()
    val machineClicked: LiveData<Event<Machine>> = _machineClicked

    private val _machineLongClicked = MutableLiveData<Event<Long>>()
    val machineLongClicked: LiveData<Event<Long>> = _machineLongClicked

    private val _errorMessage = MutableLiveData<Event<Int>>()
    val errorMessage: LiveData<Event<Int>> = _errorMessage

    // =================================
    //              Methods
    // =================================

    // --- Events

    fun onClickMachine(machine: Machine)
    {
        _machineClicked.value = Event(machine)
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

    private fun filterMachines(machines: List<Machine>): LiveData<List<Machine>>
    {
        val machinesFiltered = MutableLiveData<List<Machine>>()

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

    fun saveMachine(machine: Machine)
    {
        viewModelScope.launch {
            val result = repository.insertNewMachine(machine)
            if (result is Success)
                _machineSaved.value = Event(true)
            else
                showMessage(R.string.view_model_machine_insert_error)
        }
    }

    fun updateMachine(machine: Machine)
    {
        viewModelScope.launch {
            val result = repository.updateMachine(machine)
            if (result is Success)
                _machineSaved.value = Event(true)
            else
                showMessage(R.string.view_model_machine_update_error)
        }
    }
}