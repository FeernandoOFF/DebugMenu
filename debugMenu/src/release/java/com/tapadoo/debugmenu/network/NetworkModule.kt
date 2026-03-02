package com.tapadoo.debugmenu.network

import androidx.compose.runtime.Composable
import com.tapadoo.debugmenu.module.DebugMenuModule

class NetworkModule : DebugMenuModule {
    override val title: String = "Network"

    @Composable
    override fun Content() {
        // No-op in release builds
    }
}
