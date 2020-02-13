package com.skichrome.oc.easyvgp.viewmodel

import androidx.lifecycle.*
import com.skichrome.oc.easyvgp.R
import com.skichrome.oc.easyvgp.model.MachineRepository
import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.local.database.Machines
import com.skichrome.oc.easyvgp.util.Event
import kotlinx.coroutines.launch

class MachineViewModel(repository: MachineRepository) : ViewModel()
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

    private val _machineClicked = MutableLiveData<Event<Long>>()
    val machineClicked: LiveData<Event<Long>> = _machineClicked

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
}