package com.skichrome.oc.easyvgp.model.source

import com.skichrome.oc.easyvgp.model.Results
import com.skichrome.oc.easyvgp.model.Results.Error
import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.base.NewVgpSource
import com.skichrome.oc.easyvgp.model.local.ChoicePossibility
import com.skichrome.oc.easyvgp.model.local.VerificationType
import com.skichrome.oc.easyvgp.model.local.database.*
import com.skichrome.oc.easyvgp.util.ItemNotFoundException

class FakeNewVgpSource(
    private val machineTypeWithCtrlPtDataService: LinkedHashMap<Long, MachineTypeWithControlPoints> = LinkedHashMap(),
    private val ctrlPointsData: LinkedHashMap<Long, ControlPointData> = LinkedHashMap(),
    private val extraDataService: LinkedHashMap<Long, MachineControlPointDataExtra> = LinkedHashMap(),
    private val machineCtrlPtDataDataService: LinkedHashMap<Long, MachineControlPointData> = LinkedHashMap()
) : NewVgpSource
{
    // =================================
    //        Superclass Methods
    // =================================

    override suspend fun getAllControlPointsWithMachineType(machineTypeId: Long): Results<MachineTypeWithControlPoints> =
        when (val machWithCtrlPt = machineTypeWithCtrlPtDataService[machineTypeId])
        {
            null -> Error(ItemNotFoundException("Item doesn't exist in this list !"))
            else -> Success(machWithCtrlPt)
        }

    override suspend fun getReportFromDate(date: Long): Results<List<Report>>
    {
        val result = getReportList(date)
        if (result.isEmpty())
            return Error(Exception("Report not found !"))
        return Success(result)
    }

    override suspend fun insertControlPointData(controlPointsData: ControlPointData): Results<Long>
    {
        ctrlPointsData[controlPointsData.id] = controlPointsData
        return Success(controlPointsData.id)
    }

    override suspend fun updateControlPointData(controlPointsData: List<ControlPointData>): Results<Int>
    {
        var itemsUpdatedCount = 0
        controlPointsData.forEach { newCtrlPtData ->
            when (ctrlPointsData[newCtrlPtData.id])
            {
                null -> return Error(ItemNotFoundException("Item doesn't exist in this list"))
                else ->
                {
                    itemsUpdatedCount++
                    ctrlPointsData[newCtrlPtData.id] = newCtrlPtData
                }
            }
        }
        return Success(itemsUpdatedCount)
    }

    override suspend fun updateControlResult(extraId: Long, controlResult: ControlResult): Results<Int> = when (val extra = extraDataService[extraId])
    {
        null -> Error(ItemNotFoundException("Item doesn't exist in this list"))
        else ->
        {
            val newExtra = MachineControlPointDataExtra(
                id = extra.id,
                isReminderEmailSent = extra.isReminderEmailSent,
                reportLocalPath = extra.reportLocalPath,
                reportEndDate = extra.reportEndDate,
                isValid = extra.isValid,
                reportDate = extra.reportDate,
                reportRemotePath = extra.reportRemotePath,
                isReportValidEmailSent = extra.isReportValidEmailSent,
                machineHours = extra.machineHours,
                interventionPlace = extra.interventionPlace,
                controlType = extra.controlType,
                machineNotice = extra.machineNotice,
                isMachineClean = extra.isMachineClean,
                isLiftingEquip = extra.isLiftingEquip,
                isMachineCE = extra.isMachineCE,
                isTestsWithLoad = extra.isTestsWithLoad,
                isTestsWithNominalLoad = extra.isTestsWithNominalLoad,
                testsHasTriggeredSensors = extra.testsHasTriggeredSensors,
                controlGlobalResult = controlResult,
                loadType = extra.loadType,
                loadMass = extra.loadMass
            )
            extraDataService[extraId] = newExtra
            Success(1)
        }
    }

    override suspend fun insertMachineCtrlPtDataExtra(controlPointsDataExtra: MachineControlPointDataExtra): Results<Long>
    {
        extraDataService[controlPointsDataExtra.id] = controlPointsDataExtra
        return Success(controlPointsDataExtra.id)
    }

    override suspend fun insertMachineCtrlPtDataCrossRef(machineControlPointsData: MachineControlPointData): Results<Long>
    {
        machineCtrlPtDataDataService[machineControlPointsData.machineCtrlPointDataExtra] = MachineControlPointData(
            machineId = machineControlPointsData.machineId,
            ctrlPointDataId = machineControlPointsData.ctrlPointDataId,
            machineCtrlPointDataExtra = machineControlPointsData.machineCtrlPointDataExtra
        )
        return Success(machineControlPointsData.machineCtrlPointDataExtra)
    }

    // =================================
    //              Methods
    // =================================

    fun getReportList(date: Long) = extraDataService.filter { it.value.reportDate == date }.map { extras ->
        Report(
            machineId = 0L,
            ctrlPointDataExtra = extras.value,
            ctrlPointData = ControlPointData(
                ctrlPointRef = 0L,
                comment = null,
                ctrlPointVerificationType = VerificationType.VISUAL,
                ctrlPointPossibility = ChoicePossibility.UNKNOWN,
                id = 0L
            ),
            ctrlPoint = ControlPoint(id = 0L, code = "", name = "")
        )
    }

    fun getMachineCtrlPtDataCrossRef(extraId: Long) = when (val crossRef = machineCtrlPtDataDataService[extraId])
    {
        null -> Error(ItemNotFoundException("This reference was not found in the list"))
        else -> Success(crossRef)
    }

    fun getCtrlPtData(id: Long) = when (val ctrlPtData = ctrlPointsData[id])
    {
        null -> Error(ItemNotFoundException("This ctrlPtData was not found in this list"))
        else -> Success(ctrlPtData)
    }

    fun getExtra(id: Long) = when (val extra = extraDataService[id])
    {
        null -> Error(ItemNotFoundException("This reference was not found in the list"))
        else -> Success(extra)
    }
}