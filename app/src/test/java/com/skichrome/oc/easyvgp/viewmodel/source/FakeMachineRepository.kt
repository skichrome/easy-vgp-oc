package com.skichrome.oc.easyvgp.viewmodel.source

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.skichrome.oc.easyvgp.model.Results
import com.skichrome.oc.easyvgp.model.Results.Error
import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.base.MachineRepository
import com.skichrome.oc.easyvgp.model.local.database.Machine
import com.skichrome.oc.easyvgp.model.local.database.MachineType
import com.skichrome.oc.easyvgp.util.ItemNotFoundException

class FakeMachineRepository(
    private val machineTypeDataService: LinkedHashMap<Long, MachineType> = LinkedHashMap(),
    private val machinesDataService: LinkedHashMap<Long, Machine> = LinkedHashMap()
) : MachineRepository
{
    // =================================
    //              Fields
    // =================================

    private val observableMachineTypes = MutableLiveData<List<MachineType>>()
    private val observableMachines = MutableLiveData<List<Machine>>()

    // =================================
    //        Superclass Methods
    // =================================

    override fun observeMachines(): LiveData<Results<List<Machine>>> = observableMachines.map { Success(it) }
    override fun observeMachineTypes(): LiveData<Results<List<MachineType>>> = observableMachineTypes.map { Success(it) }

    override suspend fun getMachineById(machineId: Long): Results<Machine> = when (val machine = machinesDataService[machineId])
    {
        null -> Error(ItemNotFoundException("This item doesn't exist in the list"))
        else -> Success(machine)
    }

    override suspend fun insertNewMachine(machine: Machine): Results<Long>
    {
        machinesDataService[machine.machineId] = machine
        return Success(machine.machineId)
    }

    override suspend fun updateMachine(machine: Machine): Results<Int> = when (machinesDataService[machine.machineId])
    {
        null -> Error(ItemNotFoundException("This item doesn't exist in the list"))
        else ->
        {
            machinesDataService[machine.machineId] = machine
            Success(1)
        }
    }

    // =================================
    //              Methods
    // =================================

    fun refresh()
    {
        observableMachineTypes.value = machineTypeDataService.values.toList().sortedBy { it.id }
        observableMachines.value = machinesDataService.values.toList().sortedBy { it.machineId }
    }
}