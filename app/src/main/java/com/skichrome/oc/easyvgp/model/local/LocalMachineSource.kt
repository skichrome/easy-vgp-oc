package com.skichrome.oc.easyvgp.model.local

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.skichrome.oc.easyvgp.model.Results
import com.skichrome.oc.easyvgp.model.Results.Error
import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.base.MachineSource
import com.skichrome.oc.easyvgp.model.local.database.Machine
import com.skichrome.oc.easyvgp.model.local.database.MachineDao
import com.skichrome.oc.easyvgp.model.local.database.MachineType
import com.skichrome.oc.easyvgp.model.local.database.MachineTypeDao
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LocalMachineSource(
    private val machineDao: MachineDao,
    private val machineTypeDao: MachineTypeDao,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : MachineSource
{
    override fun observeMachines(): LiveData<Results<List<Machine>>> = machineDao.observeMachines().map { Success(it) }
    override fun observeMachineTypes(): LiveData<Results<List<MachineType>>> = machineTypeDao.observeMachineTypes().map { Success(it) }

    override suspend fun getMachineById(machineId: Long): Results<Machine> = withContext(dispatcher) {
        return@withContext try
        {
            val result = machineDao.getMachineById(machineId)
            Success(result)
        }
        catch (e: Exception)
        {
            Error(e)
        }
    }

    override suspend fun insertNewMachine(machine: Machine): Results<Long> = withContext(dispatcher) {
        return@withContext try
        {
            Success(machineDao.insertIgnore(machine))
        }
        catch (e: Exception)
        {
            Error(e)
        }
    }

    override suspend fun updateMachine(machine: Machine): Results<Int> = withContext(dispatcher) {
        return@withContext try
        {
            Success(machineDao.update(machine))
        }
        catch (e: Exception)
        {
            Error(e)
        }
    }
}