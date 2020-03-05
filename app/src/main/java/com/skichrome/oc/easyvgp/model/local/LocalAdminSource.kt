package com.skichrome.oc.easyvgp.model.local

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.skichrome.oc.easyvgp.model.AdminSource
import com.skichrome.oc.easyvgp.model.Results
import com.skichrome.oc.easyvgp.model.Results.Error
import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.local.database.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LocalAdminSource(
    private val machineTypeDao: MachineTypeDao,
    private val controlPointDao: ControlPointDao,
    private val machineTypeControlPointDao: MachineTypeControlPointCrossRefDao,
    private val dispatchers: CoroutineDispatcher = Dispatchers.IO
) : AdminSource
{
    override fun observeMachineType(): LiveData<Results<List<MachineType>>> = machineTypeDao.observeMachineTypes().map { Success(it) }
    override fun observeControlPoints(): LiveData<Results<List<ControlPoint>>> = controlPointDao.observeControlPoints().map { Success(it) }

    override suspend fun getControlPointsFromMachineTypeId(id: Long): Results<MachineTypeWithControlPoints> = withContext(dispatchers) {
        return@withContext try
        {
            Success(machineTypeDao.getMachineTypeWithControlPointsFromMachineTypeId(id))
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

    override suspend fun insertNewControlPoint(controlPoint: ControlPoint): Results<Long> = withContext(dispatchers) {
        return@withContext try
        {
            Success(controlPointDao.insertReplace(controlPoint))
        } catch (e: Exception)
        {
            Error(e)
        }
    }

    override suspend fun insertNewMachineType(machineType: MachineType): Results<Long> = withContext(dispatchers) {
        return@withContext try
        {
            Success(machineTypeDao.insertReplace(machineType))
        } catch (e: Exception)
        {
            Error(e)
        }
    }

    override suspend fun updateControlPoint(controlPoint: ControlPoint): Results<Int> = withContext(dispatchers) {
        return@withContext try
        {
            Success(controlPointDao.update(controlPoint))
        } catch (e: Exception)
        {
            Error(e)
        }
    }

    override suspend fun insertNewMachineTypeControlPoint(machineTypeWithControlPoints: MachineTypeWithControlPoints): Results<List<Long>> =
        withContext(dispatchers) {
            return@withContext try
            {
                machineTypeControlPointDao.deleteMatchMachineTypeId(machineTypeWithControlPoints.machineType.id)

                val idList = mutableListOf<Long>()
                machineTypeWithControlPoints.controlPoints.forEach {
                    val result = machineTypeControlPointDao.insertReplace(
                        MachineTypeControlPointCrossRef(
                            ctrlPointId = it.id,
                            machineId = machineTypeWithControlPoints.machineType.id
                        )
                    )
                    idList.add(result)
                }
                Success(idList)
            } catch (e: Exception)
            {
                Error(e)
            }
        }

    override suspend fun getAllMachineType(): Results<List<MachineType>> =
        throw NotImplementedError("Not implemented for local source. Use observeMachineType() method instead")

    override suspend fun getAllControlPoints(): Results<List<ControlPoint>> =
        throw NotImplementedError("Not implemented for local source. Use observeMachineType() method instead")
}