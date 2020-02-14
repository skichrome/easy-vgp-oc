package com.skichrome.oc.easyvgp.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.skichrome.oc.easyvgp.model.Results.Error
import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.local.database.MachineType
import com.skichrome.oc.easyvgp.model.local.database.Machines
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

class FakeAndroidTestMachineRepository : MachineRepository
{
    // =================================
    //              Fields
    // =================================

    private val machinesServiceData: LinkedHashMap<Long, Machines> = LinkedHashMap()
    private val observableMachinesData = MutableLiveData<List<Machines>>()

    private val machineTypeServiceData: LinkedHashMap<Long, MachineType> = LinkedHashMap()
    private val observableMachineTypeData = MutableLiveData<List<MachineType>>()

    // =================================
    //        Superclass Methods
    // =================================

    override fun observeMachines(): LiveData<Results<List<Machines>>> = observableMachinesData.map { Success(it) }

    override fun observeMachineTypes(): LiveData<Results<List<MachineType>>> = observableMachineTypeData.map { Success(it) }

    override suspend fun getMachineById(machineId: Long): Results<Machines>
    {
        val result = machinesServiceData[machineId]
        return if (result == null)
            Error(Exception("Not found"))
        else
            Success(result)
    }

    override suspend fun insertNewMachine(machines: Machines): Results<Long>
    {
        machinesServiceData[machines.machineId] = machines
        return Success(machines.machineId)
    }

    override suspend fun updateMachine(machines: Machines): Results<Int>
    {
        val machineToUpdate = machinesServiceData[machines.machineId]
        return machineToUpdate?.let {
            machinesServiceData[machines.machineId] = machines
            Success(1)
        } ?: Error(Exception("Machine not found"))
    }

    // =================================
    //              Methods
    // =================================

    fun insertMachineTypes(machineTypes: List<MachineType>) = machineTypes.forEach { machineTypeServiceData[it.id] = it }

    fun refresh() = runBlocking(Dispatchers.Main) {
        observableMachinesData.value = machinesServiceData.values.toList().sortedBy { it.machineId }
        observableMachineTypeData.value = machineTypeServiceData.values.toList().sortedBy { it.id }
    }
}