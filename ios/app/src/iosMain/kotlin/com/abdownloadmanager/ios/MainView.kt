package com.abdownloadmanager.ios

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.abdownloadmanager.ios.di.IosDownloadSystem
import com.abdownloadmanager.shared.ui.components.*
import com.abdownloadmanager.shared.ui.theme.MyApplicationTheme
import com.abdownloadmanager.shared.utils.bytesToReadableStr
import com.abdownloadmanager.shared.utils.formatETA
import ir.amirab.downloader.downloaditem.DownloadStatus
import ir.amirab.downloader.monitor.CompletedDownloadItemState
import ir.amirab.downloader.monitor.IDownloadItemState
import ir.amirab.downloader.monitor.ProcessingDownloadItemState
import ir.amirab.downloader.monitor.statusOrFinished
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@Composable
fun MainView() {
    val downloadSystem = remember { IosDownloadSystem.getDownloadSystem() }
    val downloadMonitor = remember { downloadSystem.downloadMonitor }

    var isSettingsOpen by remember { mutableStateOf(false) }
    var selectedTabIndex by remember { mutableStateOf(0) }
    val coroutineScope = rememberCoroutineScope()

    val downloadItems by downloadMonitor.monitoredItems.collectAsState(emptyList())

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

    // UI Interface Settings
    var darkThemeEnabled by remember { mutableStateOf(false) }
    var compactLayoutEnabled by remember { mutableStateOf(false) }
    var showFileExtensions by remember { mutableStateOf(true) }

    MyApplicationTheme(darkTheme = darkThemeEnabled) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(
                topBar = {
                    CommonTopAppBar(
                        title = "AB Download Manager",
                        actions = topBarActions
                    )
                }
            ) { paddingValues ->
                if (isSettingsOpen) {
                    SettingsScreen(
                        darkThemeEnabled = darkThemeEnabled,
                        onDarkThemeChange = { darkThemeEnabled = it },
                        compactLayoutEnabled = compactLayoutEnabled,
                        onCompactLayoutChange = { compactLayoutEnabled = it },
                        showFileExtensions = showFileExtensions,
                        onShowFileExtensionsChange = { showFileExtensions = it }
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
    Column(modifier = Modifier.padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Dark Theme:")
            Spacer(modifier = Modifier.weight(1f))
            Switch(checked = darkThemeEnabled, onCheckedChange = onDarkThemeChange)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Compact Layout:")
            Spacer(modifier = Modifier.weight(1f))
            Switch(checked = compactLayoutEnabled, onCheckedChange = onCompactLayoutChange)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Show File Extensions:")
            Spacer(modifier = Modifier.weight(1f))
            Switch(checked = showFileExtensions, onCheckedChange = onShowFileExtensionsChange)
        }
    }
}

@Composable
fun AddDownloadDialog(
    onDismiss: () -> Unit,
    onAddDownload: (String) -> Unit
) {
    var url by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Download") },
        text = {
            Column {
                OutlinedTextField(
                    value = url,
                    onValueChange = { url = it },
                    label = { Text("URL") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onAddDownload(url) },
                enabled = url.isNotBlank()
            ) {
                Text("Download")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}