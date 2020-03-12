package com.skichrome.oc.easyvgp.model

import androidx.lifecycle.LiveData
import com.skichrome.oc.easyvgp.model.local.database.ControlPointChoicePossibility
import com.skichrome.oc.easyvgp.model.local.database.ControlPointVerificationType
import com.skichrome.oc.easyvgp.model.local.database.MachineTypeWithControlPoints

interface VgpRepository
{
    fun observeChoicePossibilities(): LiveData<Results<List<ControlPointChoicePossibility>>>
    fun observeVerificationsType(): LiveData<Results<List<ControlPointVerificationType>>>

    suspend fun getAllControlPointsWithMachineType(machineTypeId: Long): Results<MachineTypeWithControlPoints>
}