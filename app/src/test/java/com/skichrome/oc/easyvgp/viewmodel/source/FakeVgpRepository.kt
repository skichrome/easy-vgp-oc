package com.skichrome.oc.easyvgp.viewmodel.source

import com.skichrome.oc.easyvgp.model.Results
import com.skichrome.oc.easyvgp.model.Results.Error
import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.base.NewVgpRepository
import com.skichrome.oc.easyvgp.model.local.database.*
import com.skichrome.oc.easyvgp.model.local.util.ControlPointDataVgp
import com.skichrome.oc.easyvgp.util.ItemNotFoundException

class FakeVgpRepository(
    private val machineTypeWithCtrlPtsDataService: LinkedHashMap<Long, MachineTypeWithControlPoints> = LinkedHashMap(),
    private val reportsDataService: LinkedHashMap<Long, Report> = LinkedHashMap(),
    private val ctrlPtDataService: LinkedHashMap<Long, ControlPointData> = LinkedHashMap(),
    private val machineCtrlPtDataDataService: LinkedHashMap<Long, MachineControlPointData> = LinkedHashMap(),
    private val extraDataService: LinkedHashMap<Long, MachineControlPointDataExtra> = LinkedHashMap()
) : NewVgpRepository
{
    // =================================
    //        Superclass Methods
    // =================================

    override suspend fun getAllControlPointsWithMachineType(machineTypeId: Long): Results<MachineTypeWithControlPoints> =
        when (val machTypeWithCtrlPt = machineTypeWithCtrlPtsDataService[machineTypeId])
        {
            null -> Error(ItemNotFoundException("This item doesn't exist in the list"))
            else -> Success(machTypeWithCtrlPt)
        }

    override suspend fun getReportFromDate(date: Long): Results<List<Report>>
    {
        val result = extraDataService.filter { it.value.reportDate == date }
            .map { it.value }
            .first()
            .let { extra -> reportsDataService.filter { it.value.ctrlPointDataExtra.id == extra.id } }
            .map { it.value }

        println(result.map { it.ctrlPoint.name })

        return Success(result)
    }

    override suspend fun insertMachineControlPointData(
        ctrlPointDataVgp: List<ControlPointDataVgp>,
        machineId: Long,
        controlExtraId: Long
    ): Results<List<Long>>
    {
        val result = ctrlPointDataVgp.map {
            val ctrlPointData = ControlPointData(
                id = it.ctrlPointDataId,
                comment = it.comment,
                ctrlPointPossibility = it.choicePossibility,
                ctrlPointVerificationType = it.verificationType,
                ctrlPointRef = it.controlPoint.id
            )
            ctrlPtDataService[it.ctrlPointDataId] = ctrlPointData

            machineCtrlPtDataDataService[it.ctrlPointDataId] = MachineControlPointData(
                machineId = machineId,
                ctrlPointDataId = it.ctrlPointDataId,
                machineCtrlPointDataExtra = controlExtraId
            )

            return@map it.ctrlPointDataId
        }
        return Success(result)
    }

    override suspend fun updateControlPointData(ctrlPointData: List<ControlPointData>): Results<Int> =
        when (ctrlPointData.map { ctrlPtDataService[it.id] == null }.contains(false))
        {
            true -> Error(ItemNotFoundException("This item doesn't exist in the list"))
            else ->
            {
                ctrlPointData.forEach { ctrlPtData ->
                    ctrlPtDataService[ctrlPtData.id] = ctrlPtData
                }
                Success(ctrlPointData.size)
            }
        }

    override suspend fun updateControlResult(extraId: Long, controlResult: ControlResult): Results<Int> =
        when (val extra = extraDataService[extraId])
        {
            null -> Error(ItemNotFoundException("This item doesn't exist in the list"))
            else ->
            {
                extraDataService[extraId] = MachineControlPointDataExtra(
                    id = extra.id,
                    loadMass = extra.loadMass,
                    loadType = extra.loadType,
                    controlGlobalResult = controlResult,
                    testsHasTriggeredSensors = extra.testsHasTriggeredSensors,
                    isTestsWithNominalLoad = extra.isTestsWithNominalLoad,
                    isTestsWithLoad = extra.isTestsWithLoad,
                    reportDate = extra.reportDate,
                    isMachineCE = extra.isMachineCE,
                    isLiftingEquip = extra.isLiftingEquip,
                    isMachineClean = extra.isMachineClean,
                    machineNotice = extra.machineNotice,
                    isValid = extra.isValid,
                    controlType = extra.controlType,
                    interventionPlace = extra.interventionPlace,
                    machineHours = extra.machineHours,
                    reportEndDate = extra.reportEndDate,
                    isReminderEmailSent = extra.isReminderEmailSent,
                    isReportValidEmailSent = extra.isReportValidEmailSent,
                    reportLocalPath = extra.reportLocalPath,
                    reportRemotePath = extra.reportRemotePath
                )
                Success(1)
            }
        }
}