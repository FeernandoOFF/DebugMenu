package com.tapadoo.debugmenu.analytics

import androidx.compose.runtime.Composable
import com.tapadoo.debugmenu.module.DebugMenuModule

class AnalyticsModule : DebugMenuModule {

    override val title = "Analytics"

    @Composable
    override fun Content() {
        // No-op in release builds
    }
}
