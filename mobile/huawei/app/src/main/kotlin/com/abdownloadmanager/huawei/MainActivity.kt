package com.abdownloadmanager.huawei

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.abdownloadmanager.huawei.download.HuaweiDownloadSystem
import com.abdownloadmanager.mobile.components.DownloadTabs
import com.abdownloadmanager.mobile.components.MobileBottomBar
import com.abdownloadmanager.mobile.components.MobileTopAppBar
import com.abdownloadmanager.mobile.components.TopBarAction
import com.abdownloadmanager.mobile.settings.MobileUISettings
import com.abdownloadmanager.shared.utils.ui.icon.MyIcons
import ir.amirab.downloader.monitor.api.IDownloadMonitor
import ir.amirab.downloader.monitor.collectCounts
import org.koin.androidx.compose.get

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MainView()
        }
    }
}

@Composable
fun MainView() {
    val downloadSystem = remember { HuaweiDownloadSystem.getDownloadSystem(get()) }
    val downloadMonitor = remember { downloadSystem.queue.createMonitor() }

    // Initialize UI settings
    val uiSettings = get<MobileUISettings>()
    val isDarkTheme by uiSettings.isDarkTheme.collectAsState()
    val showTitle by uiSettings.showTitle.collectAsState()
    val showTabs by uiSettings.showTabs.collectAsState()
    val actionsAtBottom by uiSettings.actionsAtBottom.collectAsState()

    // Tab state
    var selectedTabIndex by remember { mutableStateOf(0) }
    val currentTabName = when (selectedTabIndex) {
        0 -> "All"
        1 -> "Downloading"
        2 -> "Finished"
        3 -> "Error"
        else -> "All"
    }

    // Download counts
    val counts by downloadMonitor.collectCounts().collectAsState(initial = emptyMap())
    val allCount = counts["all"] ?: 0
    val downloadingCount = counts["downloading"] ?: 0
    val finishedCount = counts["completed"] ?: 0
    val errorCount = counts["error"] ?: 0

    // Dialog states
    var showAddDownloadDialog by remember { mutableStateOf(false) }
    var showSettingsScreen by remember { mutableStateOf(false) }

    // Actions
    val topBarActions = listOf(
        TopBarAction(
            icon = MyIcons.settings,
            description = "Settings",
            onClick = { showSettingsScreen = true }
        )
    )

    val bottomBarActions = if (!actionsAtBottom) emptyList() else listOf(
        TopBarAction(
            icon = MyIcons.settings,
            description = "Settings",
            onClick = { showSettingsScreen = true }
        )
    )

    // UI
    com.abdownloadmanager.mobile.components.theme.MyApplicationTheme(darkTheme = isDarkTheme) {
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
                        Icon(MyIcons.plus, contentDescription = "Add Download")
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
                    DownloadList(downloadMonitor, uiSettings)
                }

                // TODO: Implement settings screen and add download dialog
            }
        }
    }
}

@Composable
fun DownloadList(downloadMonitor: IDownloadMonitor, uiSettings: MobileUISettings) {
    // TODO: Implement download list similar to Android implementation
}
