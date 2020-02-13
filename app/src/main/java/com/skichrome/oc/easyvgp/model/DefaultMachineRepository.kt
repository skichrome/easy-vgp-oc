package com.skichrome.oc.easyvgp.model

import androidx.lifecycle.LiveData
import com.skichrome.oc.easyvgp.model.local.database.MachineType
import com.skichrome.oc.easyvgp.model.local.database.Machines

class DefaultMachineRepository(private val localSource: MachineSource) : MachineRepository
{
    override fun observeMachines(): LiveData<Results<List<Machines>>> = localSource.observeMachines()
    override fun observeMachineTypes(): LiveData<Results<List<MachineType>>> = localSource.observeMachineTypes()

    override suspend fun getMachineById(machineId: Long): Results<Machines> = localSource.getMachineById(machineId)

    override suspend fun insertNewMachine(machines: Machines): Results<Long> = localSource.insertNewMachine(machines)
    override suspend fun updateMachine(machines: Machines): Results<Int> = localSource.updateMachine(machines)
}