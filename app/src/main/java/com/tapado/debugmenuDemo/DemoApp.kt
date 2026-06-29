package com.tapado.debugmenuDemo

import android.app.Activity
import android.app.Application
import com.tapado.debugmenuDemo.data.demoDataStore
import com.tapadoo.debugmenu.DebugMenuAttacher
import com.tapadoo.debugmenu.analytics.AnalyticsModule
import com.tapadoo.debugmenu.datastore.DataStoreModule
import com.tapadoo.debugmenu.dynamic.DynamicAction
import com.tapadoo.debugmenu.dynamic.DynamicModule

class DemoApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // Demo: Attach the DebugMenu using the Attacher (no Compose dependency required in consumer app)
        //
        // The optional activityFilter skips Activities that are not Compose-ready,
        // such as splash screens or deep-link landing pages launched from push notifications.
        DebugMenuAttacher.attachToApplication(
            this,
            listOf(
                AnalyticsModule(),
                DataStoreModule(listOf(this.applicationContext.demoDataStore)),
                DynamicModule(
                    title = "Custom Module",
                    globalActions = listOf(
                        DynamicAction("Global Action 1") {
                            // Perform global action
                        }
                    )
                ),
            ),
            activityFilter = { activity ->
                // Skip splash screens — they use installSplashScreen() and may not
                // have ViewTree lifecycle owners set up before the splash completes.
                // Also skip any activity whose class name matches common push notification
                // patterns. Customize this filter to match your app's architecture.
                activity !is SplashActivity
            }
        )
    }
}
