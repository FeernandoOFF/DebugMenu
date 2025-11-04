package com.tapadoo.debugmenu.logs

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.tapadoo.debugmenu.module.DebugMenuModule

class LoggingModule: DebugMenuModule {
    override val title: String = "Logs"

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    override fun Content() {
        var invertSort by remember { mutableStateOf(false) }

        val logs by remember {
            derivedStateOf {
                if (invertSort) {
                    DebugLogs.events.toList().sortedBy { it.timestampMs }
                } else {
                    DebugLogs.events.toList().sortedByDescending { it.timestampMs }
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceContainer),
            contentPadding = PaddingValues(top = 0.dp, bottom = 12.dp, start = 0.dp, end = 0.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            stickyHeader {
                Row(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(4.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton({ DebugLogs.clear() }) {
                        Icon(
                            Icons.Outlined.Delete, null
                        )
                    }
                    IconButton({ invertSort = !invertSort }) {
                        Icon(
                            Icons.AutoMirrored.Filled.List, null
                        )
                    }
                }
            }
            items(logs) { item ->
                Column(
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                        .padding(12.dp)
                ) {
                    Row {
                        if (item.tag != null) {
                            Text(
                                text = "[${item.tag}] ",
                                maxLines = 1,
                                modifier = Modifier.basicMarquee(),
                                style = MaterialTheme.typography.titleMedium.copy(fontFamily = FontFamily.Monospace),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Text(
                            text = getPriorityLabel(item.priority),
                            maxLines = 1,
                            style = MaterialTheme.typography.titleMedium.copy(fontFamily = FontFamily.Monospace),
                            color = getPriorityColor(item.priority)
                        )
                    }
                    Text(
                        text = item.message,
                        style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (item.throwable != null) {
                        Text(
                            text = item.throwable.stackTraceToString(),
                            style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun getPriorityColor(priority: Int) = when (priority) {
        2 -> MaterialTheme.colorScheme.secondary // VERBOSE
        3 -> MaterialTheme.colorScheme.onSurfaceVariant // DEBUG
        4 -> MaterialTheme.colorScheme.primary // INFO
        5 -> MaterialTheme.colorScheme.tertiary // WARN
        6 -> MaterialTheme.colorScheme.error // ERROR
        7 -> MaterialTheme.colorScheme.error // ASSERT
        else -> MaterialTheme.colorScheme.onSurface
    }

    private fun getPriorityLabel(priority: Int) = when (priority) {
        2 -> "VERBOSE"
        3 -> "DEBUG"
        4 -> "INFO"
        5 -> "WARN"
        6 -> "ERROR"
        7 -> "ASSERT"
        else -> "UNKNOWN"
    }
}