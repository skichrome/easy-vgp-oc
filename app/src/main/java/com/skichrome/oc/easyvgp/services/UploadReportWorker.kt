package com.skichrome.oc.easyvgp.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.navigation.NavDeepLinkBuilder
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.google.firebase.storage.ktx.storageMetadata
import com.skichrome.oc.easyvgp.EasyVGPApplication
import com.skichrome.oc.easyvgp.R
import com.skichrome.oc.easyvgp.model.Results
import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.local.ChoicePossibility
import com.skichrome.oc.easyvgp.model.local.VerificationType
import com.skichrome.oc.easyvgp.model.local.database.*
import com.skichrome.oc.easyvgp.model.remote.util.*
import com.skichrome.oc.easyvgp.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.LinkedHashMap

class UploadReportWorker(appContext: Context, params: WorkerParameters) : CoroutineWorker(appContext, params)
{
    // =================================
    //              Fields
    // =================================

    private val db = Firebase.firestore
    private val storage = Firebase.storage
    private val userReference = storage.reference

    // =================================
    //        Superclass Methods
    // =================================

    @SuppressLint("RestrictedApi")
    override suspend fun doWork(): Result = coroutineScope {
        val userId = inputData.getLong(KEY_LOCAL_USER_ID, -1L)
        val customerId = inputData.getLong(KEY_LOCAL_CUSTOMER_ID, -1L)
        val machineId = inputData.getLong(KEY_LOCAL_MACHINE_ID, -1L)
        val machineTypeId = inputData.getLong(KEY_LOCAL_MACHINE_TYPE_ID, -1L)
        val extraRef = inputData.getLong(KEY_LOCAL_EXTRAS_REFERENCE, -1L)
        val reportDate = inputData.getLong(KEY_LOCAL_REPORT_DATE, -1L)

        val mainResults = if (userId != -1L && customerId != -1L && machineId != -1L && machineTypeId != -1L && extraRef != -1L)
        {
            withContext(Dispatchers.IO) {
                val db = (applicationContext as EasyVGPApplication).database
                val user = db.usersDao().getUserFromId(userId)
                val customer = db.customersDao().getCustomerById(customerId)
                val machine = db.machinesDao().getMachineById(machineId)
                val machineType = db.machinesTypeDao().getMachineTypeFromId(machineTypeId)
                val extra = db.machineControlPointDataExtraDao().getExtraFromId(extraRef)
                val report = db.machineControlPointDataDao().getPreviouslyInsertedReport(extra.reportDate)

                // --- Upload machine image --- //

                machine.localPhotoRef?.let { photoRef ->
                    val photoRefUri = Uri.fromFile(File(photoRef))
                    val storedRemoteRef = machine.remotePhotoRef

                    val uploadPhotoResult = uploadImageToStorage(
                        userUid = user.user.firebaseUid,
                        localUri = photoRefUri,
                        remoteUri = storedRemoteRef,
                        filePrefix = "machine"
                    )
                    if (uploadPhotoResult is Success)
                    {
                        machine.remotePhotoRef = uploadPhotoResult.data
                        val updateResult = db.machinesDao().update(machine)
                        if (updateResult == 0)
                            return@withContext Result.failure()
                    }
                }

                // --- Upload user signature image --- //

                user.user.signaturePath?.let { signatureRef ->
                    if (!user.user.isSignatureEnabled)
                        return@let

                    val uploadResult = uploadImageToStorage(
                        userUid = user.user.firebaseUid,
                        localUri = signatureRef,
                        remoteUri = user.user.remoteSignaturePath,
                        filePrefix = "signature"
                    )
                    if (uploadResult is Success)
                    {
                        user.user.remoteSignaturePath = uploadResult.data
                        val updateLocalResult = db.usersDao().update(user.user)
                        if (updateLocalResult == 0)
                            return@withContext Result.failure()
                    }
                }

                // --- Upload user company logo --- //

                user.company.localCompanyLogo?.let { logoRef ->
                    val uploadResult = uploadImageToStorage(
                        userUid = user.user.firebaseUid,
                        localUri = logoRef,
                        remoteUri = user.company.remoteCompanyLogo,
                        filePrefix = "logo"
                    )
                    if (uploadResult is Success)
                    {
                        user.company.remoteCompanyLogo = uploadResult.data
                        val updateLocalResult = db.companiesDao().update(user.company)
                        if (updateLocalResult == 0)
                            return@withContext Result.failure()
                    }
                }

                // --- Upload report --- //

                val reportUploadResult = generateReport(
                    user = user,
                    customer = customer,
                    machineType = machineType,
                    machine = machine,
                    reports = report,
                    reportDate = extra.reportDate,
                    reportExtra = extra
                )
                if (reportUploadResult is Results.Error)
                    return@withContext Result.failure()

                return@withContext Result.Success()
            }
        }
        else
            Result.failure()

        return@coroutineScope when (mainResults)
        {
            Result.success() -> mainResults
            Result.failure() ->
            {
                sendErrorNotification(reportDate = reportDate, customerId = customerId, machineId = machineId, machineTypeId = machineTypeId)
                Result.failure()
            }
            else -> mainResults
        }
    }

    // =================================
    //              Methods
    // =================================

    private suspend fun uploadImageToStorage(userUid: String, localUri: Uri, remoteUri: Uri?, filePrefix: String): Results<Uri> =
        withContext(Dispatchers.IO) {
            return@withContext try
            {
                val metadata = storageMetadata {
                    contentType = "image/jpg"
                }

                val userPhotoReference =
                    userReference.child("$REMOTE_USER_STORAGE/$userUid/$PICTURES_FOLDER_NAME/$filePrefix-${localUri.path?.split("/")?.last()}")

                remoteUri?.let {
                    val oldUserPhotoReference =
                        userReference.child("$REMOTE_USER_STORAGE/$userUid/$PICTURES_FOLDER_NAME/${it.path?.split("/")?.last()}")

                    if (userPhotoReference != oldUserPhotoReference)
                    {
                        Log.e("RemoteVgpListSrc", "Remote reference need to be updated")
                        oldUserPhotoReference.delete()
                        return@let uploadToStorage(reference = userPhotoReference, uri = localUri, metadata = metadata)
                    }
                    else
                        return@let Success(remoteUri)
                } ?: uploadToStorage(reference = userPhotoReference, uri = localUri, metadata = metadata)
            }
            catch (e: Exception)
            {
                Results.Error(e)
            }
        }

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
            return Results.Error(e)
        }
    }

    private suspend fun generateReport(
        reportDate: Long,
        user: UserAndCompany,
        customer: Customer,
        machine: Machine,
        machineType: MachineType,
        reports: List<Report>,
        reportExtra: MachineControlPointDataExtra
    ): Results<Boolean> =
        withContext(Dispatchers.IO) {
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
                    vatNumber = user.user.vatNumber,
                    companyLogo = user.company.remoteCompanyLogo.toString(),
                    signaturePath = user.user.remoteSignaturePath.toString()
                )

                val remoteCustomer = RemoteCustomer(
                    id = customer.id,
                    address = customer.address,
                    city = customer.city,
                    email = customer.email,
                    companyName = customer.companyName,
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
                    val verificationType =
                        applicationContext.getString(VerificationType.values()[it.ctrlPointData.ctrlPointVerificationType - 1].verification)
                    val possibility = applicationContext.getString(ChoicePossibility.values()[it.ctrlPointData.ctrlPointPossibility - 1].choice)

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

                val remoteExtra = RemoteMachineControlPointDataExtra(
                    id = reportExtra.id,
                    reportEndDate = reportExtra.reportEndDate,
                    machineHours = reportExtra.machineHours,
                    interventionPlace = reportExtra.interventionPlace,
                    controlType = reportExtra.controlType.id,
                    machineNotice = reportExtra.machineNotice,
                    isMachineClean = reportExtra.isMachineClean,
                    isLiftingEquip = reportExtra.isLiftingEquip,
                    isMachineCE = reportExtra.isMachineCE,
                    reportDate = reportExtra.reportDate
                )

                val remoteReport = RemoteReportData(
                    user = remoteUser,
                    customer = remoteCustomer,
                    machine = remoteMachine,
                    machineType = remoteMachineType,
                    reportData = remoteReportCtrlPointData,
                    reportCtrlPoint = remoteReportCtrlPoint,
                    reportDataExtra = remoteExtra
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
                Results.Error(e)
            }
        }

    private fun getUserCollection(userUid: String) = db.collection(REMOTE_USER_COLLECTION)
        .document(userUid)
        .collection(REMOTE_REPORT_COLLECTION)

    private fun sendErrorNotification(reportDate: Long, customerId: Long, machineId: Long, machineTypeId: Long)
    {
        val bundle = Bundle().apply {
            putLong(MAIN_ACTIVITY_EXTRA_CUSTOMER, customerId)
            putLong(MAIN_ACTIVITY_EXTRA_MACHINE, machineId)
            putLong(MAIN_ACTIVITY_EXTRA_MACHINE_TYPE, machineTypeId)
        }

        val pendingIntent = NavDeepLinkBuilder(applicationContext)
            .setGraph(R.navigation.main_navigation)
            .setDestination(R.id.vgpListFragment)
            .setArguments(bundle)
            .createPendingIntent()

        val channelId = applicationContext.getString(R.string.upload_report_worker_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.easy_vgp_icon)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
            .setContentText(
                applicationContext.getString(
                    R.string.upload_report_worker_notification_content,
                    SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(reportDate)
                )
            )

        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            val channel = NotificationChannel(
                channelId,
                applicationContext.getString(R.string.upload_report_worker_notification_channel),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(NOTIFICATION_UPLOAD_ID, notificationBuilder.build())
    }
}