package com.skichrome.oc.easyvgp.model.remote

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.skichrome.oc.easyvgp.model.Results
import com.skichrome.oc.easyvgp.model.Results.Error
import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.VgpListSource
import com.skichrome.oc.easyvgp.model.local.database.*
import com.skichrome.oc.easyvgp.model.remote.util.*
import com.skichrome.oc.easyvgp.util.NotImplementedException
import com.skichrome.oc.easyvgp.util.REMOTE_REPORT_COLLECTION
import com.skichrome.oc.easyvgp.util.REMOTE_USER_COLLECTION
import com.skichrome.oc.easyvgp.util.awaitUpload
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RemoteVgpListSource(
    private val db: FirebaseFirestore,
    private val dispatchers: CoroutineDispatcher = Dispatchers.IO
) : VgpListSource
{
    // =================================
    //        Superclass Methods
    // =================================

    override suspend fun generateReport(
        userUid: String,
        reportDate: Long,
        customer: Customer,
        machine: Machine,
        machineType: MachineType,
        reports: List<Report>
    ): Results<Boolean> =
        withContext(dispatchers) {
            return@withContext try
            {
                val remoteCustomer = customer.let {
                    RemoteCustomer(
                        id = it.id,
                        address = it.address,
                        city = it.city,
                        email = it.email,
                        firstName = it.firstName,
                        lastName = it.lastName,
                        mobilePhone = it.mobilePhone,
                        notes = it.notes,
                        phone = it.phone,
                        postCode = it.postCode,
                        siret = it.siret
                    )
                }

                val remoteMachine = machine.let {
                    RemoteMachine(
                        machineId = it.machineId,
                        name = it.name,
                        brand = it.brand,
                        customer = it.customer,
                        serial = it.serial,
                        type = it.type
                    )
                }

                val remoteMachineType = machineType.let {
                    RemoteMachineType(
                        id = it.id,
                        name = it.name,
                        legalName = it.legalName
                    )
                }

                val remoteReportCtrlPointData = LinkedHashMap<String, RemoteControlPointData>()

                reports.forEach {
                    remoteReportCtrlPointData["${it.ctrlPoint.id}"] = RemoteControlPointData(
                        id = it.ctrlPointData.id,
                        comment = it.ctrlPointData.comment,
                        ctrlPointVerificationType = it.ctrlPointData.ctrlPointVerificationType,
                        ctrlPointRef = it.ctrlPointData.ctrlPointRef,
                        ctrlPointPossibility = it.ctrlPointData.ctrlPointPossibility
                    )
                }

                val remoteReport = RemoteReportData(
                    customer = remoteCustomer,
                    machine = remoteMachine,
                    machineType = remoteMachineType,
                    reportData = remoteReportCtrlPointData
                )

                getUserCollection(userUid)
                    .document("$reportDate")
                    .set(remoteReport)
                    .awaitUpload()

                Success(true)
            }
            catch (e: Exception)
            {
                Log.e("RemoteVGPListSrc", "Upload failed ! ${getUserCollection(userUid).path}", e)
                Error(e)
            }
        }

    override suspend fun getAllReports(machineId: Long): Results<List<VgpListItem>> =
        Error(NotImplementedException("Not implemented for remote source"))

    override suspend fun getReportFromDate(date: Long): Results<List<Report>> =
        Error(NotImplementedException("Not implemented for remote source"))

    override suspend fun getCustomerFromId(customerId: Long): Results<Customer> =
        Error(NotImplementedException("Not implemented for remote source"))

    override suspend fun getMachineFromId(machineId: Long): Results<Machine> =
        Error(NotImplementedException("Not implemented for remote source"))

    override suspend fun getMachineTypeFromId(machineTypeId: Long): Results<MachineType> =
        Error(NotImplementedException("Not implemented for remote source"))

    // =================================
    //              Methods
    // =================================

    private fun getUserCollection(userUid: String) = db.collection(REMOTE_USER_COLLECTION)
        .document(userUid)
        .collection(REMOTE_REPORT_COLLECTION)
}