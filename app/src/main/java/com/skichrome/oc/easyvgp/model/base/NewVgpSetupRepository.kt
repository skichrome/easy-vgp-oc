package com.skichrome.oc.easyvgp.model.base

import com.skichrome.oc.easyvgp.model.Results
import com.skichrome.oc.easyvgp.model.local.database.MachineControlPointDataExtra

interface NewVgpSetupRepository
{
    suspend fun getPreviousCtrlPtDataExtraFromDate(date: Long): Results<MachineControlPointDataExtra>
    suspend fun insertMachineCtrlPtDataExtra(controlPointsDataExtra: MachineControlPointDataExtra): Results<Long>
    suspend fun updateMachineCtrlPtDataExtra(controlPointsDataExtra: MachineControlPointDataExtra): Results<Int>
}