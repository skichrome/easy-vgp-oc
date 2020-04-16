package com.skichrome.oc.easyvgp.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.skichrome.oc.easyvgp.R
import com.skichrome.oc.easyvgp.util.MAIN_ACTIVITY_FRAGMENT_ROUTE
import com.skichrome.oc.easyvgp.util.NOTIFICATION_FCM_ID
import com.skichrome.oc.easyvgp.util.RC_FCM
import com.skichrome.oc.easyvgp.view.MainActivity

class ReportReadyNotificationService : FirebaseMessagingService()
{
    override fun onNewToken(newToken: String)
    {
        Log.d("FirebaseCloudMessage", "Warning, new token has been generated")
        super.onNewToken(newToken)
    }

    override fun onMessageReceived(message: RemoteMessage)
    {
        Log.e("FirebaseCloudMessage", "New message received from ${message.data["customer"]}")
        sendNotification(messageData = message.data)
        super.onMessageReceived(message)
    }

    private fun sendNotification(messageData: Map<String, String>)
    {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra(MAIN_ACTIVITY_FRAGMENT_ROUTE, true)

        val pendingIntent = PendingIntent.getActivity(this, RC_FCM, intent, PendingIntent.FLAG_ONE_SHOT)

        val channelId = getString(R.string.cloud_msg_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.easy_vgp_icon)
            .setContentTitle(getString(R.string.cloud_msg_notification_title))
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(getString(R.string.cloud_msg_notification_content, messageData["date"], messageData["machine"]))
            )

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            val channel = NotificationChannel(
                channelId,
                getString(R.string.cloud_msg_notification_channel),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(NOTIFICATION_FCM_ID, notificationBuilder.build())
    }
}