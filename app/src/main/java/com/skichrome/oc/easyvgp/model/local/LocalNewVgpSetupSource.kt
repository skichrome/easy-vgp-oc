package com.skichrome.oc.easyvgp.model.local

import com.skichrome.oc.easyvgp.model.Results
import com.skichrome.oc.easyvgp.model.Results.Error
import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.base.NewVgpSetupSource
import com.skichrome.oc.easyvgp.model.local.database.MachineControlPointDataExtra
import com.skichrome.oc.easyvgp.model.local.database.MachineControlPointDataExtraDao
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LocalNewVgpSetupSource(
    private val machineCtrlPtExtraDao: MachineControlPointDataExtraDao,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : NewVgpSetupSource
{
    override suspend fun getPreviousCtrlPtDataExtraFromDate(date: Long): Results<MachineControlPointDataExtra> = withContext(dispatcher) {
        return@withContext try
        {
            Success(machineCtrlPtExtraDao.getPreviouslyCreatedExtras(date))
        }
        catch (e: Exception)
        {
            Error(e)
        }
    }

    override suspend fun insertMachineCtrlPtDataExtra(controlPointsDataExtra: MachineControlPointDataExtra): Results<Long> = withContext(dispatcher) {
        return@withContext try
        {
            Success(machineCtrlPtExtraDao.insertReplace(controlPointsDataExtra))
        }
        catch (e: Exception)
        {
            Error(e)
        }
    }

    override suspend fun updateMachineCtrlPtDataExtra(controlPointsDataExtra: MachineControlPointDataExtra): Results<Int> = withContext(dispatcher) {
        return@withContext try
        {
            Success(machineCtrlPtExtraDao.update(controlPointsDataExtra))
        }
        catch (e: Exception)
        {
            Error(e)
        }
    }
}