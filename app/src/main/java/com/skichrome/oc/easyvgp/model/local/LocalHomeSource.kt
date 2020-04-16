package com.skichrome.oc.easyvgp.model.local

import com.skichrome.oc.easyvgp.model.Results
import com.skichrome.oc.easyvgp.model.Results.Error
import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.local.base.HomeSource
import com.skichrome.oc.easyvgp.model.local.database.*
import com.skichrome.oc.easyvgp.util.AppCoroutinesConfiguration
import com.skichrome.oc.easyvgp.util.NotImplementedException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class LocalHomeSource(
    private val companyDao: CompanyDao,
    private val userDao: UserDao,
    private val controlPointDao: ControlPointDao,
    private val machineTypeDao: MachineTypeDao,
    private val machineTypeControlPointCrossRefDao: MachineTypeControlPointCrossRefDao,
    private val dispatchers: CoroutineDispatcher = AppCoroutinesConfiguration.ioDispatchers
) : HomeSource
{
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