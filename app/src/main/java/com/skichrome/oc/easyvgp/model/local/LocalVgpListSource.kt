package com.skichrome.oc.easyvgp.model.local

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.skichrome.oc.easyvgp.model.Results
import com.skichrome.oc.easyvgp.model.Results.Error
import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.base.VgpListSource
import com.skichrome.oc.easyvgp.model.local.database.MachineControlPointDataDao
import com.skichrome.oc.easyvgp.model.local.database.MachineControlPointDataExtra
import com.skichrome.oc.easyvgp.model.local.database.MachineControlPointDataExtraDao
import com.skichrome.oc.easyvgp.model.local.database.VgpListItem
import com.skichrome.oc.easyvgp.util.NotImplementedException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class LocalVgpListSource(
    private val machineControlPointDataDao: MachineControlPointDataDao,
    private val machineCtrlPtExtraDao: MachineControlPointDataExtraDao,
    private val dispatchers: CoroutineDispatcher = Dispatchers.IO
) : VgpListSource
{
    override fun observeReports(): LiveData<Results<List<VgpListItem>>> = machineControlPointDataDao.observeCtrlPtData().map { Success(it) }

    override suspend fun getMachineCtrlPtExtraFromId(id: Long): Results<MachineControlPointDataExtra> = withContext(dispatchers) {
        return@withContext try
        {
            Success(machineCtrlPtExtraDao.getExtraFromId(id))
        }
        catch (e: Exception)
        {
            Error(e)
        }
    }

    override suspend fun updateMachineCtrlPtExtra(extra: MachineControlPointDataExtra): Results<Int> = withContext(dispatchers) {
        return@withContext try
        {
            Success(machineCtrlPtExtraDao.update(extra))
        }
        catch (e: Exception)
        {
            Error(e)
        }
    }

    override suspend fun downloadReportFromStorage(remotePath: String?, destinationFile: File): Results<File> =
        Error(NotImplementedException("Method not available on local VGPList source"))
}