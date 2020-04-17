package com.skichrome.oc.easyvgp.model.source

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.skichrome.oc.easyvgp.model.Results
import com.skichrome.oc.easyvgp.model.Results.Error
import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.base.MachineSource
import com.skichrome.oc.easyvgp.model.local.database.Machine
import com.skichrome.oc.easyvgp.model.local.database.MachineType
import com.skichrome.oc.easyvgp.util.ItemNotFoundException
import kotlinx.coroutines.runBlocking

class FakeMachineDataSource(
    private val machineDataService: LinkedHashMap<Long, Machine> = LinkedHashMap(),
    private val machineTypeDataService: LinkedHashMap<Long, MachineType> = LinkedHashMap()
) : MachineSource
{
    // =================================
    //              Fields
    // =================================

    private val observableMachine = MutableLiveData<List<Machine>>()
    private val observableMachineType = MutableLiveData<List<MachineType>>()

    // =================================
    //        Superclass Methods
    // =================================

    override fun observeMachines(): LiveData<Results<List<Machine>>> = observableMachine.map { Success(it) }
    override fun observeMachineTypes(): LiveData<Results<List<MachineType>>> = observableMachineType.map { Success(it) }

    override suspend fun getMachineById(machineId: Long): Results<Machine>
    {
        val result = machineDataService[machineId]
        return if (result != null)
            Success(result)
        else
            Error(ItemNotFoundException("Machine does not exist"))
    }

    override suspend fun insertNewMachine(machine: Machine): Results<Long>
    {
        machineDataService[machine.machineId] = machine
        return Success(machine.machineId)
    }

    override suspend fun updateMachine(machine: Machine): Results<Int>
    {
        val machineExist = machineDataService[machine.machineId]
        return if (machineExist != null)
        {
            machineDataService[machine.machineId] = machine
            Success(1)
        } else
            Error(ItemNotFoundException("Machine to update does not exist"))
    }

    // =================================
    //              Methods
    // =================================

    fun refresh() = runBlocking {
        observableMachine.value = machineDataService.values.toList().sortedBy { it.machineId }
        observableMachineType.value = machineTypeDataService.values.toList().sortedBy { it.id }
    }
}