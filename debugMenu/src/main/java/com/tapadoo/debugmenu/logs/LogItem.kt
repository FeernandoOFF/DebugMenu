package com.tapadoo.debugmenu.logs

data class LogItem(
    val priority: Int,
    val tag: String?,
    val message: String,
    val throwable: Throwable?,
    val timestampMs: Long = System.currentTimeMillis()
)
