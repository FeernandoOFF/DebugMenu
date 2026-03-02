package com.tapadoo.debugmenu.datastore

import androidx.compose.runtime.Composable
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.tapadoo.debugmenu.module.DebugMenuModule

class DataStoreModule(
    val dataStores: List<DataStore<Preferences>>,
) : DebugMenuModule {
    override val title: String = "DataStore"

    @Composable
    override fun Content() {
        // No-op in release builds
    }
}
