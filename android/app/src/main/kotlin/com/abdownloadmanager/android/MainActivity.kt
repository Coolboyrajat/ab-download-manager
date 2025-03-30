package com.abdownloadmanager.android

import android.os.Bundle
import android.os.Environment
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.abdownloadmanager.android.settings.UserInterfaceSettings
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
            val uiSettings = koinViewModel<UserInterfaceSettings>()
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
fun MainScreen(downloadSystem: DownloadSystem, downloadMonitor: IDownloadMonitor, uiSettings: UserInterfaceSettings) {
    var showAddDownloadDialog by remember { mutableStateOf(false) }
    var showSettingsScreen by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            CustomTopAppBar(
                title = "AB DM",
                actions = uiSettings.getConfiguredTopBarActions(
                    onExitClick = { /* finishAffinity()  -  Needs to be handled appropriately within the context of MainScreen*/ },
                    onSortClick = { /* Implement sort */ },
                    onQrClick = { /* Implement QR code functionality */ },
                    onSearchClick = { /* Implement search */ },
                    onBrowserClick = { /* Implement browser */ }
                ),
                onNavigationClick = { /* Implement drawer open */ },
                onMoreClick = { showMenu = true }
            )

            // Dropdown menu for more options
            if (showMenu) {
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier.background(MaterialTheme.colorScheme.surface) //Using MaterialTheme for consistent color
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
        }
    ) { padding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            color = MaterialTheme.colorScheme.background //Using MaterialTheme for consistent color
        ) {
            DownloadList(downloadMonitor, uiSettings)
        }

        if (showAddDownloadDialog) {
            AddDownloadDialog(downloadSystem = downloadSystem, scope = scope, showDialog = remember { mutableStateOf(showAddDownloadDialog) })
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