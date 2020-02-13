package com.skichrome.oc.easyvgp.model

import androidx.lifecycle.LiveData
import com.skichrome.oc.easyvgp.model.local.database.MachineType
import com.skichrome.oc.easyvgp.model.local.database.Machines

interface MachineRepository
{
    fun observeMachines(): LiveData<Results<List<Machines>>>
    fun observeMachineTypes(): LiveData<Results<List<MachineType>>>
}