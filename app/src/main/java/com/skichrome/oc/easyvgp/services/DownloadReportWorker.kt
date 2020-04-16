package com.skichrome.oc.easyvgp.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.skichrome.oc.easyvgp.R
import com.skichrome.oc.easyvgp.util.*
import com.skichrome.oc.easyvgp.view.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext

class DownloadReportWorker(
    context: Context,
    params: WorkerParameters
) :
    CoroutineWorker(context, params)
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
        val date = inputData.getString(KEY_REPORT_DATE_WORK)

        if (machine != null && pdfLocation != null && reportId != null && date != null)
        {
            val isSuccess = downloadReport(location = pdfLocation, machine = machine, reportId = reportId)
            if (isSuccess == true)
            {
                sendNotification(notificationStyle = getSuccessStyleForNotification(date = date, machine = machine))
                Result.Success()
            }
            else
            {
                sendNotification(notificationStyle = getErrorStyleForNotification(date = date, machine = machine))
                Result.failure()
            }
        }
        else Result.Failure()
    }

    // =================================
    //              Methods
    // =================================

    private suspend fun downloadReport(location: String, machine: String, reportId: String) = withContext(Dispatchers.IO) {
        applicationContext.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
            ?.createOrGetPdfFile(PDF_FOLDER_NAME, "$reportId-$machine")
            ?.let { reportFile ->
                var isSuccess = false

                storage.reference.child(location)
                    .getFile(reportFile)
                    .addOnSuccessListener { task ->
                        Log.d("Worker", "Successfully downloaded report in ${reportFile.absolutePath}, ${task.bytesTransferred}")
                    }
                    .addOnFailureListener {
                        Log.e("Worker", "An error occurred with Worker", it)
                    }
                    .addOnCompleteListener {
                        isSuccess = it.isSuccessful
                    }
                    .await()
                return@withContext isSuccess
            }
    }

    private fun sendNotification(notificationStyle: NotificationCompat.BigTextStyle)
    {
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra(MAIN_ACTIVITY_FRAGMENT_ROUTE, true)

        val pendingIntent = PendingIntent.getActivity(applicationContext, RC_FCM, intent, PendingIntent.FLAG_ONE_SHOT)

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