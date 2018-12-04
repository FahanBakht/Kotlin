package com.example.muhammadfarhan.firebasefcm

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.app.NotificationManager
import android.media.RingtoneManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import android.util.Log


/**
 * Created by MuhammadFarhan on 31/08/2018.
 */

private const val JSON_KEY_AUTHOR = "Author"
private const val JSON_KEY_MESSAGE = "Message"
private const val NOTIFICATION_MAX_CHARACTERS = 30

class MyFirebaseMessagingService : FirebaseMessagingService() {

    @Override
    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        //super.onMessageReceived(remoteMessage)

        if (remoteMessage?.data!!.isNotEmpty()) {
            //showNotification(remoteMessage.data.get("name")!!, remoteMessage.getData().get("status")!!);
            Log.e("onMessageReceived", "messege received" + remoteMessage.data)
            sendNotification(remoteMessage.data)
        } else {
            //showNotification("P1","approved nhi ks ath")
        }

    }

    override fun onNewToken(p0: String?) {
        super.onNewToken(p0)
        Log.e("onNewToken", p0)
    }

    private fun sendNotification(data: Map<String, String>) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        // Create the pending intent to launch the activity
        val pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT)

        val author: String? = data[JSON_KEY_AUTHOR]
        var message: String? = data[JSON_KEY_MESSAGE]

        // If the message is longer than the max number of characters we want in our
        // notification, truncate it and add the unicode character for ellipsis
        if (message!!.length > NOTIFICATION_MAX_CHARACTERS) {
            message = message.substring(0, NOTIFICATION_MAX_CHARACTERS) + "\u2026"
        }

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(String.format(getString(R.string.notification_message), author))
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
    }
}