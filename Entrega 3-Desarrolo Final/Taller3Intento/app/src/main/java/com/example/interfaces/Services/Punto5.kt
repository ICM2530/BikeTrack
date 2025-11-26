package com.example.interfaces.Services

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.interfaces.MainActivity
import com.example.interfaces.R
import com.example.interfaces.Screens.TrackUserAct

class Punto5 {
    companion object {
        @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
        fun sendUserAvailableNotification(
            context: Context,
            name: String,
            lastName: String,
            mail: String,
            latitude: Double,
            longitude: Double
        ) {
            val channelId = "available_user_channel"

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    channelId,
                    "Available Users",
                    NotificationManager.IMPORTANCE_HIGH
                )
                val manager = context.getSystemService(NotificationManager::class.java)
                manager.createNotificationChannel(channel)
            }

            val intent = Intent(context, TrackUserAct::class.java).apply {
                putExtra("trackedMail", mail)
            }

            val pendingIntent = PendingIntent.getActivity(
                context,
                mail.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val builder = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.userbasic)
                .setContentTitle("Usuario disponible")
                .setContentText("$name $lastName ahora está disponible")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

            NotificationManagerCompat.from(context).notify(mail.hashCode(), builder.build())
        }
    }
}
