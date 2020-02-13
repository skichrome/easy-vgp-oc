package com.skichrome.oc.easyvgp.model

import androidx.lifecycle.LiveData
import com.skichrome.oc.easyvgp.model.local.database.MachineType
import com.skichrome.oc.easyvgp.model.local.database.Machines

interface MachineRepository
{
    fun observeMachines(): LiveData<Results<List<Machines>>>
    fun observeMachineTypes(): LiveData<Results<List<MachineType>>>

    suspend fun getMachineById(machineId: Long): Results<Machines>

    suspend fun insertNewMachine(machines: Machines): Results<Long>
    suspend fun updateMachine(machines: Machines): Results<Int>
}