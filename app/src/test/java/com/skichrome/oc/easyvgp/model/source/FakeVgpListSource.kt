package com.skichrome.oc.easyvgp.model.source

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.skichrome.oc.easyvgp.model.Results
import com.skichrome.oc.easyvgp.model.Results.Error
import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.base.VgpListSource
import com.skichrome.oc.easyvgp.model.local.database.Customer
import com.skichrome.oc.easyvgp.model.local.database.MachineControlPointDataExtra
import com.skichrome.oc.easyvgp.model.local.database.VgpListItem
import com.skichrome.oc.easyvgp.util.ItemNotFoundException
import com.skichrome.oc.easyvgp.util.RemoteRepositoryException
import java.io.File

class FakeVgpListSource(
    private val extrasDataService: LinkedHashMap<Long, MachineControlPointDataExtra> = LinkedHashMap(),
    private val customersDataService: LinkedHashMap<Long, Customer> = LinkedHashMap()
) : VgpListSource
{
    // =================================
    //              Fields
    // =================================

    private val observableReports = MutableLiveData<List<VgpListItem>>()

    // =================================
    //        Superclass Methods
    // =================================

    override fun observeReports(): LiveData<Results<List<VgpListItem>>> = observableReports.map { Success(it) }

    override suspend fun getMachineCtrlPtExtraFromId(id: Long): Results<MachineControlPointDataExtra> = when (val extra = extrasDataService[id])
    {
        null -> Error(ItemNotFoundException("Item doesn't exist in this list"))
        else -> Success(extra)
    }

    override suspend fun updateMachineCtrlPtExtra(extra: MachineControlPointDataExtra): Results<Int> = when (extrasDataService[extra.id])
    {
        null -> Error(ItemNotFoundException("Item doesn't exist in this list"))
        else ->
        {
            extrasDataService[extra.id] = extra
            Success(1)
        }
    }

    override suspend fun downloadReportFromStorage(remotePath: String?, destinationFile: File): Results<File> = remotePath?.let {
        Success(File(remotePath))
    } ?: Error(RemoteRepositoryException("Remote report path is NULL !!"))

    override suspend fun loadCustomerEmail(customerId: Long): Results<String> = when (val customer = customersDataService[customerId])
    {
        null -> Error(ItemNotFoundException("Item doesn't exist in this list"))
        else ->
        {
            Success(customer.email)
        }
    }

    // =================================
    //              Methods
    // =================================

    fun refresh()
    {
        observableReports.value = extrasDataService.values.map {
            VgpListItem(
                reportRemotePath = it.reportRemotePath,
                reportDate = it.reportDate,
                reportEndDate = it.reportEndDate,
                reportLocalPath = it.reportLocalPath,
                machineId = 0L,
                isValid = it.isValid,
                extrasReference = it.id,
                controlPointDataId = 0L
            )
        }
            .toList()
            .sortedBy { it.extrasReference }
    }

    fun getVgpListItems() = observableReports.value
}