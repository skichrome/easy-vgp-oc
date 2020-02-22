package com.skichrome.oc.easyvgp.model.local

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.skichrome.oc.easyvgp.model.AdminSource
import com.skichrome.oc.easyvgp.model.Results
import com.skichrome.oc.easyvgp.model.Results.Error
import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.local.database.MachineType
import com.skichrome.oc.easyvgp.model.local.database.MachineTypeDao
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LocalAdminSource(
    private val machineTypeDao: MachineTypeDao,
    private val dispatchers: CoroutineDispatcher = Dispatchers.IO
) : AdminSource
{
    override fun observeMachineType(): LiveData<Results<List<MachineType>>> = machineTypeDao.observeMachineTypes().map { Success(it) }

    override suspend fun insertOrUpdateMachineType(machineType: List<MachineType>): Results<List<Long>> = withContext(dispatchers) {
        return@withContext try
        {
            Success(machineTypeDao.insertReplace(*machineType.toTypedArray()))
        } catch (e: Exception)
        {
            Error(e)
        }
    }

    override suspend fun updateMachineType(machineType: MachineType): Results<Int> = withContext(dispatchers) {
        return@withContext try
        {
            Success(machineTypeDao.update(machineType))
        } catch (e: Exception)
        {
            Error(e)
        }
    }

    override suspend fun insertNewMachineType(machineType: MachineType): Results<String> =
        throw Exception("Not implemented for local source. Use insertOrUpdateMachineType() method instead")

    override suspend fun getAllMachineType(): Results<List<MachineType>> =
        throw Exception("Not implemented for local source. Use observeMachineType() method instead")
}