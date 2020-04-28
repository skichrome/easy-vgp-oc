package com.skichrome.oc.easyvgp.services

import android.util.Log
import androidx.work.*
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.skichrome.oc.easyvgp.util.*

class ReportReadyNotificationService : FirebaseMessagingService()
{
    override fun onNewToken(newToken: String)
    {
        Log.d("FirebaseCloudMessage", "Warning, new token has been generated")
        super.onNewToken(newToken)
    }

    override fun onMessageReceived(message: RemoteMessage)
    {
        Log.d("FirebaseCloudMessage", "New message received from ${message.data}")
        scheduleWork(messageData = message.data)
        super.onMessageReceived(message)
    }

    private fun scheduleWork(messageData: Map<String, String>)
    {
        val pdfData = workDataOf(
            KEY_PDF_WORK to messageData["path"],
            KEY_REPORT_ID_WORK to messageData["reportId"],
            KEY_REPORT_MACHINE_WORK to messageData["machine"],
            KEY_REPORT_DATE_WORK to messageData["date"],
            KEY_REPORT__EXTRA_ID_WORK to messageData["extraId"],
            KEY_REPORT_MACHINE_ID to messageData["machineId"],
            KEY_REPORT_MACHINE_TYPE_ID to messageData["machineTypeId"],
            KEY_REPORT_CUSTOMER_ID to messageData["customerId"]
        )

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresStorageNotLow(true)
            .build()

        val work = OneTimeWorkRequest.Builder(DownloadReportWorker::class.java)
            .setConstraints(constraints)
            .setInputData(pdfData)
            .build()

        WorkManager.getInstance(applicationContext).beginWith(work).enqueue()
    }
}