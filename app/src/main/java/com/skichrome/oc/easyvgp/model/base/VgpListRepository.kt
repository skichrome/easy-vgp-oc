package com.skichrome.oc.easyvgp.model.base

import com.skichrome.oc.easyvgp.model.Results
import com.skichrome.oc.easyvgp.model.local.database.VgpListItem
import java.io.File

interface VgpListRepository
{
    suspend fun getAllReports(machineId: Long): Results<List<VgpListItem>>
    suspend fun downloadReportFromStorage(extraId: Long, remotePath: String?, destinationFile: File): Results<File>
}