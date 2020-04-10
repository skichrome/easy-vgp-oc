package com.skichrome.oc.easyvgp.model.remote

import android.content.res.Resources
import android.net.Uri
import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.google.firebase.storage.ktx.storageMetadata
import com.skichrome.oc.easyvgp.model.Results
import com.skichrome.oc.easyvgp.model.Results.Error
import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.VgpListSource
import com.skichrome.oc.easyvgp.model.local.ChoicePossibility
import com.skichrome.oc.easyvgp.model.local.VerificationType
import com.skichrome.oc.easyvgp.model.local.database.*
import com.skichrome.oc.easyvgp.model.remote.util.*
import com.skichrome.oc.easyvgp.util.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RemoteVgpListSource(
    private val resources: Resources,
    private val dispatchers: CoroutineDispatcher = Dispatchers.IO
) : VgpListSource
{
    private val db = Firebase.firestore
    private val storage = Firebase.storage
    private val userReference = storage.reference

    // =================================
    //        Superclass Methods
    // =================================

    override suspend fun uploadImageToStorage(userUid: String, localUri: Uri, oldRemoteUri: Uri?): Results<Uri> = withContext(dispatchers) {
        return@withContext try
        {
            val metadata = storageMetadata {
                contentType = "image/jpg"
            }

            val userPhotoReference = userReference.child("$REMOTE_USER_STORAGE/$userUid/$PICTURES_FOLDER_NAME/${localUri.path?.split("/")?.last()}")

            oldRemoteUri?.let {
                val oldUserPhotoReference =
                    userReference.child("$REMOTE_USER_STORAGE/$userUid/$PICTURES_FOLDER_NAME/${it.path?.split("/")?.last()}")

                if (userPhotoReference != oldUserPhotoReference)
                {
                    Log.e("RemoteVgpListSrc", "Remote reference need to be updated")
                    oldUserPhotoReference.delete()
                    uploadToStorage(reference = userPhotoReference, uri = localUri, metadata = metadata)
                }
                else
                    Success(localUri)
            } ?: uploadToStorage(reference = userPhotoReference, uri = localUri, metadata = metadata)
        }
        catch (e: Exception)
        {
            Error(e)
        }
    }

    override suspend fun generateReport(
        reportDate: Long,
        user: UserAndCompany,
        customer: Customer,
        machine: Machine,
        machineType: MachineType,
        reports: List<Report>
    ): Results<Boolean> =
        withContext(dispatchers) {
            return@withContext try
            {
                val notificationToken = FirebaseInstanceId.getInstance().instanceId
                    .await()
                    .token

                val remoteUser = RemoteUser(
                    id = user.user.id,
                    name = user.user.name,
                    email = user.user.email,
                    approval = user.user.approval,
                    companyId = user.company.id,
                    companyName = user.company.name,
                    companySiret = user.company.siret,
                    firebaseUid = user.user.firebaseUid,
                    notificationToken = notificationToken,
                    vatNumber = user.user.vatNumber
                )

                val remoteCustomer = RemoteCustomer(
                    id = customer.id,
                    address = customer.address,
                    city = customer.city,
                    email = customer.email,
                    firstName = customer.firstName,
                    lastName = customer.lastName,
                    mobilePhone = customer.mobilePhone,
                    notes = customer.notes,
                    phone = customer.phone,
                    postCode = customer.postCode,
                    siret = customer.siret
                )

                val remoteMachine = RemoteMachine(
                    machineId = machine.machineId,
                    name = machine.name,
                    brand = machine.brand,
                    model = machine.model,
                    manufacturingYear = machine.manufacturingYear,
                    customer = machine.customer,
                    serial = machine.serial,
                    type = machine.type,
                    photoReference = machine.remotePhotoRef.toString()
                )

                val remoteMachineType = RemoteMachineType(
                    id = machineType.id,
                    name = machineType.name,
                    legalName = machineType.legalName
                )

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
                    user = remoteUser,
                    customer = remoteCustomer,
                    machine = remoteMachine,
                    machineType = remoteMachineType,
                    reportData = remoteReportCtrlPointData,
                    reportCtrlPoint = remoteReportCtrlPoint
                )

                getUserCollection(user.user.firebaseUid)
                    .document("$reportDate")
                    .set(remoteReport)
                    .await()

                Success(true)
            }
            catch (e: Exception)
            {
                Log.e("RemoteVGPListSrc", "Upload failed ! ${getUserCollection(user.user.firebaseUid).path}", e)
                Error(e)
            }
        }

    override suspend fun getAllReports(machineId: Long): Results<List<VgpListItem>> =
        Error(NotImplementedException("Not implemented for remote source"))

    override suspend fun getReportFromDate(date: Long): Results<List<Report>> =
        Error(NotImplementedException("Not implemented for remote source"))

    override suspend fun getUserFromId(id: Long): Results<UserAndCompany> =
        Error(NotImplementedException("Not implemented for remote source"))

    override suspend fun getCustomerFromId(customerId: Long): Results<Customer> =
        Error(NotImplementedException("Not implemented for remote source"))

    override suspend fun getMachineFromId(machineId: Long): Results<Machine> =
        Error(NotImplementedException("Not implemented for remote source"))

    override suspend fun getMachineTypeFromId(machineTypeId: Long): Results<MachineType> =
        Error(NotImplementedException("Not implemented for remote source"))

    override suspend fun updateMachine(machine: Machine): Results<Int> =
        Error(NotImplementedException("Not implemented for remote source"))

    override suspend fun updateUser(user: User): Results<Int> =
        Error(NotImplementedException("Not implemented for remote source"))

    // =================================
    //              Methods
    // =================================

    private fun getUserCollection(userUid: String) = db.collection(REMOTE_USER_COLLECTION)
        .document(userUid)
        .collection(REMOTE_REPORT_COLLECTION)

    private suspend fun uploadToStorage(reference: StorageReference, uri: Uri, metadata: StorageMetadata): Results<Uri>
    {
        try
        {
            val remotePhotoReference = reference.putFile(uri, metadata)
                .continueWithTask { task ->
                    if (!task.isSuccessful)
                        task.exception?.let { throw it }

                    reference.downloadUrl
                }
                .await()
            return Success(remotePhotoReference)
        }
        catch (e: Exception)
        {
            return Error(e)
        }
    }
}