package com.tapadoo.debugmenu

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.tapadoo.debugmenu.module.DebugMenuModule

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebugMenuOverlay(
    modules: List<DebugMenuModule> = emptyList(),
    modifier: Modifier = Modifier,
    showFab: Boolean = true,
    enableShake: Boolean = false,
    sheetState: SheetState = rememberModalBottomSheetState(),
    colorScheme: ColorScheme = lightColorScheme()
) {
    // No-op in release builds
}
