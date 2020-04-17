package com.skichrome.oc.easyvgp.model

import com.skichrome.oc.easyvgp.model.base.NewVgpSetupRepository
import com.skichrome.oc.easyvgp.model.base.NewVgpSetupSource
import com.skichrome.oc.easyvgp.model.local.database.MachineControlPointDataExtra

class DefaultNewVgpSetupRepository(private val localSource: NewVgpSetupSource) : NewVgpSetupRepository
{
    override suspend fun getPreviousCtrlPtDataExtraFromDate(date: Long): Results<MachineControlPointDataExtra> =
        localSource.getPreviousCtrlPtDataExtraFromDate(date)

    override suspend fun insertMachineCtrlPtDataExtra(controlPointsDataExtra: MachineControlPointDataExtra): Results<Long> =
        localSource.insertMachineCtrlPtDataExtra(controlPointsDataExtra)

    override suspend fun updateMachineCtrlPtDataExtra(controlPointsDataExtra: MachineControlPointDataExtra): Results<Int> =
        localSource.updateMachineCtrlPtDataExtra(controlPointsDataExtra)
}