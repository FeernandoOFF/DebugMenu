package com.tapadoo.debugmenu.logs

object DebugLogs {

    fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        // No-op in release builds
    }

    fun clear() {
        // No-op in release builds
    }
}
