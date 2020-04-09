package com.skichrome.oc.easyvgp.model.local

import android.net.Uri
import com.skichrome.oc.easyvgp.model.Results
import com.skichrome.oc.easyvgp.model.Results.Error
import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.VgpListSource
import com.skichrome.oc.easyvgp.model.local.database.*
import com.skichrome.oc.easyvgp.util.NotImplementedException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LocalVgpListSource(
    private val userDao: UserDao,
    private val customerDao: CustomerDao,
    private val machineDao: MachineDao,
    private val machineTypeDao: MachineTypeDao,
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

    override suspend fun getReportFromDate(date: Long): Results<List<Report>> = withContext(dispatchers) {
        return@withContext try
        {
            val result = machineControlPointDataDao.getPreviouslyInsertedReport(date)
            Success(result)
        }
        catch (e: Exception)
        {
            Error(e)
        }
    }

    override suspend fun getUserFromId(id: Long): Results<UserAndCompany> = withContext(dispatchers) {
        return@withContext try
        {
            Success(userDao.getUserFromId(id))
        }
        catch (e: Exception)
        {
            Error(e)
        }
    }

    override suspend fun getCustomerFromId(customerId: Long): Results<Customer> = withContext(dispatchers) {
        return@withContext try
        {
            Success(customerDao.getCustomerById(customerId))
        }
        catch (e: Exception)
        {
            Error(e)
        }
    }

    override suspend fun getMachineFromId(machineId: Long): Results<Machine> = withContext(dispatchers) {
        return@withContext try
        {
            Success(machineDao.getMachineById(machineId))
        }
        catch (e: Exception)
        {
            Error(e)
        }
    }

    override suspend fun getMachineTypeFromId(machineTypeId: Long): Results<MachineType> = withContext(dispatchers) {
        return@withContext try
        {
            Success(machineTypeDao.getMachineTypeFromId(machineTypeId))
        }
        catch (e: Exception)
        {
            Error(e)
        }
    }

    override suspend fun updateMachine(machine: Machine): Results<Int> = withContext(dispatchers) {
        return@withContext try
        {
            Success(machineDao.update(machine))
        }
        catch (e: Exception)
        {
            Error(e)
        }
    }

    override suspend fun uploadImageToStorage(userUid: String, filePath: String): Results<Uri> =
        Error(NotImplementedException("Method not available on local VGPList source"))

    override suspend fun generateReport(
        reportDate: Long,
        user: UserAndCompany,
        customer: Customer,
        machine: Machine,
        machineType: MachineType,
        reports: List<Report>
    ): Results<Boolean> =
        Error(NotImplementedException("Method not available on local VGPList source"))
}