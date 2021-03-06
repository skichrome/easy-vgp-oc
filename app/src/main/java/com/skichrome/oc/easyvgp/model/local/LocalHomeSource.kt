package com.skichrome.oc.easyvgp.model.local

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.skichrome.oc.easyvgp.model.Results
import com.skichrome.oc.easyvgp.model.Results.Error
import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.base.HomeSource
import com.skichrome.oc.easyvgp.model.local.database.*
import com.skichrome.oc.easyvgp.util.NotImplementedException
import kotlinx.coroutines.*

class LocalHomeSource(
    private val companyDao: CompanyDao,
    private val userDao: UserDao,
    private val controlPointDao: ControlPointDao,
    private val machineTypeDao: MachineTypeDao,
    private val machineTypeControlPointCrossRefDao: MachineTypeControlPointCrossRefDao,
    private val machineCtrlPtDataExtraDao: MachineControlPointDataExtraDao,
    private val machineControlPointDataDao: MachineControlPointDataDao,
    private val dispatchers: CoroutineDispatcher = Dispatchers.IO
) : HomeSource
{
    override fun observeHomeReportsEndValidityDate(): LiveData<Results<List<HomeEndValidityReportItem>>> =
        machineControlPointDataDao.observeEndValidityReports().map { Success(it) }

    override suspend fun getAllUserAndCompany(): Results<List<UserAndCompany>> = withContext(dispatchers) {
        return@withContext try
        {
            Success(userDao.getAllUserAndCompanies())
        }
        catch (e: Exception)
        {
            Error(e)
        }
    }

    override suspend fun insertNewUserAndCompany(userAndCompany: UserAndCompany): Results<Long> = withContext(dispatchers) {
        return@withContext try
        {
            userAndCompany.user.companyId = companyDao.insertReplace(userAndCompany.company)
            val result = userDao.insertReplace(userAndCompany.user)
            Success(result)
        }
        catch (e: Exception)
        {
            Error(e)
        }
    }

    override suspend fun updateUserAndCompany(userAndCompany: UserAndCompany): Results<Int> = withContext(dispatchers) {
        return@withContext try
        {
            companyDao.update(userAndCompany.company)
            val result = userDao.update(userAndCompany.user)
            Success(result)
        }
        catch (e: Exception)
        {
            Error(e)
        }
    }

    override suspend fun updateExtraEmailSentStatus(extraId: Long): Results<Int> = withContext(dispatchers) {
        return@withContext try
        {
            Success(machineCtrlPtDataExtraDao.updateExtraEmailStatus(extraId))
        }
        catch (e: Exception)
        {
            Error(e)
        }
    }

    override suspend fun insertControlPointsAsync(ctrlPoints: List<ControlPoint>): Deferred<Results<List<Long>>> = withContext(dispatchers) {
        async {
            try
            {
                val results = controlPointDao.insertReplace(*ctrlPoints.toTypedArray())
                Success(results)
            }
            catch (e: Exception)
            {
                Error(e)
            }
        }
    }

    override suspend fun insertMachineTypesAsync(machineTypes: List<MachineType>): Deferred<Results<List<Long>>> = withContext(dispatchers) {
        async {
            try
            {
                val results = machineTypeDao.insertReplace(*machineTypes.toTypedArray())
                Success(results)
            }
            catch (e: Exception)
            {
                Error(e)
            }
        }
    }

    override suspend fun insertMachineTypesWithCtrlPoints(machineTypesWithCtrlPoints: List<MachineTypeWithControlPoints>): Results<List<Long>> =
        withContext(dispatchers) {
            try
            {
                machineTypeControlPointCrossRefDao.deleteAll()
                val dataToInsert = mutableListOf<MachineTypeControlPointCrossRef>()
                machineTypesWithCtrlPoints.map { machineTypesWithCtrlPoints ->
                    machineTypesWithCtrlPoints.controlPoints.map { ctrlPoint ->
                        dataToInsert.add(
                            MachineTypeControlPointCrossRef(
                                machineTypeId = machineTypesWithCtrlPoints.machineType.id,
                                ctrlPointId = ctrlPoint.id
                            )
                        )
                    }
                }
                val results = machineTypeControlPointCrossRefDao.insertReplace(*dataToInsert.toTypedArray())
                Success(results)
            }
            catch (e: Exception)
            {
                Error(e)
            }
        }

    override suspend fun getAllControlPointsAsync(): Deferred<Results<List<ControlPoint>>> = withContext(dispatchers) {
        async { Error(NotImplementedException("Not available on local database for home viewModel")) }
    }

    override suspend fun getAllMachineTypesAsync(): Deferred<Results<List<MachineType>>> = withContext(dispatchers) {
        async { Error(NotImplementedException("Not available on local database for home viewModel")) }
    }

    override suspend fun getAllMachineTypeCtrlPointsAsync(): Deferred<Results<List<MachineTypeWithControlPoints>>> = withContext(dispatchers) {
        async { Error(NotImplementedException("Not available on local database for home viewModel")) }
    }
}