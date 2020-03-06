package com.skichrome.oc.easyvgp.model

import androidx.lifecycle.LiveData
import com.skichrome.oc.easyvgp.model.local.database.Machine
import com.skichrome.oc.easyvgp.model.local.database.MachineType

class DefaultMachineRepository(private val localSource: MachineSource) : MachineRepository
{
    override fun observeMachines(): LiveData<Results<List<Machine>>> = localSource.observeMachines()
    override fun observeMachineTypes(): LiveData<Results<List<MachineType>>> = localSource.observeMachineTypes()

    override suspend fun getMachineById(machineId: Long): Results<Machine> = localSource.getMachineById(machineId)

    override suspend fun insertNewMachine(machine: Machine): Results<Long> = localSource.insertNewMachine(machine)
    override suspend fun updateMachine(machine: Machine): Results<Int> = localSource.updateMachine(machine)
}