package com.skichrome.oc.easyvgp.model

import com.skichrome.oc.easyvgp.model.local.database.VgpListItem

interface VgpListRepository
{
    suspend fun getAllReports(machineId: Long): Results<List<VgpListItem>>
}