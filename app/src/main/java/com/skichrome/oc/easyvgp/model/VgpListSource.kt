package com.skichrome.oc.easyvgp.model

interface VgpListSource
{
    suspend fun getAllReports(machineId: Long): Results<List<Long>>
}