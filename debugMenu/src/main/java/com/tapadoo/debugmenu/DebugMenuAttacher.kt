package com.tapadoo.debugmenu

import android.app.Activity
import android.app.Application
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.ui.platform.ComposeView
import androidx.core.view.ViewCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.findViewTreeSavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
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
        showFab: Boolean = true,
        enableShake: Boolean = false,
    ) {
        application.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: android.os.Bundle?) {
                DebugMenuAttacher.attach(activity, modules, showFab, enableShake)
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
        showFab: Boolean = true,
        enableShake: Boolean = false,
    ) = runCatching {
        val decor = activity.window?.decorView as? ViewGroup ?: return@runCatching

        // Avoid duplicates
        val existing = decor.findViewWithTag<FrameLayout>(TAG)
        if (existing != null) return@runCatching

        // Ensure ViewTree owners are set for Compose.
        // Some activities like Splash Screens might not call setContentView(), so these are missing.
        if (decor.findViewTreeLifecycleOwner() == null) {
            (activity as? LifecycleOwner)?.let {
                decor.setViewTreeLifecycleOwner(it)
            }
        }
        if (decor.findViewTreeViewModelStoreOwner() == null) {
            (activity as? ViewModelStoreOwner)?.let {
                decor.setViewTreeViewModelStoreOwner(it)
            }
        }
        if (decor.findViewTreeSavedStateRegistryOwner() == null) {
            (activity as? SavedStateRegistryOwner)?.let {
                decor.setViewTreeSavedStateRegistryOwner(it)
            }
        }

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
                    showFab = showFab,
                    enableShake = enableShake,
                    modules = modules,
                )
            }
        }
        container.addView(composeView)
        decor.addView(container)
    }

    private const val TAG = "debugMenu_overlay_container"
}