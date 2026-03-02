package com.tapadoo.debugmenu.dynamic

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner


/**
 * A singleton object that manages the state of dynamic actions in a module.
 */
object DynamicModuleState {

    val action: SnapshotStateList<DynamicAction> = mutableStateListOf()

    fun addAction(title: String, description: String? = null, onClick: () -> Unit) {
        action.add(DynamicAction(title, description, onClick))
    }

    fun remove(option: DynamicAction) = action.remove(option)

    fun clear() = action.clear()
}

/**
 * A composable function that dynamically manages and displays module actions
 * based on the lifecycle events of the provided `LifecycleOwner`.
 *
 * @param lifecycleOwner The lifecycle owner that controls the lifecycle of the dynamic actions.
 * @param options A variable number of `DynamicAction` objects representing the actions
 * to be added or removed dynamically based on lifecycle state.
 */
@Composable
fun DynamicModuleActions(lifecycleOwner: LifecycleOwner, vararg options: DynamicAction) {
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                options.forEach { DynamicModuleState.addAction(it.title, it.description, it.onClick) }
            }
            if (event == Lifecycle.Event.ON_PAUSE) {
                options.forEach { DynamicModuleState.remove(it) }
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            options.forEach { DynamicModuleState.remove(it) }
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

/**
 * Extension function for LifecycleOwner to register dynamic module actions.
 *
 * @param options A variable number of DynamicAction objects to be managed.
 */
fun LifecycleOwner.registerDynamicModuleActions(vararg options: DynamicAction) {
    val observer = LifecycleEventObserver { obser, event ->
        when (event) {
            Lifecycle.Event.ON_RESUME -> options.forEach {
                DynamicModuleState.addAction(it.title, it.description, it.onClick)
            }

            Lifecycle.Event.ON_PAUSE -> options.forEach {
                DynamicModuleState.remove(it)
            }

            Lifecycle.Event.ON_DESTROY -> {
                options.forEach { DynamicModuleState.remove(it) }
            }

            else -> { /* no-op */
            }
        }
    }
    lifecycle.addObserver(observer)
}
