
package com.abdownloadmanager.android.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.abdownloadmanager.android.settings.UserInterfaceSettings
import ir.amirab.downloader.downloaditem.DownloadStatus
import ir.amirab.downloader.monitor.IDownloadMonitor
import ir.amirab.downloader.monitor.IDownloadItemState
import kotlinx.coroutines.flow.map

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
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@Composable
fun DownloadList(
    downloadMonitor: IDownloadMonitor,
    settings: UserInterfaceSettings
) {
    var selectedTab by remember { mutableStateOf(0) }
    var downloads by remember { mutableStateOf(downloadMonitor.getAllDownloads()) }
    
    LaunchedEffect(Unit) {
        // Subscribe to download updates
        downloadMonitor.onListChanged()
            .onEach { downloads = downloadMonitor.getAllDownloads() }
            .launchIn(this)
    }
    
    Column(modifier = Modifier.fillMaxSize()) {
        // Tabs for filtering downloads
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color(0xFF212121),
            contentColor = Color(0xFF9C27B0),
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    height = 3.dp,
                    color = Color(0xFF9C27B0)
                )
            }
        ) {
            val tabs = listOf(
                "All" to getDownloadCountByStatus(downloads, null),
                "Downloading" to getDownloadCountByStatus(downloads, DownloadStatus.Downloading),
                "Finished" to getDownloadCountByStatus(downloads, DownloadStatus.Completed),
                "Error" to getDownloadCountByStatus(downloads, DownloadStatus.Error)
            )
            
            tabs.forEachIndexed { index, (title, count) ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(title)
                            Badge(
                                containerColor = if (selectedTab == index) Color(0xFF9C27B0) else Color.Gray
                            ) {
                                Text(
                                    text = count.toString(),
                                    color = Color.White
                                )
                            }
                        }
                    },
                    selectedContentColor = Color.White,
                    unselectedContentColor = Color.Gray
                )
            }
        }
        
        // Filter downloads based on selected tab
        val filteredDownloads = when (selectedTab) {
            0 -> downloads
            1 -> downloads.filter { it.downloadItem.status == DownloadStatus.Downloading }
            2 -> downloads.filter { it.downloadItem.status == DownloadStatus.Completed }
            3 -> downloads.filter { it.downloadItem.status == DownloadStatus.Error }
            else -> downloads
        }
        
        if (filteredDownloads.isEmpty()) {
            // Empty state
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No downloads found",
                    color = Color.Gray
                )
            }
        } else {
            // Display downloads
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF121212)),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(
                    items = filteredDownloads,
                    key = { it.downloadItem.id }
                ) { downloadInfo ->
                    DownloadItemCard(
                        downloadInfo = downloadInfo,
                        settings = settings,
                        onPlayPauseClick = { id ->
                            // Toggle download state (pause/resume)
                            val download = downloadInfo.downloadItem
                            if (download.status == DownloadStatus.Downloading) {
                                downloadMonitor.pauseDownload(download.id)
                            } else if (download.status == DownloadStatus.Paused || 
                                       download.status == DownloadStatus.Added) {
                                downloadMonitor.resumeDownload(download.id)
                            }
                        },
                        onMoreClick = { /* Show more options menu */ }
                    )
                }
            }
        }
    }
}

private fun getDownloadCountByStatus(downloads: List<ir.amirab.downloader.monitor.DownloadInfo>, status: DownloadStatus?): Int {
    return if (status == null) {
        downloads.size
    } else {
        downloads.count { it.downloadItem.status == status }
    }
}
