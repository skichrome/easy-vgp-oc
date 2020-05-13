package com.skichrome.oc.easyvgp.viewmodel.source

import com.skichrome.oc.easyvgp.model.Results
import com.skichrome.oc.easyvgp.model.Results.Error
import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.base.NewVgpSetupRepository
import com.skichrome.oc.easyvgp.model.local.database.MachineControlPointDataExtra
import com.skichrome.oc.easyvgp.util.ItemNotFoundException

class FakeNewVgpSetupRepository(
    private val extraDataService: LinkedHashMap<Long, MachineControlPointDataExtra> = LinkedHashMap()
) : NewVgpSetupRepository
{
    override suspend fun getPreviousCtrlPtDataExtraFromDate(date: Long): Results<MachineControlPointDataExtra> =
        when (val extra = extraDataService[date])
        {
            null -> Error(ItemNotFoundException("This item doesn't exist in the list"))
            else -> Success(extra)
        }

    override suspend fun insertMachineCtrlPtDataExtra(controlPointsDataExtra: MachineControlPointDataExtra): Results<Long>
    {
        extraDataService[controlPointsDataExtra.reportDate] = controlPointsDataExtra
        return Success(controlPointsDataExtra.id)
    }

    override suspend fun updateMachineCtrlPtDataExtra(controlPointsDataExtra: MachineControlPointDataExtra): Results<Int> =
        when (extraDataService[controlPointsDataExtra.reportDate])
        {
            null -> Error(ItemNotFoundException("This item doesn't exist in the list"))
            else ->
            {
                extraDataService[controlPointsDataExtra.reportDate] = controlPointsDataExtra
                Success(1)
            }
        }

    override suspend fun deleteMachineCtrlPtDataExtra(date: Long): Results<Int> =
        when (extraDataService[date])
        {
            null -> Success(0)
            else ->
            {
                extraDataService.remove(date)
                Success(1)
            }
        }

    // =================================
    //              Methods
    // =================================

    fun getExtra(date: Long) = when (val extra = extraDataService[date])
    {
        null -> Error(ItemNotFoundException("This item doesn't exist in the list"))
        else -> Success(extra)
    }
}