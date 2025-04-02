
package com.abdownloadmanager.android.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.abdownloadmanager.android.settings.UserInterfaceSettings
import com.abdownloadmanager.shared.domain.DownloadSystem
import com.abdownloadmanager.shared.domain.IDownloadMonitor
import com.abdownloadmanager.shared.ui.components.*
import ir.amirab.downloader.downloaditem.DownloadStatus
import ir.amirab.downloader.monitor.statusOrFinished
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    downloadSystem: DownloadSystem,
    downloadMonitor: IDownloadMonitor,
    uiSettings: UserInterfaceSettings
) {
    val coroutineScope = rememberCoroutineScope()
    val downloadItems by downloadMonitor.monitoredItems.collectAsState(emptyList())
    
    var isSettingsOpen by remember { mutableStateOf(false) }
    var selectedTabIndex by remember { mutableStateOf(0) }
    
    val compactLayout by uiSettings.isCompactLayout.collectAsState()
    val showFileExtensions by uiSettings.showFileExtensions.collectAsState()
    val isDarkTheme by uiSettings.isDarkTheme.collectAsState()
    
    val topBarActions = listOf(
        TopBarAction(
            icon = Icons.Default.Settings,
            description = "Settings",
            onClick = { isSettingsOpen = true }
        ),
        TopBarAction(
            icon = Icons.Default.Add,
            description = "Add Download",
            onClick = { /* Add download functionality */ }
        )
    )
    
    Scaffold(
        topBar = {
            CommonTopAppBar(
                title = "AB Download Manager",
                actions = topBarActions,
                onNavigationClick = { /* Open drawer if needed */ }
            )
        }
    ) { paddingValues ->
        if (isSettingsOpen) {
            SettingsScreen(
                darkThemeEnabled = isDarkTheme,
                onDarkThemeChange = { 
                    coroutineScope.launch {
                        uiSettings.setDarkTheme(it)
                    }
                },
                compactLayoutEnabled = compactLayout,
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
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                val tabs = listOf("All", "Active", "Completed", "Failed")
                
                DownloadListTabRow(
                    selectedTabIndex = selectedTabIndex,
                    onTabSelected = { selectedTabIndex = it },
                    tabs = tabs
                )
                
                val filteredItems = when (selectedTabIndex) {
                    1 -> downloadItems.filter { it.statusOrFinished() == DownloadStatus.DOWNLOADING || it.statusOrFinished() == DownloadStatus.PAUSED }
                    2 -> downloadItems.filter { it.statusOrFinished() == DownloadStatus.COMPLETED }
                    3 -> downloadItems.filter { it.statusOrFinished() == DownloadStatus.FAILED || it.statusOrFinished() == DownloadStatus.CANCELED }
                    else -> downloadItems
                }
                
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(filteredItems) { downloadState ->
                        DownloadItemRow(
                            downloadState = downloadState,
                            onPause = { 
                                coroutineScope.launch {
                                    downloadSystem.pauseDownload(it)
                                }
                            },
                            onResume = { 
                                coroutineScope.launch {
                                    downloadSystem.resumeDownload(it)
                                }
                            },
                            onCancel = { 
                                coroutineScope.launch {
                                    downloadSystem.cancelDownload(it)
                                }
                            },
                            onItemClick = { /* Item click functionality */ }
                        )
                    }
                }
            }
        }
    }
}
