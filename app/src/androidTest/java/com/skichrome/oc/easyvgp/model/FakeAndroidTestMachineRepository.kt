package com.skichrome.oc.easyvgp.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.skichrome.oc.easyvgp.model.Results.Error
import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.local.database.Machine
import com.skichrome.oc.easyvgp.model.local.database.MachineType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

class FakeAndroidTestMachineRepository : MachineRepository
{
    // =================================
    //              Fields
    // =================================

    private val machineServiceData: LinkedHashMap<Long, Machine> = LinkedHashMap()
    private val observableMachinesData = MutableLiveData<List<Machine>>()

    private val machineTypeServiceData: LinkedHashMap<Long, MachineType> = LinkedHashMap()
    private val observableMachineTypeData = MutableLiveData<List<MachineType>>()

    // =================================
    //        Superclass Methods
    // =================================

    override fun observeMachines(): LiveData<Results<List<Machine>>> = observableMachinesData.map { Success(it) }

    override fun observeMachineTypes(): LiveData<Results<List<MachineType>>> = observableMachineTypeData.map { Success(it) }

    override suspend fun getMachineById(machineId: Long): Results<Machine>
    {
        val result = machineServiceData[machineId]
        return if (result == null)
            Error(Exception("Not found"))
        else
            Success(result)
    }

    override suspend fun insertNewMachine(machine: Machine): Results<Long>
    {
        machineServiceData[machine.machineId] = machine
        return Success(machine.machineId)
    }

    override suspend fun updateMachine(machine: Machine): Results<Int>
    {
        val machineToUpdate = machineServiceData[machine.machineId]
        return machineToUpdate?.let {
            machineServiceData[machine.machineId] = machine
            Success(1)
        } ?: Error(Exception("Machine not found"))
    }

    // =================================
    //              Methods
    // =================================

    fun insertMachineTypes(machineTypes: List<MachineType>) = machineTypes.forEach { machineTypeServiceData[it.id] = it }

    fun refresh() = runBlocking(Dispatchers.Main) {
        observableMachinesData.value = machineServiceData.values.toList().sortedBy { it.machineId }
        observableMachineTypeData.value = machineTypeServiceData.values.toList().sortedBy { it.id }
    }
}