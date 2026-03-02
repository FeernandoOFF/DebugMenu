package com.tapadoo.debugmenu.network

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList

object DebugNetworkEvents {
    val events: SnapshotStateList<DebugNetworkRequest> = mutableStateListOf()

    fun addEvent(event: DebugNetworkRequest) = events.add(event)
}
