package com.skichrome.oc.easyvgp.model

import com.skichrome.oc.easyvgp.model.Results.Error
import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.base.NewVgpRepository
import com.skichrome.oc.easyvgp.model.base.NewVgpSource
import com.skichrome.oc.easyvgp.model.local.database.*
import com.skichrome.oc.easyvgp.model.local.util.ControlPointDataVgp

class DefaultNewVgpRepository(private val localSource: NewVgpSource) : NewVgpRepository
{
    override suspend fun getAllControlPointsWithMachineType(machineTypeId: Long): Results<MachineTypeWithControlPoints> =
        localSource.getAllControlPointsWithMachineType(machineTypeId)

    override suspend fun getReportFromDate(date: Long): Results<List<Report>> = localSource.getReportFromDate(date)

    override suspend fun insertMachineControlPointData(
        ctrlPointDataVgp: List<ControlPointDataVgp>,
        machineId: Long,
        controlExtraId: Long
    ): Results<List<Long>>
    {
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
                    machineCtrlPointDataExtra = controlExtraId
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

    override suspend fun updateControlResult(extraId: Long, controlResult: ControlResult): Results<Int> =
        localSource.updateControlResult(extraId, controlResult)
}