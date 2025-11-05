package com.tapado.debugmenuDemo

import android.app.Application

class DemoApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // Demo: Attach the DebugMenu using the Attacher (no Compose dependency required in consumer app)
//        DebugMenuAttacher.attachToApplication(
//            this,
//            listOf(
//                AnalyticsModule(),
//                DataStoreModule(listOf(this.applicationContext.demoDataStore)),
//                DynamicModule(
//                    title = "Custom Module",
//                    globalActions = listOf(
//                        DynamicAction("Global Action 1") {
//                            // Perform global action
//                        }
//                    )
//                ),
//            ))
    }
}