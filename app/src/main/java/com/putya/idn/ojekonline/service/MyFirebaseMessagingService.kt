package com.putya.idn.ojekonline.service

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.putya.idn.ojekonline.MainActivity
import com.putya.idn.ojekonline.R

@Suppress("DEPRECATION")
class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(p0: RemoteMessage) {
        showNotification()
    }

    @SuppressLint("WrongConstant")
    private fun showNotification() {
        val notificationBuilder = NotificationCompat.Builder(this)
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pageIntent = PendingIntent.getActivity(this, 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK)

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        notificationBuilder
            .setSmallIcon(R.drawable.icon)
            .setBadgeIconType(R.drawable.icon)
            .setLargeIcon(BitmapFactory.decodeResource(this.resources, R.drawable.icon))
            .setContentIntent(pageIntent).setContentTitle("Ada new order nih, ambil yuk!")
            .setContentText("Check Listmu!").setAutoCancel(true).setSound(defaultSoundUri)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(0, notificationBuilder.build())
    }
}
