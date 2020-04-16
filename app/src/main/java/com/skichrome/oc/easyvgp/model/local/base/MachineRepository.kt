package com.skichrome.oc.easyvgp.model.local.base

import androidx.lifecycle.LiveData
import com.skichrome.oc.easyvgp.model.Results
import com.skichrome.oc.easyvgp.model.local.database.Machine
import com.skichrome.oc.easyvgp.model.local.database.MachineType

interface MachineRepository
{
    fun observeMachines(): LiveData<Results<List<Machine>>>
    fun observeMachineTypes(): LiveData<Results<List<MachineType>>>

    suspend fun getMachineById(machineId: Long): Results<Machine>

    suspend fun insertNewMachine(machine: Machine): Results<Long>
    suspend fun updateMachine(machine: Machine): Results<Int>
}