package com.skichrome.oc.easyvgp.model.local

import com.skichrome.oc.easyvgp.model.NewVgpSource
import com.skichrome.oc.easyvgp.model.Results
import com.skichrome.oc.easyvgp.model.Results.Error
import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.local.database.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LocalNewVgpSource(
    private val machineTypeDao: MachineTypeDao,
    private val ctrlPointDataDao: ControlPointDataDao,
    private val machineCtrlPointDao: MachineControlPointDataDao,
    private val dispatchers: CoroutineDispatcher = Dispatchers.IO
) : NewVgpSource
{
    override suspend fun getAllControlPointsWithMachineType(machineTypeId: Long): Results<MachineTypeWithControlPoints> = withContext(dispatchers) {
        return@withContext try
        {
            Success(machineTypeDao.getMachineTypeWithControlPointsFromMachineTypeId(machineTypeId))
        }
        catch (e: Exception)
        {
            Error(e)
        }
    }

    override suspend fun insertControlPointData(controlPointsData: ControlPointData): Results<Long> = withContext(dispatchers) {
        return@withContext try
        {
            val result = ctrlPointDataDao.insertReplace(controlPointsData)
            Success(result)
        }
        catch (e: Exception)
        {
            Error(e)
        }
    }

    override suspend fun insertMachineCtrlPtDataCrossRef(machineControlPointsData: MachineControlPointData): Results<Long> =
        withContext(dispatchers) {
            return@withContext try
            {
                val result = machineCtrlPointDao.insertReplace(machineControlPointsData)
                Success(result)
            }
            catch (e: Exception)
            {
                Error(e)
            }
        }
}