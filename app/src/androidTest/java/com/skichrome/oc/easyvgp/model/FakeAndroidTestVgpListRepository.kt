package com.skichrome.oc.easyvgp.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.skichrome.oc.easyvgp.model.base.VgpListRepository
import com.skichrome.oc.easyvgp.model.local.database.Customer
import com.skichrome.oc.easyvgp.model.local.database.VgpListItem
import com.skichrome.oc.easyvgp.util.ItemNotFoundException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.io.File

class FakeAndroidTestVgpListRepository(
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

    override fun observeReports(): LiveData<Results<List<VgpListItem>>> = observableReports.map { Results.Success(it) }

    override suspend fun downloadReportFromStorage(extraId: Long, remotePath: String?, destinationFile: File): Results<File> =
        Results.Success(destinationFile)

    override suspend fun loadCustomerEmail(customerId: Long): Results<String> = when (val customer = customersDataService[customerId])
    {
        null -> Results.Error(ItemNotFoundException("This item doesn't exist in the list"))
        else -> Results.Success(customer.email)
    }

    // =================================
    //              Methods
    // =================================

    fun refresh() = runBlocking(Dispatchers.Main) {
        observableReports.value = reportsDataService.values.toList().sortedBy { it.machineId }
    }

    fun insertCustomersForTest(customers: List<Customer>) = customers.forEach {
        customersDataService[it.id] = it
    }

    fun insertVgpListItemsForTest(vgpListItems: List<VgpListItem>) = vgpListItems.forEachIndexed { index, vgpListItem ->
        reportsDataService[index.toLong()] = vgpListItem
    }
}