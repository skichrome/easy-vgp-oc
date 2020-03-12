package com.skichrome.oc.easyvgp.model

import com.skichrome.oc.easyvgp.model.local.database.ControlPointData
import com.skichrome.oc.easyvgp.model.local.database.MachineControlPointData
import com.skichrome.oc.easyvgp.model.local.database.MachineTypeWithControlPoints

interface NewVgpSource
{
    suspend fun getAllControlPointsWithMachineType(machineTypeId: Long): Results<MachineTypeWithControlPoints>

    suspend fun insertControlPointData(controlPointsData: ControlPointData): Results<Long>
    suspend fun insertMachineCtrlPtDataCrossRef(machineControlPointsData: MachineControlPointData): Results<Long>
}