package com.tapadoo.debugmenu.network

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import com.tapadoo.debugmenu.module.DebugMenuModule


class NetworkModule : DebugMenuModule {
    override val title: String = "Network"

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    override fun Content() {
        NetworkScreen()
    }
}
