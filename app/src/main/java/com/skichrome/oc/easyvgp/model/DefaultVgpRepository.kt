package com.skichrome.oc.easyvgp.model

import androidx.lifecycle.LiveData
import com.skichrome.oc.easyvgp.model.local.database.ControlPointChoicePossibility
import com.skichrome.oc.easyvgp.model.local.database.ControlPointVerificationType
import com.skichrome.oc.easyvgp.model.local.database.MachineTypeWithControlPoints

class DefaultVgpRepository(private val localSource: VgpSource) : VgpRepository
{
    override fun observeChoicePossibilities(): LiveData<Results<List<ControlPointChoicePossibility>>> = localSource.observeChoicePossibilities()
    override fun observeVerificationsType(): LiveData<Results<List<ControlPointVerificationType>>> = localSource.observeVerificationsType()

    override suspend fun getAllControlPointsWithMachineType(machineTypeId: Long): Results<MachineTypeWithControlPoints> =
        localSource.getAllControlPointsWithMachineType(machineTypeId)
}