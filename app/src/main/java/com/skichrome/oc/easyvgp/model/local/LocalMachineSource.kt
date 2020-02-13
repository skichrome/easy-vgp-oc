package com.skichrome.oc.easyvgp.model.local

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.skichrome.oc.easyvgp.model.MachineSource
import com.skichrome.oc.easyvgp.model.Results
import com.skichrome.oc.easyvgp.model.Results.Error
import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.local.database.MachineType
import com.skichrome.oc.easyvgp.model.local.database.MachineTypeDao
import com.skichrome.oc.easyvgp.model.local.database.Machines
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
    override fun observeMachines(): LiveData<Results<List<Machines>>> = machinesDao.observeMachines().map { Success(it) }
    override fun observeMachineTypes(): LiveData<Results<List<MachineType>>> = machineTypeDao.observeMachineTypes().map { Success(it) }

    override suspend fun getMachineById(machineId: Long): Results<Machines> = withContext(dispatcher) {
        return@withContext try
        {
            val result = machinesDao.getMachineById(machineId)
            Success(result)
        } catch (e: Exception)
        {
            Error(e)
        }
    }

    override suspend fun insertNewMachine(machines: Machines): Results<Long> = withContext(dispatcher) {
        return@withContext try
        {
            Success(machinesDao.insertIgnore(machines))
        } catch (e: Exception)
        {
            Error(e)
        }
    }

    override suspend fun updateMachine(machines: Machines): Results<Int> = withContext(dispatcher) {
        return@withContext try
        {
            Success(machinesDao.update(machines))
        } catch (e: Exception)
        {
            Error(e)
        }
    }
}