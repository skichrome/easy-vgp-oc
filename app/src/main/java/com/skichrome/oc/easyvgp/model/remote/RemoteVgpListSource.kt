package com.skichrome.oc.easyvgp.model.remote

import android.content.res.Resources
import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.skichrome.oc.easyvgp.model.Results
import com.skichrome.oc.easyvgp.model.Results.Error
import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.VgpListSource
import com.skichrome.oc.easyvgp.model.local.ChoicePossibility
import com.skichrome.oc.easyvgp.model.local.VerificationType
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
    private val resources: Resources,
    private val dispatchers: CoroutineDispatcher = Dispatchers.IO
) : VgpListSource
{
    private val db = Firebase.firestore

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
                        model = it.model,
                        manufacturingYear = it.manufacturingYear,
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
                val remoteReportCtrlPoint = LinkedHashMap<String, RemoteControlPoint>()

                reports.forEach {
                    val verificationType = resources.getString(VerificationType.values()[it.ctrlPointData.ctrlPointVerificationType - 1].verification)
                    val possibility = resources.getString(ChoicePossibility.values()[it.ctrlPointData.ctrlPointPossibility - 1].choice)

                    remoteReportCtrlPointData["${it.ctrlPoint.id}"] = RemoteControlPointData(
                        id = it.ctrlPointData.id,
                        comment = it.ctrlPointData.comment,
                        ctrlPointVerificationType = verificationType,
                        ctrlPointRef = it.ctrlPointData.ctrlPointRef,
                        ctrlPointPossibility = possibility
                    )

                    remoteReportCtrlPoint["${it.ctrlPoint.id}"] = RemoteControlPoint(
                        id = it.ctrlPoint.id,
                        name = it.ctrlPoint.name,
                        code = it.ctrlPoint.code,
                        remoteId = ""
                    )
                }

                val remoteReport = RemoteReportData(
                    customer = remoteCustomer,
                    machine = remoteMachine,
                    machineType = remoteMachineType,
                    reportData = remoteReportCtrlPointData,
                    reportCtrlPoint = remoteReportCtrlPoint
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