package com.tapadoo.debugmenu.analytics

import android.os.Bundle

object DebugAnalytics {

    fun logEvent(eventName: String, bundle: Bundle) {
        // No-op in release builds
    }

    fun logEvent(event: AnalyticsItem) {
        // No-op in release builds
    }

    fun clear() {
        // No-op in release builds
    }
}
