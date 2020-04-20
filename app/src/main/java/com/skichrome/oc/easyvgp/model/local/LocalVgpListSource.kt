package com.skichrome.oc.easyvgp.model.local

import com.skichrome.oc.easyvgp.model.Results
import com.skichrome.oc.easyvgp.model.Results.Error
import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.base.VgpListSource
import com.skichrome.oc.easyvgp.model.local.database.*
import com.skichrome.oc.easyvgp.util.NotImplementedException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class LocalVgpListSource(
    private val userDao: UserDao,
    private val companyDao: CompanyDao,
    private val customerDao: CustomerDao,
    private val machineDao: MachineDao,
    private val machineTypeDao: MachineTypeDao,
    private val machineControlPointDataDao: MachineControlPointDataDao,
    private val machineCtrlPtExtraDao: MachineControlPointDataExtraDao,
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