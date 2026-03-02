package com.tapadoo.debugmenu.logs

import androidx.compose.runtime.Composable
import com.tapadoo.debugmenu.module.DebugMenuModule

class LoggingModule : DebugMenuModule {
    override val title: String = "Logs"

    @Composable
    override fun Content() {
        // No-op in release builds
    }
}
