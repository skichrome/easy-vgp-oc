package com.skichrome.oc.easyvgp.model.local

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.skichrome.oc.easyvgp.model.MachineSource
import com.skichrome.oc.easyvgp.model.Results
import com.skichrome.oc.easyvgp.model.local.database.MachineType
import com.skichrome.oc.easyvgp.model.local.database.MachineTypeDao
import com.skichrome.oc.easyvgp.model.local.database.Machines
import com.skichrome.oc.easyvgp.model.local.database.MachinesDao

class LocalMachineSource(private val machinesDao: MachinesDao, private val machineTypeDao: MachineTypeDao) : MachineSource
{
    override fun observeMachines(): LiveData<Results<List<Machines>>> = machinesDao.observeMachines().map { Results.Success(it) }
    override fun observeMachineTypes(): LiveData<Results<List<MachineType>>> = machineTypeDao.observeMachineTypes().map { Results.Success(it) }
}