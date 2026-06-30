package com.tapadoo.debugmenu

import android.app.Activity
import android.app.Application
import android.util.Log
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
 * Compose APIs directly.
 *
 * Supports an optional [activityFilter] to skip certain Activities (e.g. Splash screens,
 * deep-link landing pages launched from push notifications) that may not have the proper
 * ViewTree lifecycle owners set up for Compose.
 */
object DebugMenuAttacher {

    private const val TAG = "DebugMenuAttacher"
    private const val OVERLAY_TAG = "debugMenu_overlay_container"

    /**
     * Registers an [Application.ActivityLifecycleCallbacks] that attaches the debug menu
     * to every Activity that passes the optional [activityFilter].
     *
     * @param application The Application instance.
     * @param modules     List of debug menu modules to display.
     * @param showFab     Whether to show the floating action button.
     * @param enableShake Whether to enable shake-to-open gesture.
     * @param activityFilter Optional predicate. Only Activities for which this returns
     *                       `true` will get the debug menu attached. Useful for skipping
     *                       splash screens, deep-link activities, or push notification targets
     *                       that may not be Compose-ready.
     */
    @JvmStatic
    @JvmOverloads
    fun attachToApplication(
        application: Application,
        modules: List<DebugMenuModule>,
        showFab: Boolean = true,
        enableShake: Boolean = false,
        activityFilter: ((Activity) -> Boolean)? = null,
    ) {
        application.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: android.os.Bundle?) {
                // Defense-in-depth: catch any unexpected exception here so the lifecycle
                // callback never brings down the app.
                try {
                    if (activityFilter == null || activityFilter(activity)) {
                        attach(activity, modules, showFab, enableShake)
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to attach DebugMenu to ${activity.localClassName}", e)
                }
            }

            override fun onActivityStarted(activity: Activity) {}
            override fun onActivityResumed(activity: Activity) {}
            override fun onActivityPaused(activity: Activity) {}
            override fun onActivityStopped(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, outState: android.os.Bundle) {}
            override fun onActivityDestroyed(activity: Activity) {}
        })
    }

    /**
     * Attaches the debug menu overlay to a single [activity].
     *
     * Safe to call multiple times — duplicate overlays are detected and skipped.
     */
    @OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
    @JvmStatic
    @JvmOverloads
    fun attach(
        activity: Activity,
        modules: List<DebugMenuModule>,
        showFab: Boolean = true,
        enableShake: Boolean = false,
    ) = runCatching {
        val decor = activity.window?.decorView as? ViewGroup ?: return@runCatching

        // Avoid duplicates
        val existing = decor.findViewWithTag<FrameLayout>(OVERLAY_TAG)
        if (existing != null) return@runCatching

        // Ensure ViewTree owners are set for Compose.
        // Some activities like Splash Screens or activities launched from push
        // notifications might not call setContentView(), so these are missing.
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

        // Final validation: if Compose-critical owners are still missing after our
        // best-effort attempt, skip attaching to prevent runtime crashes.
        if (decor.findViewTreeLifecycleOwner() == null ||
            decor.findViewTreeViewModelStoreOwner() == null
        ) {
            Log.w(TAG, "Skipping attach to ${activity.localClassName}: " +
                "required ViewTree owners are not available and the Activity does not " +
                "implement the necessary lifecycle interfaces")
            return@runCatching
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
            tag = OVERLAY_TAG
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
}
