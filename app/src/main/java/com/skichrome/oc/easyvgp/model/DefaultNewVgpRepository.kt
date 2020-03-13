package com.skichrome.oc.easyvgp.model

import com.skichrome.oc.easyvgp.model.Results.Error
import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.local.database.ControlPointData
import com.skichrome.oc.easyvgp.model.local.database.MachineControlPointData
import com.skichrome.oc.easyvgp.model.local.database.MachineTypeWithControlPoints
import com.skichrome.oc.easyvgp.model.local.database.Report
import com.skichrome.oc.easyvgp.model.local.util.ControlPointDataVgp

class DefaultNewVgpRepository(private val localSource: NewVgpSource) : NewVgpRepository
{
    override suspend fun getAllControlPointsWithMachineType(machineTypeId: Long): Results<MachineTypeWithControlPoints> =
        localSource.getAllControlPointsWithMachineType(machineTypeId)

    override suspend fun getReportFromDate(date: Long): Results<List<Report>> = localSource.getReportFromDate(date)

    override suspend fun insertMachineControlPointData(ctrlPointDataVgp: List<ControlPointDataVgp>, machineId: Long): Results<List<Long>>
    {
        val reportDate = System.currentTimeMillis()
        val idResultList = mutableListOf<Long>()
        ctrlPointDataVgp.map {
            ControlPointData(
                id = 0L,
                ctrlPointPossibility = it.choicePossibilityId,
                ctrlPointRef = it.controlPoint.id,
                ctrlPointVerificationType = it.verificationTypeId,
                comment = it.comment
            )
        }.forEach {
            val results = localSource.insertControlPointData(it)
            if (results is Error)
                return results

            if (results is Success)
            {
                val crossRef = MachineControlPointData(
                    machineId = machineId,
                    ctrlPointDataId = results.data,
                    reportDate = reportDate
                )

                val crossRefResult = localSource.insertMachineCtrlPtDataCrossRef(crossRef)
                if (crossRefResult is Error)
                    return crossRefResult
                idResultList.add(results.data)
            }
        }
        return Success(idResultList)
    }

    override suspend fun updateControlPointData(ctrlPointData: List<ControlPointData>): Results<Int> =
        localSource.updateControlPointData(ctrlPointData)
}