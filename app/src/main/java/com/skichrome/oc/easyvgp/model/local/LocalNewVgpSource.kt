package com.skichrome.oc.easyvgp.model.local

import com.skichrome.oc.easyvgp.model.Results
import com.skichrome.oc.easyvgp.model.Results.Error
import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.base.NewVgpSource
import com.skichrome.oc.easyvgp.model.local.database.*
import com.skichrome.oc.easyvgp.util.ItemNotFoundException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LocalNewVgpSource(
    private val machineTypeDao: MachineTypeDao,
    private val ctrlPointDataDao: ControlPointDataDao,
    private val machineCtrlPtExtraDao: MachineControlPointDataExtraDao,
    private val machineCtrlPointDao: MachineControlPointDataDao,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : NewVgpSource
{
    override suspend fun getAllControlPointsWithMachineType(machineTypeId: Long): Results<MachineTypeWithControlPoints> = withContext(dispatcher) {
        return@withContext try
        {
            Success(machineTypeDao.getMachineTypeWithControlPointsFromMachineTypeId(machineTypeId))
        }
        catch (e: Exception)
        {
            Error(e)
        }
    }

    override suspend fun getReportFromDate(date: Long): Results<List<Report>> = withContext(dispatcher) {
        return@withContext try
        {
            val result = machineCtrlPointDao.getPreviouslyInsertedReport(date)
            if (result.isEmpty())
                Error(ItemNotFoundException("This report was not found"))
            Success(result)
        }
        catch (e: Exception)
        {
            Error(e)
        }
    }

    override suspend fun insertControlPointData(controlPointsData: ControlPointData): Results<Long> = withContext(dispatcher) {
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

    override suspend fun updateControlPointData(controlPointsData: List<ControlPointData>): Results<Int> = withContext(dispatcher) {
        return@withContext try
        {
            val result = ctrlPointDataDao.update(*controlPointsData.toTypedArray())
            Success(result)
        }
        catch (e: Exception)
        {
            Error(e)
        }
    }

    override suspend fun updateControlResult(extraId: Long, controlResult: ControlResult): Results<Int> = withContext(dispatcher) {
        return@withContext try
        {
            Success(machineCtrlPtExtraDao.updateControlResult(extraId, controlResult))
        }
        catch (e: Exception)
        {
            Error(e)
        }
    }

    override suspend fun insertMachineCtrlPtDataExtra(controlPointsDataExtra: MachineControlPointDataExtra): Results<Long> =
        withContext(dispatcher) {
            return@withContext try
            {
                Success(machineCtrlPtExtraDao.insertReplace(controlPointsDataExtra))
            }
            catch (e: Exception)
            {
                Error(e)
            }
        }

    override suspend fun insertMachineCtrlPtDataCrossRef(machineControlPointsData: MachineControlPointData): Results<Long> =
        withContext(dispatcher) {
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