package com.tapadoo.debugmenu.dynamic

import androidx.compose.runtime.Composable
import androidx.lifecycle.LifecycleOwner

object DynamicModuleState {

    fun addAction(title: String, description: String? = null, onClick: () -> Unit) {
        // No-op in release builds
    }

    fun remove(option: DynamicAction) {
        // No-op in release builds
    }

    fun clear() {
        // No-op in release builds
    }
}

@Composable
fun DynamicModuleActions(lifecycleOwner: LifecycleOwner, vararg options: DynamicAction) {
    // No-op in release builds
}

fun LifecycleOwner.registerDynamicModuleActions(vararg options: DynamicAction) {
    // No-op in release builds
}
