package com.skichrome.oc.easyvgp.model

import androidx.lifecycle.LiveData
import com.skichrome.oc.easyvgp.model.local.database.MachineType

interface AdminRepository
{
    fun observeMachineType(): LiveData<Results<List<MachineType>>>
    suspend fun getAllMachineType(): Results<List<MachineType>>

    suspend fun insertNewMachineType(machineType: MachineType): Results<String>
    suspend fun updateMachineType(machineType: MachineType): Results<Int>
}