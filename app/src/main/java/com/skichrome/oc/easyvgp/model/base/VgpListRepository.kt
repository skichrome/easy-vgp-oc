package com.skichrome.oc.easyvgp.model.base

import androidx.lifecycle.LiveData
import com.skichrome.oc.easyvgp.model.Results
import com.skichrome.oc.easyvgp.model.local.database.VgpListItem
import java.io.File

interface VgpListRepository
{
    fun observeReports(): LiveData<Results<List<VgpListItem>>>
    suspend fun downloadReportFromStorage(extraId: Long, remotePath: String?, destinationFile: File): Results<File>
    suspend fun loadCustomerEmail(customerId: Long): Results<String>
}