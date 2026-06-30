package com.tapado.debugmenuDemo.push

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.tapadoo.debugmenu.logs.DebugLogs

/**
 * Manages Android notification channels and posting notifications for the demo app.
 *
 * Notification channels:
 * - [CHANNEL_DEFAULT]: General notifications (social, chat, etc.)
 * - [CHANNEL_ORDERS]: Order/shipping notifications
 * - [CHANNEL_ALERTS]: Security/alert notifications
 * - [CHANNEL_UPDATES]: App/tooling updates
 */
object PushNotificationManager {

    const val CHANNEL_DEFAULT = "demo_push_general"
    const val CHANNEL_ORDERS = "demo_push_orders"
    const val CHANNEL_ALERTS = "demo_push_alerts"
    const val CHANNEL_UPDATES = "demo_push_updates"

    private const val CHANNEL_DEFAULT_NAME = "General"
    private const val CHANNEL_ORDERS_NAME = "Orders"
    private const val CHANNEL_ALERTS_NAME = "Alerts"
    private const val CHANNEL_UPDATES_NAME = "Updates"

    /**
     * Creates all notification channels. Should be called once during Application.onCreate().
     */
    fun createChannels(context: Context) {
        val channels = listOf(
            NotificationChannel(
                CHANNEL_DEFAULT, CHANNEL_DEFAULT_NAME, NotificationManager.IMPORTANCE_DEFAULT
            ).apply { description = "General notifications from the demo app" },
            NotificationChannel(
                CHANNEL_ORDERS, CHANNEL_ORDERS_NAME, NotificationManager.IMPORTANCE_HIGH
            ).apply { description = "Order updates and shipping confirmations" },
            NotificationChannel(
                CHANNEL_ALERTS, CHANNEL_ALERTS_NAME, NotificationManager.IMPORTANCE_HIGH
            ).apply { description = "Security alerts and warnings" },
            NotificationChannel(
                CHANNEL_UPDATES, CHANNEL_UPDATES_NAME, NotificationManager.IMPORTANCE_LOW
            ).apply { description = "App updates and system notifications" },
        )

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        channels.forEach { manager.createNotificationChannel(it) }

        DebugLogs.log("PushChannels", "Created ${channels.size} notification channels")
    }

    /**
     * Posts a single [PushEvent] as an Android system notification.
     * On Android 13+, the notification is only posted if POST_NOTIFICATIONS permission is granted.
     */
    fun postNotification(context: Context, event: PushEvent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                DebugLogs.log("PushNotificationManager",
                    "Skipping notification — POST_NOTIFICATIONS not granted")
                return
            }
        }

        // Build a basic click intent (re-open the app)
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)?.apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("push_title", event.title)
            putExtra("push_body", event.body)
        }

        val pendingIntent = PendingIntent.getActivity(
            context, event.notificationId, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, event.channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(event.title)
            .setContentText(event.body)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setStyle(NotificationCompat.BigTextStyle().bigText(event.body))
            .build()

        NotificationManagerCompat.from(context).notify(event.notificationId, notification)

        DebugLogs.log("PushNotificationManager",
            "Posted notification: ${event.title} (id=${event.notificationId})")
    }
}
