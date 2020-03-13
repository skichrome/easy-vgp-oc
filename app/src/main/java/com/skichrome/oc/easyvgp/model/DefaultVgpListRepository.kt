package com.skichrome.oc.easyvgp.model

import com.skichrome.oc.easyvgp.model.local.database.VgpListItem

class DefaultVgpListRepository(private val localSource: VgpListSource) : VgpListRepository
{
    override suspend fun getAllReports(machineId: Long): Results<List<VgpListItem>> = localSource.getAllReports(machineId)
}