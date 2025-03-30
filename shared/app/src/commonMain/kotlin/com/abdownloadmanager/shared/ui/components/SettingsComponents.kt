
package com.abdownloadmanager.shared.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun SettingsSwitchItem(
    title: String,
    description: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title)
            if (description != null) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
fun SettingsScreen(
    darkThemeEnabled: Boolean,
    onDarkThemeChange: (Boolean) -> Unit,
    compactLayoutEnabled: Boolean,
    onCompactLayoutChange: (Boolean) -> Unit,
    showFileExtensions: Boolean,
    onShowFileExtensionsChange: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        SettingsSectionHeader(title = "Display Settings")
        
        SettingsSwitchItem(
            title = "Dark Theme",
            description = "Enable dark theme for the application",
            checked = darkThemeEnabled,
            onCheckedChange = onDarkThemeChange
        )
        
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        
        SettingsSwitchItem(
            title = "Compact Layout",
            description = "Show more items in the download list",
            checked = compactLayoutEnabled,
            onCheckedChange = onCompactLayoutChange
        )
        
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        
        SettingsSwitchItem(
            title = "Show File Extensions",
            description = "Display file extensions in the download list",
            checked = showFileExtensions,
            onCheckedChange = onShowFileExtensionsChange
        )
    }
}
