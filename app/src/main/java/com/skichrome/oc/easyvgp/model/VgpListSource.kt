package com.skichrome.oc.easyvgp.model

import com.skichrome.oc.easyvgp.model.local.database.*

interface VgpListSource
{
    suspend fun getAllReports(machineId: Long): Results<List<VgpListItem>>

    suspend fun getReportFromDate(date: Long): Results<List<Report>>

    suspend fun getCustomerFromId(customerId: Long): Results<Customer>

    suspend fun getMachineFromId(machineId: Long): Results<Machine>

    suspend fun getMachineTypeFromId(machineTypeId: Long): Results<MachineType>

    suspend fun generateReport(
        userUid: String,
        reportDate: Long,
        customer: Customer,
        machine: Machine,
        machineType: MachineType,
        reports: List<Report>
    ): Results<Boolean>
}