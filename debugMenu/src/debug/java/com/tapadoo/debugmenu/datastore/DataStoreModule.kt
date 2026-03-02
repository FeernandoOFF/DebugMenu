package com.tapadoo.debugmenu.datastore

import androidx.compose.runtime.Composable
import androidx.datastore.core.DataStore
import com.tapadoo.debugmenu.module.DebugMenuModule
import com.tapadoo.debugmenu.ui.DataStoreScreen
import androidx.datastore.preferences.core.Preferences

/**
 * A module for the debug menu providing an interface to interact with DataStore objects.
 *
 * @property dataStores A list of DataStore<Preferences> instances that are managed and displayed
 * in the module's interface.
 */
class DataStoreModule(
    val dataStores: List<DataStore<Preferences>>,
) : DebugMenuModule{
    override val title: String = "DataStore"

    @Composable
    override fun Content() {
        DataStoreScreen(dataStores)
    }

}
