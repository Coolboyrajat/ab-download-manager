
package com.abdownloadmanager.ios

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.abdownloadmanager.mobile.components.DownloadTabs
import com.abdownloadmanager.mobile.components.MobileBottomBar
import com.abdownloadmanager.mobile.components.MobileTopAppBar
import com.abdownloadmanager.mobile.settings.MobileUISettings
import com.abdownloadmanager.shared.ui.components.TopBarAction
import com.abdownloadmanager.shared.ui.theme.MyApplicationTheme
import ir.amirab.downloader.monitor.DownloadState
import org.koin.core.context.startKoin
import org.koin.dsl.module

@Composable
fun MainView() {
    val downloadSystem = remember { IosDownloadSystem.getDownloadSystem() }
    val downloadMonitor = remember { downloadSystem.downloadMonitor }

    // Initialize UI settings
    val uiSettings = remember { MobileUISettings() }
    val isDarkTheme by uiSettings.isDarkTheme.collectAsState()
    val showTitle by uiSettings.showTitle.collectAsState()
    val showTabs by uiSettings.showTabs.collectAsState()
    val actionsAtBottom by uiSettings.actionsAtBottom.collectAsState()

    val downloadItems by downloadMonitor.monitoredItems.collectAsState(emptyList())
    var selectedTabIndex by remember { mutableStateOf(0) }
    var showSettingsScreen by remember { mutableStateOf(false) }
    var showAddDownloadDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    
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
            onExitClick = { /* Not applicable on iOS */ },
            onSortClick = { /* Implement sort */ },
            onQrClick = { /* Implement QR code functionality */ },
            onSearchClick = { /* Implement search */ },
            onBrowserClick = { /* Implement browser */ },
            onMagnetClick = { /* Implement magnet functionality */ },
            onMoreClick = { /* Open menu */ }
        )
    } else {
        listOf(
            TopBarAction(
                icon = Icons.Default.MoreVert,
                description = "More",
                onClick = { /* Open menu */ }
            )
        )
    }
    
    // Build bottom bar actions
    val bottomBarActions = uiSettings.getBottomBarActions(
        onExitClick = { /* Not applicable on iOS */ },
        onSortClick = { /* Implement sort */ },
        onQrClick = { /* Implement QR code functionality */ },
        onSearchClick = { /* Implement search */ },
        onBrowserClick = { /* Implement browser */ },
        onMagnetClick = { /* Implement magnet functionality */ }
    )

    MyApplicationTheme(darkTheme = isDarkTheme) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
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
                // Main content
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    // Download list would go here
                    // Using similar implementation as Android
                }
                
                // Settings screen
                if (showSettingsScreen) {
                    // Custom implementation of settings for iOS
                }
                
                // Add download dialog
                if (showAddDownloadDialog) {
                    // Custom implementation of add download dialog for iOS
                }
            }
        }
    }
}

fun initKoin() {
    startKoin {
        modules(
            module {
                single { IosDownloadSystem.getDownloadSystem() }
                single { MobileUISettings() }
            }
        )
    }
}
