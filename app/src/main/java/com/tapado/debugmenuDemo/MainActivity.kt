package com.tapado.debugmenuDemo

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.tapado.debugmenuDemo.data.demoDataStore
import com.tapado.debugmenuDemo.ui.DemoScreen
import com.tapado.debugmenuDemo.ui.DemoViewModel
import com.tapado.debugmenuDemo.ui.theme.DebugMenuTheme
import com.tapadoo.debugmenu.DebugMenuAttacher
import com.tapadoo.debugmenu.DebugMenuOverlay
import com.tapadoo.debugmenu.analytics.AnalyticsModule
import com.tapadoo.debugmenu.datastore.DataStoreModule
import com.tapadoo.debugmenu.dynamic.DynamicAction
import com.tapadoo.debugmenu.dynamic.DynamicModule
import com.tapadoo.debugmenu.logs.DebugLogs
import com.tapadoo.debugmenu.logs.LoggingModule
import com.tapadoo.debugmenu.network.DebugNetworkEvents
import com.tapadoo.debugmenu.network.DebugNetworkRequest
import com.tapadoo.debugmenu.network.NetworkModule
import timber.log.Timber
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: DemoViewModel

    /** Permission launcher for Android 13+ POST_NOTIFICATIONS. */
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        Timber.d("Notification permission granted: $isGranted")
    }

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

        // Request notification permission on Android 13+ (required to show system notifications)
        requestNotificationPermission()

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
                    // Simulate Navigation
                    if(!loggedIn) {
                        DemoScreen(viewModel = viewModel) { loggedIn = true }
                    } else {
                        Text("Logged in!")
                    }
//                    DebugMenuOverlay(
//                        modules = listOf(
//                            DynamicModule(
//                                title = "Custom Module",
//                                globalActions = listOf(
//                                    DynamicAction("Global Action 1") {
//                                          // Perform global action
//                                    },
//                                    DynamicAction("Add API Call") {
//                                        // Mocking API call / Intercept
//                                        DebugNetworkEvents.addEvent(
//                                            DebugNetworkRequest.random()
//                                        )
//                                    }
//                                )
//                            ),
//                            AnalyticsModule(),
//                            LoggingModule(),
//                            DataStoreModule(
//                                listOf(
//                                    this@MainActivity.applicationContext.demoDataStore
//                                )
//                            ),
//                            NetworkModule()
//                        ),
//                    )
                }
            }
        }
    }

    /**
     * Requests POST_NOTIFICATIONS permission on Android 13+.
     * On older versions the permission is granted at install time.
     */
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    Timber.d("Notification permission already granted")
                }
                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    Timber.d("Showing notification permission rationale")
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
                else -> {
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }
}

private fun DebugNetworkRequest.Companion.random(): DebugNetworkRequest {
    val methods = listOf("GET", "POST", "PUT", "DELETE")
    val urls = listOf(
        "https://api.example.com/users",
        "https://api.example.com/products",
        "https://api.example.com/orders"
    )
    val successful = Random.nextBoolean()

    return DebugNetworkRequest(
        url = urls.random(),
        method = methods.random(),
        headers = mapOf("Content-Type" to "application/json"),
        responseHeaders = mapOf("Content-Type" to "application/json"),
        body = """{"id": ${Random.nextInt(1000)}}""",
        timestamp = System.currentTimeMillis(),
        durationMs = Random.nextLong(100, 2000),
        isSuccessful = successful,
        code = if (successful) 200 else 400,
        error = if (!successful) "Bad Request" else null,
        response = if (successful) """{"status": "success"}""" else null,
        requestSize = Random.nextLong(100, 5000)
    )
}
