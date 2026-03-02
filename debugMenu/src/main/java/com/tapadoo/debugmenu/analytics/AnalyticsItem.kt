package com.tapadoo.debugmenu.analytics

/** Public display model for analytics items shown in the debug menu */
data class AnalyticsItem(
    val name: String,
    val params: Map<String, String> = emptyMap(),
    val timestampMs: Long = System.currentTimeMillis(),
)
