package com.skichrome.oc.easyvgp.model.local

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.skichrome.oc.easyvgp.model.MachineSource
import com.skichrome.oc.easyvgp.model.Results
import com.skichrome.oc.easyvgp.model.Results.Error
import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.local.database.Machine
import com.skichrome.oc.easyvgp.model.local.database.MachineType
import com.skichrome.oc.easyvgp.model.local.database.MachineTypeDao
import com.skichrome.oc.easyvgp.model.local.database.MachinesDao
import com.skichrome.oc.easyvgp.util.AppCoroutinesConfiguration
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class LocalMachineSource(
    private val machinesDao: MachinesDao,
    private val machineTypeDao: MachineTypeDao,
    private val dispatcher: CoroutineDispatcher = AppCoroutinesConfiguration.ioDispatchers
) : MachineSource
{
    override fun observeMachines(): LiveData<Results<List<Machine>>> = machinesDao.observeMachines().map { Success(it) }
    override fun observeMachineTypes(): LiveData<Results<List<MachineType>>> = machineTypeDao.observeMachineTypes().map { Success(it) }

    override suspend fun getMachineById(machineId: Long): Results<Machine> = withContext(dispatcher) {
        return@withContext try
        {
            val result = machinesDao.getMachineById(machineId)
            Success(result)
        } catch (e: Exception)
        {
            Error(e)
        }
    }

    override suspend fun insertNewMachine(machine: Machine): Results<Long> = withContext(dispatcher) {
        return@withContext try
        {
            Success(machinesDao.insertIgnore(machine))
        } catch (e: Exception)
        {
            Error(e)
        }
    }

    override suspend fun updateMachine(machine: Machine): Results<Int> = withContext(dispatcher) {
        return@withContext try
        {
            Success(machinesDao.update(machine))
        } catch (e: Exception)
        {
            Error(e)
        }
    }
}