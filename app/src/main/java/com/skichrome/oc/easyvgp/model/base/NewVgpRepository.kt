package com.skichrome.oc.easyvgp.model.base

import com.skichrome.oc.easyvgp.model.Results
import com.skichrome.oc.easyvgp.model.local.database.ControlPointData
import com.skichrome.oc.easyvgp.model.local.database.MachineTypeWithControlPoints
import com.skichrome.oc.easyvgp.model.local.database.Report
import com.skichrome.oc.easyvgp.model.local.util.ControlPointDataVgp

interface NewVgpRepository
{
    suspend fun getAllControlPointsWithMachineType(machineTypeId: Long): Results<MachineTypeWithControlPoints>
    suspend fun getReportFromDate(date: Long): Results<List<Report>>

    suspend fun insertMachineControlPointData(ctrlPointDataVgp: List<ControlPointDataVgp>, machineId: Long, controlExtraId: Long): Results<List<Long>>
    suspend fun updateControlPointData(ctrlPointData: List<ControlPointData>): Results<Int>
}