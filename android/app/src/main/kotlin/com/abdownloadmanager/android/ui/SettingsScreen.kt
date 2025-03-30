package com.abdownloadmanager.android.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.abdownloadmanager.android.settings.UserInterfaceSettings
import com.abdownloadmanager.shared.ui.components.SettingsScreen
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun AndroidSettingsScreen() {
    val uiSettings = koinViewModel<UserInterfaceSettings>()
    val isDarkTheme by uiSettings.isDarkTheme.collectAsState()
    val isCompactLayout by uiSettings.isCompactLayout.collectAsState()
    val showFileExtensions by uiSettings.showFileExtensions.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    SettingsScreen(
        darkThemeEnabled = isDarkTheme,
        onDarkThemeChange = { 
            coroutineScope.launch {
                uiSettings.setDarkTheme(it)
            }
        },
        compactLayoutEnabled = isCompactLayout,
        onCompactLayoutChange = { 
            coroutineScope.launch {
                uiSettings.setCompactLayout(it)
            }
        },
        showFileExtensions = showFileExtensions,
        onShowFileExtensionsChange = { 
            coroutineScope.launch {
                uiSettings.setShowFileExtensions(it)
            }
        }
    )
}