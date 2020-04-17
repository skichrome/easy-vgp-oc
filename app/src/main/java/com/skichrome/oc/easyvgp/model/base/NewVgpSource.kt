package com.skichrome.oc.easyvgp.model.base

import com.skichrome.oc.easyvgp.model.Results
import com.skichrome.oc.easyvgp.model.local.database.*

interface NewVgpSource
{
    suspend fun getAllControlPointsWithMachineType(machineTypeId: Long): Results<MachineTypeWithControlPoints>
    suspend fun getReportFromDate(date: Long): Results<List<Report>>

    suspend fun insertControlPointData(controlPointsData: ControlPointData): Results<Long>
    suspend fun updateControlPointData(controlPointsData: List<ControlPointData>): Results<Int>
    suspend fun insertMachineCtrlPtDataExtra(controlPointsDataExtra: MachineControlPointDataExtra): Results<Long>
    suspend fun insertMachineCtrlPtDataCrossRef(machineControlPointsData: MachineControlPointData): Results<Long>
}