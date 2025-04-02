
package com.abdownloadmanager.android.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.abdownloadmanager.mobile.settings.MobileUISettings
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settings: MobileUISettings,
    onBackClick: () -> Unit
) {
    val isDarkTheme by settings.isDarkTheme.collectAsState()
    val isCompactLayout by settings.isCompactLayout.collectAsState()
    val showFileExtensions by settings.showFileExtensions.collectAsState()
    val showTitle by settings.showTitle.collectAsState()
    val showTabs by settings.showTabs.collectAsState()
    val actionsAtBottom by settings.actionsAtBottom.collectAsState()
    
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Appearance settings
            Text(
                text = "Appearance",
                style = MaterialTheme.typography.titleLarge
            )
            
            SwitchPreference(
                title = "Dark Theme",
                checked = isDarkTheme,
                onCheckedChange = { 
                    coroutineScope.launch {
                        settings.setDarkTheme(it)
                    }
                }
            )
            
            SwitchPreference(
                title = "Compact Layout",
                checked = isCompactLayout,
                onCheckedChange = { 
                    coroutineScope.launch {
                        settings.setCompactLayout(it)
                    }
                }
            )
            
            SwitchPreference(
                title = "Show File Extensions",
                checked = showFileExtensions,
                onCheckedChange = { 
                    coroutineScope.launch {
                        settings.setShowFileExtensions(it)
                    }
                }
            )
            
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            
            // TopBar settings
            Text(
                text = "Top Bar",
                style = MaterialTheme.typography.titleLarge
            )
            
            SwitchPreference(
                title = "Show Title",
                checked = showTitle,
                onCheckedChange = { 
                    coroutineScope.launch {
                        settings.setShowTitle(it)
                    }
                }
            )
            
            SwitchPreference(
                title = "Show Tabs",
                checked = showTabs,
                onCheckedChange = { 
                    coroutineScope.launch {
                        settings.setShowTabs(it)
                    }
                }
            )
            
            SwitchPreference(
                title = "Move Actions to Bottom",
                checked = actionsAtBottom,
                onCheckedChange = { 
                    coroutineScope.launch {
                        settings.setActionsAtBottom(it)
                    }
                }
            )
            
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            
            // Top Bar Buttons
            Text(
                text = "Top Bar Buttons",
                style = MaterialTheme.typography.titleLarge
            )
            
            SwitchPreference(
                title = "Show Exit Button",
                checked = settings.showExitButton,
                onCheckedChange = { settings.showExitButton = it }
            )
            
            SwitchPreference(
                title = "Show Sort Button",
                checked = settings.showSortButton,
                onCheckedChange = { settings.showSortButton = it }
            )
            
            SwitchPreference(
                title = "Show QR Button",
                checked = settings.showQrButton,
                onCheckedChange = { settings.showQrButton = it }
            )
            
            SwitchPreference(
                title = "Show Search Button",
                checked = settings.showSearchButton,
                onCheckedChange = { settings.showSearchButton = it }
            )
            
            SwitchPreference(
                title = "Show Browser Button",
                checked = settings.showBrowserButton,
                onCheckedChange = { settings.showBrowserButton = it }
            )
            
            SwitchPreference(
                title = "Show Magnet Button",
                checked = settings.showMagnetButton,
                onCheckedChange = { settings.showMagnetButton = it }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = { settings.resetToDefaults() },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Reset to Defaults")
            }
        }
    }
}

@Composable
fun SwitchPreference(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    description: String? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = title)
            if (description != null) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}
