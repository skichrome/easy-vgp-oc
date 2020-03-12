package com.skichrome.oc.easyvgp.model.local

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.skichrome.oc.easyvgp.model.Results
import com.skichrome.oc.easyvgp.model.Results.Error
import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.VgpSource
import com.skichrome.oc.easyvgp.model.local.database.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LocalVgpSource(
    private val machineTypeDao: MachineTypeDao,
    private val choicePossibilityDao: ControlPointChoicePossibilityDao,
    private val verificationTypeDao: ControlPointVerificationTypeDao,
    private val dispatchers: CoroutineDispatcher = Dispatchers.IO
) : VgpSource
{
    override fun observeChoicePossibilities(): LiveData<Results<List<ControlPointChoicePossibility>>> =
        choicePossibilityDao.observePossibilities().map { Success(it) }

    override fun observeVerificationsType(): LiveData<Results<List<ControlPointVerificationType>>> =
        verificationTypeDao.observeVerificationsType().map { Success(it) }

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
}