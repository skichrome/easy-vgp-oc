package com.skichrome.oc.easyvgp.model.base

import androidx.lifecycle.LiveData
import com.skichrome.oc.easyvgp.model.Results
import com.skichrome.oc.easyvgp.model.local.database.MachineControlPointDataExtra
import com.skichrome.oc.easyvgp.model.local.database.VgpListItem
import java.io.File

interface VgpListSource
{
    fun observeReports(): LiveData<Results<List<VgpListItem>>>

    suspend fun getMachineCtrlPtExtraFromId(id: Long): Results<MachineControlPointDataExtra>
    suspend fun updateMachineCtrlPtExtra(extra: MachineControlPointDataExtra): Results<Int>

    suspend fun downloadReportFromStorage(remotePath: String?, destinationFile: File): Results<File>
}