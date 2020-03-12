package com.skichrome.oc.easyvgp.model

class DefaultVgpListRepository(private val localSource: VgpListSource) : VgpListRepository
{
    override suspend fun getAllReports(machineId: Long): Results<List<Long>> = localSource.getAllReports(machineId)
}