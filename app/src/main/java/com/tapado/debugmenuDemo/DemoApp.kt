package com.tapado.debugmenuDemo

import android.app.Activity
import android.app.Application
import com.tapado.debugmenuDemo.data.demoDataStore
import com.tapado.debugmenuDemo.push.MockPushService
import com.tapado.debugmenuDemo.push.PushModule
import com.tapado.debugmenuDemo.push.PushNotificationManager
import com.tapadoo.debugmenu.DebugMenuAttacher
import com.tapadoo.debugmenu.analytics.AnalyticsModule
import com.tapadoo.debugmenu.datastore.DataStoreModule
import com.tapadoo.debugmenu.dynamic.DynamicAction
import com.tapadoo.debugmenu.dynamic.DynamicModule
import timber.log.Timber

class DemoApp : Application() {

    /** Shared mock push service used across the app. */
    lateinit var mockPushService: MockPushService
        private set

    override fun onCreate() {
        super.onCreate()

        // Create notification channels before any push notifications arrive
        PushNotificationManager.createChannels(this)

        // Initialize the mock push service
        mockPushService = MockPushService(this)

        Timber.d("DemoApp: push notifications initialized with ${com.tapado.debugmenuDemo.push.PushEvent.HARDCODED_PAYLOADS.size} hardcoded payloads")

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
                        },
                        DynamicAction("Send Test Push") {
                            mockPushService.simulateIncomingPush()
                        },
                        DynamicAction("Start Auto-Push (5x)") {
                            mockPushService.startAutoSimulation(5)
                        },
                    )
                ),
                PushModule(),
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
