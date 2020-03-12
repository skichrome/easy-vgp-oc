package com.skichrome.oc.easyvgp.model

interface VgpListRepository
{
    suspend fun getAllReports(machineId: Long): Results<List<Long>>
}