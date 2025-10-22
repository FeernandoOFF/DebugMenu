package com.tapado.debugmenuDemo.analytics

import android.os.Bundle
import com.tapadoo.debugmenu.analytics.DebugAnalytics

/**
 * A simple mock analytics manager for the demo app. It forwards events to the DebugAnalytics store
 * so they appear in the Debug Menu analytics tab.
 */
class MockAnalyticsManager {
    fun logEvent(name: String, params: Bundle = Bundle()) {
        DebugAnalytics.logEvent(name, params)
    }

    fun logScreen(screenName: String) {
        val bundle = Bundle().apply { putString("screen", screenName) }
        DebugAnalytics.logEvent("screen_view", bundle)
    }
}