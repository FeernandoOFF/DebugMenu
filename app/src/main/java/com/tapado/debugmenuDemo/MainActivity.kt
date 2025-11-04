package com.tapado.debugmenuDemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.tapado.debugmenuDemo.data.demoDataStore
import com.tapado.debugmenuDemo.ui.DemoScreen
import com.tapado.debugmenuDemo.ui.DemoViewModel
import com.tapado.debugmenuDemo.ui.theme.DebugMenuTheme
import com.tapadoo.debugmenu.DebugMenuOverlay
import com.tapadoo.debugmenu.analytics.AnalyticsModule
import com.tapadoo.debugmenu.datastore.DataStoreModule
import com.tapadoo.debugmenu.dynamic.DynamicAction
import com.tapadoo.debugmenu.dynamic.DynamicModule
import com.tapadoo.debugmenu.logs.DebugLogs
import com.tapadoo.debugmenu.logs.LoggingModule
import timber.log.Timber

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: DemoViewModel

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.plant(
            object : Timber.DebugTree() {
                override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                    super.log(priority, tag, message, t)
                    DebugLogs.log(priority = priority, tag = "$tag", message = message, t = t)
                }
            }
        )
        val factory = DemoViewModel.Companion.Factory(applicationContext)
        viewModel = ViewModelProvider(this, factory)[DemoViewModel::class.java]
        enableEdgeToEdge()

        // Demo: Attach the DebugMenu using the Attacher (no Compose dependency required in consumer app)
//        DebugMenuAttacher.attach(
//            this,
//            listOf(
//                AnalyticsModule(),
//                DataStoreModule(listOf(this@MainActivity.applicationContext.demoDataStore)),
//                DynamicModule(
//                    title = "Custom Module",
//                    globalActions = listOf(
//                        DynamicAction("Global Action 1") {
//                          // Perform global action
//                        }
//                    )
//                ),
//            ))

        setContent {
            DebugMenuTheme {
                var loggedIn by remember { mutableStateOf(false) }

                BackHandler(loggedIn) {
                    loggedIn = false
                }

                Box(Modifier.fillMaxSize()) {
                    if(!loggedIn) {
                        DemoScreen(viewModel = viewModel) { loggedIn = true }
                    } else {
                        Text("Logged in!")
                    }
                    DebugMenuOverlay(
                        modules = listOf(
                            DynamicModule(
                                title = "Custom Module",
                                globalActions = listOf(
                                    DynamicAction("Global Action 1") {
                                          // Perform global action
                                    }
                                )
                            ),
                            AnalyticsModule(),
                            LoggingModule(),
                            DataStoreModule(
                                listOf(
                                    this@MainActivity.applicationContext.demoDataStore
                                )
                            ),
                        ),
                    )
                }
            }
        }
    }
}