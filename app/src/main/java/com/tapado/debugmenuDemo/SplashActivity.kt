package com.tapado.debugmenuDemo

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : ComponentActivity() {

    private var keepSplashScreen = true

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition { keepSplashScreen }

        // Some apps reported crashes when having a custom splash activity
        lifecycleScope.launch {
            delay(2000)
            keepSplashScreen = false
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            finish()
        }
    }
}
