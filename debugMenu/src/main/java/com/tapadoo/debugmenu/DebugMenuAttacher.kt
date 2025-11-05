package com.tapadoo.debugmenu

import android.app.Activity
import android.app.Application
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.ui.platform.ComposeView
import androidx.core.view.ViewCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.tapadoo.debugmenu.module.DebugMenuModule

/**
 * Attaches the Debug Menu overlay to any Activity without requiring the app module to depend on
 * Compose APIs directly. Call once from your Activity onCreate().
 */
object DebugMenuAttacher {

    @JvmStatic
    fun attachToApplication(
        application: Application,
        modules: List<DebugMenuModule>,
    ) {
        application.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: android.os.Bundle?) {
                DebugMenuAttacher.attach(activity, modules)
            }

            override fun onActivityStarted(activity: Activity) {}
            override fun onActivityResumed(activity: Activity) {}
            override fun onActivityPaused(activity: Activity) {}
            override fun onActivityStopped(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, outState: android.os.Bundle) {}
            override fun onActivityDestroyed(activity: Activity) {}
        })
    }


    @OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
    @JvmStatic
    fun attach(
        activity: Activity,
        modules: List<DebugMenuModule>,
    ) = runCatching {
        val decor = activity.window?.decorView as? ViewGroup ?: return@runCatching
        // Avoid duplicates
        val existing = decor.findViewWithTag<FrameLayout>(TAG)
        if (existing != null) return@runCatching

        val container = FrameLayout(activity).apply {
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            isClickable = false
            isFocusable = false
            isFocusableInTouchMode = false
            importantForAccessibility = ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_NO
            tag = TAG
        }

        val composeView = ComposeView(activity).apply {
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setContent {
                DebugMenuOverlay(
                    showFab = true,
                    modules = modules,
                )
            }
        }
        container.addView(composeView)
        decor.addView(container)
    }

    private const val TAG = "debugMenu_overlay_container"
}