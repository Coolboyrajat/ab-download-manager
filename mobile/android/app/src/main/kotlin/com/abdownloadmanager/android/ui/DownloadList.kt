package com.abdownloadmanager.android.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.abdownloadmanager.android.settings.UserInterfaceSettings
import ir.amirab.downloader.downloaditem.DownloadStatus
import ir.amirab.downloader.monitor.IDownloadMonitor
import ir.amirab.downloader.monitor.IDownloadItemState
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

enum class DownloadTab {
    ALL, DOWNLOADING, FINISHED, ERROR
}

@Composable
fun DownloadList(
    downloadMonitor: IDownloadMonitor,
    settings: UserInterfaceSettings
) {
    var selectedTab by remember { mutableStateOf(DownloadTab.ALL) }
    val downloadItems by downloadMonitor.getAllDownloadsFlow().collectAsState(emptyList())

    // Filter downloads based on selected tab
    val filteredItems = when (selectedTab) {
        DownloadTab.ALL -> downloadItems
        DownloadTab.DOWNLOADING -> downloadItems.filter { 
            it.status is DownloadStatus.Downloading 
        }
        DownloadTab.FINISHED -> downloadItems.filter { 
            it.status is DownloadStatus.Finished 
        }
        DownloadTab.ERROR -> downloadItems.filter { 
            it.status is DownloadStatus.Error 
        }
    }

    // Calculate tab counts
    val allCount = downloadItems.size
    val downloadingCount = downloadItems.count { it.status is DownloadStatus.Downloading }
    val finishedCount = downloadItems.count { it.status is DownloadStatus.Finished }
    val errorCount = downloadItems.count { it.status is DownloadStatus.Error }

    Column(modifier = Modifier.fillMaxSize()) {
        // Tab row
        TabRow(
            selectedTabIndex = selectedTab.ordinal,
            contentColor = MaterialTheme.colorScheme.primary,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab.ordinal]),
                    height = 3.dp,
                    color = MaterialTheme.colorScheme.primary
                )
            },
            divider = {}
        ) {
            Tab(
                selected = selectedTab == DownloadTab.ALL,
                onClick = { selectedTab = DownloadTab.ALL },
                text = { Text("ALL ($allCount)") }
            )

            Tab(
                selected = selectedTab == DownloadTab.DOWNLOADING,
                onClick = { selectedTab = DownloadTab.DOWNLOADING },
                text = { Text("DOWNLOADING ($downloadingCount)") }
            )

            Tab(
                selected = selectedTab == DownloadTab.FINISHED,
                onClick = { selectedTab = DownloadTab.FINISHED },
                text = { Text("FINISHED ($finishedCount)") }
            )

            Tab(
                selected = selectedTab == DownloadTab.ERROR,
                onClick = { selectedTab = DownloadTab.ERROR },
                text = { Text("ERROR ($errorCount)") }
            )
        }

        // Download items list
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF121212))
        ) {
            items(
                items = filteredItems,
                key = { it.id }
            ) { downloadItem ->
                DownloadItem(
                    downloadItem = downloadItem,
                    settings = settings,
                    onPlayPause = {
                        // Implement play/pause functionality
                        when (downloadItem.status) {
                            is DownloadStatus.Downloading -> downloadMonitor.pauseDownload(downloadItem.id)
                            is DownloadStatus.Paused -> downloadMonitor.resumeDownload(downloadItem.id)
                            is DownloadStatus.Error -> downloadMonitor.retryDownload(downloadItem.id)
                            else -> Unit // Handle other states
                        }
                    },
                    onMoreOptions = {
                        // Show more options menu
                    }
                )

                Divider(
                    color = Color(0xFF2A2A2A),
                    thickness = 1.dp
                )
            }
        }
    }
}

// Function for version compatibility with the mobile implementation
@Composable
fun getDownloadCountByStatus(downloads: List<ir.amirab.downloader.monitor.DownloadInfo>, status: DownloadStatus?): Int {
    return if (status == null) {
        downloads.size
    } else {
        downloads.count { it.downloadItem.status == status }
    }
}