package com.skichrome.oc.easyvgp.model.local

import com.skichrome.oc.easyvgp.model.Results
import com.skichrome.oc.easyvgp.model.Results.Error
import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.VgpListSource
import com.skichrome.oc.easyvgp.model.local.database.MachineControlPointDataDao
import com.skichrome.oc.easyvgp.model.local.database.VgpListItem
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LocalVgpListSource(
    private val machineControlPointDataDao: MachineControlPointDataDao,
    private val dispatchers: CoroutineDispatcher = Dispatchers.IO
) : VgpListSource
{
    override suspend fun getAllReports(machineId: Long): Results<List<VgpListItem>> = withContext(dispatchers) {
        return@withContext try
        {
            Success(machineControlPointDataDao.getCtrlPointDataFromMachineId(machineId))
        }
        catch (e: Exception)
        {
            Error(e)
        }
    }
}