package com.skichrome.oc.easyvgp.model

import com.skichrome.oc.easyvgp.model.local.database.MachineTypeWithControlPoints
import com.skichrome.oc.easyvgp.model.local.util.ControlPointDataVgp

interface NewVgpRepository
{
    suspend fun getAllControlPointsWithMachineType(machineTypeId: Long): Results<MachineTypeWithControlPoints>

    suspend fun insertMachineControlPointData(ctrlPointDataVgp: List<ControlPointDataVgp>, machineId: Long): Results<List<Long>>
}