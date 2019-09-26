package com.zimplifica.redipuntos.services.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.zimplifica.redipuntos.R
import com.zimplifica.redipuntos.RPApplication

class PushListenerService : FirebaseMessagingService() {
    val TAG = PushListenerService::class.java.simpleName

    override fun onNewToken(p0: String?) {
        super.onNewToken(p0)
        Log.d(TAG, "Registering push notifications token: $p0")
        val app = application as RPApplication
        val environment = app.component()?.environment()
        val api = environment?.userUseCase()
        api?.registPushNotificationToken(p0?:"",environment.currentUser().getCurrentUser()?.id ?: "")
    }
    override fun onMessageReceived(p0: RemoteMessage?) {
        super.onMessageReceived(p0)
        Log.d(TAG, "Message: " + p0?.data)

        if(!p0?.data.isNullOrEmpty()){
            val title = p0?.data?.get("pinpoint.notification.title")
            val body = p0?.data?.get("pinpoint.notification.body")
            val channelId = getString(R.string.default_notification_channel_id)
            val defaultSong = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val notification = NotificationCompat.Builder(this,channelId)
                .setSound(defaultSong)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setStyle(NotificationCompat.BigTextStyle()
                    .bigText(body))
                .build()

            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                val channel = NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT)
                manager.createNotificationChannel(channel)
            }
            manager.notify(0,notification)
        }
    }
}