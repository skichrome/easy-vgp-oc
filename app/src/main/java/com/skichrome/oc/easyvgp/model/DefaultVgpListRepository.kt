package com.skichrome.oc.easyvgp.model

import com.skichrome.oc.easyvgp.androidmanagers.NetManager
import com.skichrome.oc.easyvgp.model.Results.Error
import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.local.database.VgpListItem
import com.skichrome.oc.easyvgp.util.LocalRepositoryException
import com.skichrome.oc.easyvgp.util.NetworkException

class DefaultVgpListRepository(
    private val netManager: NetManager,
    private val localSource: VgpListSource,
    private val remoteSource: VgpListSource
) : VgpListRepository
{
    override suspend fun getAllReports(machineId: Long): Results<List<VgpListItem>> = localSource.getAllReports(machineId)

    override suspend fun generateReport(userUid: String, customerId: Long, machineId: Long, machineTypeId: Long, reportDate: Long): Results<Boolean>
    {
        return if (netManager.isConnectedToInternet())
        {
            val localReport = localSource.getReportFromDate(reportDate)
            val customer = localSource.getCustomerFromId(customerId)
            val machine = localSource.getMachineFromId(machineId)
            val machineType = localSource.getMachineTypeFromId(machineTypeId)
            if (localReport is Success && customer is Success && machine is Success && machineType is Success)
                remoteSource.generateReport(
                    userUid = userUid,
                    customer = customer.data,
                    machineType = machineType.data,
                    machine = machine.data,
                    reports = localReport.data,
                    reportDate = reportDate
                )
            else
                Error((localReport as? Error)?.exception ?: LocalRepositoryException("Something went wrong with local source"))
        }
        else
            Error(NetworkException("Internet access is required to upload a report"))
    }
}