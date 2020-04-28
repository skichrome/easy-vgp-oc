package com.skichrome.oc.easyvgp.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.navigation.NavDeepLinkBuilder
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.skichrome.oc.easyvgp.EasyVGPApplication
import com.skichrome.oc.easyvgp.R
import com.skichrome.oc.easyvgp.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext

class DownloadReportWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params)
{
    // =================================
    //              Fields
    // =================================

    private val storage = Firebase.storage

    // =================================
    //        Superclass Methods
    // =================================

    @SuppressLint("RestrictedApi")
    override suspend fun doWork(): Result = coroutineScope {
        val machine = inputData.getString(KEY_REPORT_MACHINE_WORK)
        val pdfLocation = inputData.getString(KEY_PDF_WORK)
        val reportId = inputData.getString(KEY_REPORT_ID_WORK)
        val extraId = inputData.getString(KEY_REPORT__EXTRA_ID_WORK)?.toLongOrNull()
        val date = inputData.getString(KEY_REPORT_DATE_WORK)
        val customerId = inputData.getString(KEY_REPORT_CUSTOMER_ID)?.toLongOrNull()
        val machineId = inputData.getString(KEY_REPORT_MACHINE_ID)?.toLongOrNull()
        val machineTypeId = inputData.getString(KEY_REPORT_MACHINE_TYPE_ID)?.toLongOrNull()

        if (machine != null && pdfLocation != null && reportId != null && date != null && extraId != null && customerId != null && machineId != null && machineTypeId != null)
        {
            val localFile = downloadReport(location = pdfLocation, machine = machine, reportId = reportId)
            if (localFile != null)
            {
                val localResult = updateDatabase(extraId = extraId, reportRemotePath = pdfLocation, reportLocalFileName = localFile)
                if (localResult == -1)
                {
                    sendNotification(
                        notificationStyle = getErrorStyleForNotification(date = date, machine = machine),
                        customerId = customerId,
                        machineTypeId = machineTypeId,
                        machineId = machineId
                    )
                    Result.failure()
                }
                else
                {
                    sendNotification(
                        notificationStyle = getSuccessStyleForNotification(date = date, machine = machine),
                        customerId = customerId,
                        machineTypeId = machineTypeId,
                        machineId = machineId
                    )
                    Result.Success()
                }
            }
            else
            {
                sendNotification(
                    notificationStyle = getErrorStyleForNotification(date = date, machine = machine),
                    customerId = customerId,
                    machineTypeId = machineTypeId,
                    machineId = machineId
                )
                Result.failure()
            }
        }
        else Result.Failure()
    }

    // =================================
    //              Methods
    // =================================

    private suspend fun downloadReport(location: String, machine: String, reportId: String): String? = withContext(Dispatchers.IO) {
        applicationContext.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
            ?.createOrGetFile(PDF_FOLDER_NAME, "$reportId-$machine.pdf")
            ?.let { reportFile ->
                storage.reference.child(location)
                    .getFile(reportFile)
                    .addOnSuccessListener { task ->
                        Log.d("Worker", "Successfully downloaded report in ${reportFile.absolutePath}, ${task.bytesTransferred}")
                    }
                    .addOnFailureListener {
                        Log.e("Worker", "An error occurred with Worker", it)
                    }
                    .await()
                return@withContext Uri.fromFile(reportFile).lastPathSegment
            }
    }

    private suspend fun updateDatabase(extraId: Long, reportRemotePath: String, reportLocalFileName: String) = withContext(Dispatchers.IO) {
        return@withContext try
        {
            val machineCtrlPtExtraDao = (applicationContext as EasyVGPApplication).database.machineControlPointDataExtraDao()
            val matchingExtra = machineCtrlPtExtraDao.getExtraFromId(extraId)
            matchingExtra.isValid = true
            matchingExtra.reportRemotePath = reportRemotePath
            matchingExtra.reportLocalPath = reportLocalFileName
            machineCtrlPtExtraDao.update(matchingExtra)
        }
        catch (e: Exception)
        {
            Log.e("DownloadReportWorker", "An error occurred when updating database", e)
            -1
        }
    }

    private fun sendNotification(notificationStyle: NotificationCompat.BigTextStyle, customerId: Long, machineId: Long, machineTypeId: Long)
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

        val channelId = applicationContext.getString(R.string.cloud_msg_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.easy_vgp_icon)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
            .setStyle(notificationStyle)

        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            val channel = NotificationChannel(
                channelId,
                applicationContext.getString(R.string.cloud_msg_notification_channel),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(NOTIFICATION_FCM_ID, notificationBuilder.build())
    }

    private fun getSuccessStyleForNotification(date: String, machine: String) = NotificationCompat.BigTextStyle()
        .bigText(applicationContext.getString(R.string.cloud_msg_notification_success_content, date, machine))
        .setBigContentTitle(applicationContext.getString(R.string.cloud_msg_notification_success_title))

    private fun getErrorStyleForNotification(date: String, machine: String) = NotificationCompat.BigTextStyle()
        .bigText(applicationContext.getString(R.string.cloud_msg_notification_error_content, date, machine))
        .setBigContentTitle(applicationContext.getString(R.string.cloud_msg_notification_error_title))
}