package com.skichrome.oc.easyvgp.model.base

import com.skichrome.oc.easyvgp.model.Results
import com.skichrome.oc.easyvgp.model.local.database.VgpListItem

interface VgpListRepository
{
    suspend fun getAllReports(machineId: Long): Results<List<VgpListItem>>

    suspend fun generateReport(
        userId: Long,
        customerId: Long,
        machineId: Long,
        machineTypeId: Long,
        report: VgpListItem
    ): Results<Boolean>
}