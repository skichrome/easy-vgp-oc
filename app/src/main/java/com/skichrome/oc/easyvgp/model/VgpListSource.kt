package com.skichrome.oc.easyvgp.model

import android.net.Uri
import com.skichrome.oc.easyvgp.model.local.database.*

interface VgpListSource
{
    suspend fun getAllReports(machineId: Long): Results<List<VgpListItem>>

    suspend fun getReportFromDate(date: Long): Results<List<Report>>

    suspend fun getUserFromId(id: Long): Results<UserAndCompany>

    suspend fun getCustomerFromId(customerId: Long): Results<Customer>

    suspend fun getMachineFromId(machineId: Long): Results<Machine>

    suspend fun getMachineTypeFromId(machineTypeId: Long): Results<MachineType>

    suspend fun updateMachine(machine: Machine): Results<Int>

    suspend fun uploadImageToStorage(userUid: String, filePath: String): Results<Uri>

    suspend fun generateReport(
        reportDate: Long,
        user: UserAndCompany,
        customer: Customer,
        machine: Machine,
        machineType: MachineType,
        reports: List<Report>
    ): Results<Boolean>
}