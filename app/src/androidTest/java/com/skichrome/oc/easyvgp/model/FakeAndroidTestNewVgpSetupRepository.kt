package com.skichrome.oc.easyvgp.model

import com.skichrome.oc.easyvgp.model.base.NewVgpSetupRepository
import com.skichrome.oc.easyvgp.model.local.database.MachineControlPointDataExtra
import com.skichrome.oc.easyvgp.util.ItemNotFoundException

class FakeAndroidTestNewVgpSetupRepository(
    private val extraDataService: LinkedHashMap<Long, MachineControlPointDataExtra> = LinkedHashMap()
) : NewVgpSetupRepository
{
    override suspend fun getPreviousCtrlPtDataExtraFromDate(date: Long): Results<MachineControlPointDataExtra> =
        when (val extra = extraDataService[date])
        {
            null -> Results.Error(ItemNotFoundException("This item doesn't exist in the list"))
            else -> Results.Success(extra)
        }

    override suspend fun insertMachineCtrlPtDataExtra(controlPointsDataExtra: MachineControlPointDataExtra): Results<Long>
    {
        extraDataService[controlPointsDataExtra.reportDate] = controlPointsDataExtra
        return Results.Success(controlPointsDataExtra.id)
    }

    override suspend fun updateMachineCtrlPtDataExtra(controlPointsDataExtra: MachineControlPointDataExtra): Results<Int> =
        when (extraDataService[controlPointsDataExtra.reportDate])
        {
            null -> Results.Error(ItemNotFoundException("This item doesn't exist in the list"))
            else ->
            {
                extraDataService[controlPointsDataExtra.reportDate] = controlPointsDataExtra
                Results.Success(1)
            }
        }

    override suspend fun deleteMachineCtrlPtDataExtra(date: Long): Results<Int> =
        when (extraDataService[date])
        {
            null -> Results.Success(0)
            else ->
            {
                extraDataService.remove(date)
                Results.Success(1)
            }
        }

    // =================================
    //              Methods
    // =================================

    fun insertExtrasForTests(extras: List<MachineControlPointDataExtra>) = extras.forEach {
        extraDataService[it.reportDate] = it
    }
}