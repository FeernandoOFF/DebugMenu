package com.tapado.debugmenuDemo

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
            ))
    }
}