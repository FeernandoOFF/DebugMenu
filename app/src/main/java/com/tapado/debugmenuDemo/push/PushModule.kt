package com.tapado.debugmenuDemo.push

import androidx.compose.runtime.Composable
import com.tapadoo.debugmenu.module.DebugMenuModule

/**
 * DebugMenu module that displays push notification history from [DebugPushEvents].
 */
class PushModule : DebugMenuModule {
    override val title: String = "Push Notifications"

    @Composable
    override fun Content() {
        PushScreen()
    }
}
