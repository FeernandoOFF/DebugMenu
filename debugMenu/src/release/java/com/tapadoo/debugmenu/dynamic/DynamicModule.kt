package com.tapadoo.debugmenu.dynamic

import androidx.compose.runtime.Composable
import com.tapadoo.debugmenu.module.DebugMenuModule

class DynamicModule(
    override val title: String,
    val globalActions: List<DynamicAction> = emptyList()
) : DebugMenuModule {

    @Composable
    override fun Content() {
        // No-op in release builds
    }
}
