
package com.abdownloadmanager.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.abdownloadmanager.mobile.components.DownloadTabs
import com.abdownloadmanager.mobile.components.MobileBottomBar
import com.abdownloadmanager.mobile.components.MobileTopAppBar
import com.abdownloadmanager.mobile.settings.MobileUISettings
import com.abdownloadmanager.shared.domain.DownloadSystem
import com.abdownloadmanager.shared.domain.IDownloadMonitor
import com.abdownloadmanager.shared.ui.components.TopBarAction
import com.abdownloadmanager.shared.ui.theme.MyApplicationTheme
import ir.amirab.downloader.monitor.*
import kotlinx.coroutines.*
import org.koin.androidx.compose.koinViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File

class MainActivity : ComponentActivity(), KoinComponent {
    private val downloadSystem: DownloadSystem by inject()
    private val downloadMonitor: IDownloadMonitor by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val uiSettings = koinViewModel<MobileUISettings>()
            val isDarkTheme by uiSettings.isDarkTheme.collectAsState()

            MyApplicationTheme(darkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(
                        downloadSystem = downloadSystem,
                        downloadMonitor = downloadMonitor,
                        uiSettings = uiSettings
                    )
                }
            }
        }
    }
}

@Composable
fun MainScreen(
    downloadSystem: DownloadSystem, 
    downloadMonitor: IDownloadMonitor, 
    uiSettings: MobileUISettings
) {
    var showAddDownloadDialog by remember { mutableStateOf(false) }
    var showSettingsScreen by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    
    val downloadItems by downloadMonitor.monitoredItems.collectAsState(emptyList())
    var selectedTabIndex by remember { mutableStateOf(0) }
    
    val showTitle by uiSettings.showTitle.collectAsState()
    val showTabs by uiSettings.showTabs.collectAsState()
    val actionsAtBottom by uiSettings.actionsAtBottom.collectAsState()
    
    // Calculate tab counts
    val allCount = downloadItems.size
    val downloadingCount = downloadItems.count { it.state == DownloadState.DOWNLOADING }
    val finishedCount = downloadItems.count { it.state == DownloadState.FINISHED }
    val errorCount = downloadItems.count { it.state == DownloadState.FAILED }
    
    // Get current tab name
    val currentTabName = when (selectedTabIndex) {
        0 -> "All"
        1 -> "DOWNLOADING"
        2 -> "FINISHED"
        3 -> "ERROR"
        else -> "All"
    }
    
    // Build top bar actions
    val topBarActions = if (!actionsAtBottom) {
        uiSettings.getConfiguredTopBarActions(
            onExitClick = { finishAffinity() },
            onSortClick = { /* Implement sort */ },
            onQrClick = { /* Implement QR code functionality */ },
            onSearchClick = { /* Implement search */ },
            onBrowserClick = { /* Implement browser */ },
            onMagnetClick = { /* Implement magnet functionality */ },
            onMoreClick = { showMenu = true }
        )
    } else {
        listOf(
            TopBarAction(
                icon = Icons.Default.MoreVert,
                description = "More",
                onClick = { showMenu = true }
            )
        )
    }
    
    // Build bottom bar actions
    val bottomBarActions = uiSettings.getBottomBarActions(
        onExitClick = { finishAffinity() },
        onSortClick = { /* Implement sort */ },
        onQrClick = { /* Implement QR code functionality */ },
        onSearchClick = { /* Implement search */ },
        onBrowserClick = { /* Implement browser */ },
        onMagnetClick = { /* Implement magnet functionality */ }
    )

    Scaffold(
        topBar = {
            Column {
                MobileTopAppBar(
                    title = "AB DM",
                    actions = topBarActions,
                    onNavigationClick = { /* Implement drawer open */ },
                    showTitle = showTitle,
                    currentTab = currentTabName,
                    showTabs = showTabs,
                    bottomActionsEnabled = actionsAtBottom
                )
                
                // Only show tabs if they're enabled in settings
                if (showTabs) {
                    DownloadTabs(
                        selectedTabIndex = selectedTabIndex,
                        onTabSelected = { selectedTabIndex = it },
                        allCount = allCount,
                        downloadingCount = downloadingCount,
                        finishedCount = finishedCount,
                        errorCount = errorCount
                    )
                }
            }

            // Dropdown menu for more options
            if (showMenu) {
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                ) {
                    DropdownMenuItem(
                        text = { Text("Settings") },
                        onClick = {
                            showSettingsScreen = true
                            showMenu = false
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Settings, "Settings")
                        }
                    )
                    // Add more menu items as needed
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDownloadDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.CloudDownload, contentDescription = "Add Download")
            }
        },
        bottomBar = {
            if (actionsAtBottom && bottomBarActions.isNotEmpty()) {
                MobileBottomBar(actions = bottomBarActions)
            }
        }
    ) { padding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            color = MaterialTheme.colorScheme.background
        ) {
            DownloadList(downloadMonitor, uiSettings)
        }

        if (showAddDownloadDialog) {
            AddDownloadDialog(
                downloadSystem = downloadSystem, 
                scope = scope, 
                showDialog = remember { mutableStateOf(showAddDownloadDialog) }
            )
        }
        
        if (showSettingsScreen) {
            SettingsScreen(
                settings = uiSettings,
                onBackClick = { showSettingsScreen = false }
            )
        }
    }
}

@Composable
fun AddDownloadDialog(downloadSystem: DownloadSystem, scope: CoroutineScope, showDialog: MutableState<Boolean>) {
    var url by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = { showDialog.value = false },
        title = { Text("Add Download") },
        text = {
            OutlinedTextField(
                value = url,
                onValueChange = { url = it },
                label = { Text("URL") },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    val trimmedUrl = url.trim()
                    if (trimmedUrl.isNotBlank()) {
                        scope.launch {
                            try {
                                downloadSystem.downloadManager.createDownloadJob(trimmedUrl)
                                showDialog.value = false
                            } catch (e: Exception) {
                                // Handle error appropriately
                            }
                        }
                    }
                }
            ) {
                Text("Download")
            }
        },
        dismissButton = {
            TextButton(onClick = { showDialog.value = false }) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun DownloadList(downloadMonitor: IDownloadMonitor, uiSettings: MobileUISettings) {
    val downloadItems by downloadMonitor.monitoredItems.collectAsState(emptyList())
    val isCompactLayout by uiSettings.isCompactLayout.collectAsState()
    val showFileExtensions by uiSettings.showFileExtensions.collectAsState()
    
    // Display download list
    // Implementation will depend on specific UI requirements
    LazyColumn {
        items(downloadItems.size) { index ->
            val item = downloadItems[index]
            DownloadItem(
                downloadItem = item, 
                isCompact = isCompactLayout,
                showExtension = showFileExtensions
            )
        }
    }
}

@Composable
fun DownloadItem(downloadItem: DownloadItem, isCompact: Boolean, showExtension: Boolean) {
    // Implement download item UI
    // This is a simplified version
    val filename = if (showExtension) {
        downloadItem.filename
    } else {
        downloadItem.filename.substringBeforeLast(".", downloadItem.filename)
    }
    
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(
                if (isCompact) 8.dp else 16.dp
            )
        ) {
            Text(
                text = filename,
                style = MaterialTheme.typography.titleMedium
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            LinearProgressIndicator(
                progress = { downloadItem.progress.toFloat() / 100f },
                modifier = Modifier.fillMaxWidth(),
                color = when (downloadItem.state) {
                    DownloadState.DOWNLOADING -> MaterialTheme.colorScheme.primary
                    DownloadState.PAUSED -> MaterialTheme.colorScheme.tertiary
                    DownloadState.FINISHED -> MaterialTheme.colorScheme.secondary
                    else -> MaterialTheme.colorScheme.error
                }
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${downloadItem.downloadedBytes}/${downloadItem.totalBytes}",
                    style = MaterialTheme.typography.bodySmall
                )
                
                Text(
                    text = "${downloadItem.speed} | ${downloadItem.eta}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
