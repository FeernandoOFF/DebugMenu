package com.tapado.debugmenuDemo.push

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList

/**
 * In-memory store of push notification events for the DebugMenu PushModule.
 * Follows the same pattern as [com.tapadoo.debugmenu.network.DebugNetworkEvents].
 */
object DebugPushEvents {
    val events: SnapshotStateList<PushEvent> = mutableStateListOf()

    fun addEvent(event: PushEvent) = events.add(event)

    fun clear() = events.clear()

    /** Retrieve all unread events. */
    val unreadCount: Int get() = events.count { !it.isRead }

    /** Mark a single event as read. */
    fun markRead(event: PushEvent) {
        val idx = events.indexOf(event)
        if (idx >= 0) {
            events[idx] = event.copy(isRead = true)
        }
    }

    /** Mark all events as read. */
    fun markAllRead() {
        val updated = events.map { it.copy(isRead = true) }
        events.clear()
        events.addAll(updated)
    }
}
