package com.tapado.debugmenuDemo.push

/**
 * Represents a simulated push notification received by the demo app.
 */
data class PushEvent(
    val title: String,
    val body: String,
    val data: Map<String, String> = emptyMap(),
    val channelId: String = PushNotificationManager.CHANNEL_DEFAULT,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val notificationId: Int = timestamp.hashCode(),
) {
    companion object {
        /** Hardcoded push payloads to simulate realistic notifications. */
        val HARDCODED_PAYLOADS = listOf(
            PushEvent(
                title = "New follower",
                body = "jane_doe92 started following you",
                data = mapOf("type" to "social", "user_id" to "42")
            ),
            PushEvent(
                title = "Message received",
                body = "Alex: Hey, are you free for lunch today?",
                data = mapOf("type" to "chat", "conversation_id" to "chat_819")
            ),
            PushEvent(
                title = "Order shipped!",
                body = "Your package #ORD-4821 is on its way — delivery expected tomorrow",
                data = mapOf("type" to "order", "order_id" to "ORD-4821"),
                channelId = PushNotificationManager.CHANNEL_ORDERS
            ),
            PushEvent(
                title = "Price drop alert",
                body = "The item in your wishlist just dropped 20% — grab it now!",
                data = mapOf("type" to "promo", "promo_code" to "WINTER20")
            ),
            PushEvent(
                title = "Security alert",
                body = "New sign-in from Chrome on macOS. Was this you?",
                data = mapOf("type" to "security", "action" to "review"),
                channelId = PushNotificationManager.CHANNEL_ALERTS
            ),
            PushEvent(
                title = "Weekly digest ready",
                body = "You received 12 likes and 3 new messages this week",
                data = mapOf("type" to "digest", "week" to "26")
            ),
            PushEvent(
                title = "App update available",
                body = "Version 3.2.0 is ready to install — includes bug fixes and performance improvements",
                data = mapOf("type" to "system", "version" to "3.2.0"),
                channelId = PushNotificationManager.CHANNEL_UPDATES
            ),
            PushEvent(
                title = "Event reminder",
                body = "Tech Meetup: Kotlin Multiplatform workshop starts in 30 minutes",
                data = mapOf("type" to "event", "event_id" to "evt_005")
            ),
        )
    }
}
