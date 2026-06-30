package com.tapado.debugmenuDemo.push

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.tapadoo.debugmenu.logs.DebugLogs
import timber.log.Timber

/**
 * Simulates receiving push notifications with hardcoded payloads.
 *
 * Call [simulateIncomingPush] to trigger a notification, or [startAutoSimulation]
 * to cycle through all hardcoded payloads with random delays.
 */
class MockPushService(private val context: Context) {

    private val scope = CoroutineScope(Dispatchers.IO)

    /** Whether auto-simulation is currently running. */
    @Volatile
    var isSimulating: Boolean = false
        private set

    private var autoJob: kotlinx.coroutines.Job? = null

    /**
     * Simulates receiving a single hardcoded push notification.
     * Returns the popped [PushEvent], or null if the payload list is exhausted
     * (the list cycles around).
     */
    fun simulateIncomingPush(): PushEvent {
        val payloads = PushEvent.HARDCODED_PAYLOADS
        val event = payloads.random()
        return receivePush(event)
    }

    /**
     * Simulates receiving a specific [PushEvent].
     */
    fun simulatePush(event: PushEvent): PushEvent {
        return receivePush(event)
    }

    /**
     * Starts auto-simulation: cycles through all hardcoded payloads with random delays
     * between pushes. Stops after [count] notifications (default: all payloads once).
     */
    fun startAutoSimulation(count: Int = PushEvent.HARDCODED_PAYLOADS.size) {
        if (isSimulating) return
        isSimulating = true

        autoJob = scope.launch {
            val payloads = PushEvent.HARDCODED_PAYLOADS
            for (i in 0 until count) {
                if (!isSimulating) break
                val event = payloads[i % payloads.size]
                receivePush(event)
                val delayMs = (3000L..8000L).random()
                delay(delayMs)
            }
            isSimulating = false
            Timber.d("MockPushService: auto-simulation finished")
        }
    }

    /**
     * Stops any running auto-simulation.
     */
    fun stopAutoSimulation() {
        isSimulating = false
        autoJob?.cancel()
        autoJob = null
    }

    private fun receivePush(event: PushEvent): PushEvent {
        // 1. Store in the DebugMenu event store
        DebugPushEvents.addEvent(event)

        // 2. Post as an Android system notification
        PushNotificationManager.postNotification(context, event)

        // 3. Log the event
        DebugLogs.log("MockPush", "Received: ${event.title} — ${event.body}")

        Timber.d("MockPushService: received push '%s': %s", event.title, event.body)
        return event
    }
}
