package com.skichrome.oc.easyvgp.model

import com.skichrome.oc.easyvgp.model.local.database.VgpListItem

interface VgpListSource
{
    suspend fun getAllReports(machineId: Long): Results<List<VgpListItem>>
}