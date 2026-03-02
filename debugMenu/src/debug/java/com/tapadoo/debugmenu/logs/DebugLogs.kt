package com.tapadoo.debugmenu.logs

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList

object DebugLogs {

    internal val events: SnapshotStateList<LogItem> = mutableStateListOf()


    fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        events.add(
            LogItem(
                priority = priority,
                tag = tag,
                message = message,
                throwable = t
            )
        )
    }

    fun clear() {
        events.clear()
    }

}
