package com.skichrome.oc.easyvgp.model

import androidx.lifecycle.LiveData
import com.skichrome.oc.easyvgp.model.local.database.MachineType
import com.skichrome.oc.easyvgp.model.local.database.Machines

class DefaultMachineRepository(private val localSource: MachineSource) : MachineRepository
{
    override fun observeMachines(): LiveData<Results<List<Machines>>> = localSource.observeMachines()
    override fun observeMachineTypes(): LiveData<Results<List<MachineType>>> = localSource.observeMachineTypes()
}