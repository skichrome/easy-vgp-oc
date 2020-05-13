package com.skichrome.oc.easyvgp.viewmodel.source

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.skichrome.oc.easyvgp.model.Results
import com.skichrome.oc.easyvgp.model.Results.Error
import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.base.VgpListRepository
import com.skichrome.oc.easyvgp.model.local.database.Customer
import com.skichrome.oc.easyvgp.model.local.database.VgpListItem
import com.skichrome.oc.easyvgp.util.ItemNotFoundException
import java.io.File

class FakeVgpListRepository(
    private val reportsDataService: LinkedHashMap<Long, VgpListItem> = LinkedHashMap(),
    private val customersDataService: LinkedHashMap<Long, Customer> = LinkedHashMap()
) : VgpListRepository
{
    // =================================
    //              Fields
    // =================================

    private val observableReports = MutableLiveData<List<VgpListItem>>()

    // =================================
    //        Superclass Methods
    // =================================

    override fun observeReports(): LiveData<Results<List<VgpListItem>>> = observableReports.map { Success(it) }

    override suspend fun downloadReportFromStorage(extraId: Long, remotePath: String?, destinationFile: File): Results<File> =
        Success(destinationFile)

    override suspend fun loadCustomerEmail(customerId: Long): Results<String> = when (val customer = customersDataService[customerId])
    {
        null -> Error(ItemNotFoundException("This item doesn't exist in the list"))
        else -> Success(customer.email)
    }

    // =================================
    //              Methods
    // =================================

    fun refresh()
    {
        observableReports.value = reportsDataService.values.toList().sortedBy { it.machineId }
    }
}