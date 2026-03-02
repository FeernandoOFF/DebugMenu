package com.tapadoo.debugmenu

import android.app.Activity
import android.app.Application
import com.tapadoo.debugmenu.module.DebugMenuModule

object DebugMenuAttacher {

    @JvmStatic
    fun attachToApplication(
        application: Application,
        modules: List<DebugMenuModule>,
        showFab: Boolean = true,
        enableShake: Boolean = false,
    ) {
        // No-op in release builds
    }

    @JvmStatic
    fun attach(
        activity: Activity,
        modules: List<DebugMenuModule>,
        showFab: Boolean = true,
        enableShake: Boolean = false,
    ) {
        // No-op in release builds
    }
}
