
package com.abdownloadmanager.shared.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ir.amirab.downloader.downloaditem.DownloadStatus
import ir.amirab.downloader.monitor.*
import com.abdownloadmanager.shared.utils.bytesToReadableStr
import com.abdownloadmanager.shared.utils.formatETA

@Composable
fun DownloadItemRow(
    downloadState: IDownloadItemState,
    onPause: (String) -> Unit,
    onResume: (String) -> Unit,
    onCancel: (String) -> Unit,
    onItemClick: (String) -> Unit
) {
    val downloadId = downloadState.id
    val fileName = downloadState.info.fileName
    val status = downloadState.statusOrFinished()
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = fileName,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                
                Row {
                    when (status) {
                        DownloadStatus.DOWNLOADING -> {
                            IconButton(onClick = { onPause(downloadId) }) {
                                Icon(Icons.Default.Pause, contentDescription = "Pause")
                            }
                        }
                        DownloadStatus.PAUSED -> {
                            IconButton(onClick = { onResume(downloadId) }) {
                                Icon(Icons.Default.PlayArrow, contentDescription = "Resume")
                            }
                        }
                        else -> {}
                    }
                    
                    IconButton(onClick = { onCancel(downloadId) }) {
                        Icon(Icons.Default.Close, contentDescription = "Cancel")
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            when (downloadState) {
                is ProcessingDownloadItemState -> {
                    val progress = downloadState.progress
                    DownloadProgressInfo(
                        progress = progress,
                        downloadSpeed = downloadState.downloadSpeed,
                        eta = downloadState.etaSeconds
                    )
                }
                is CompletedDownloadItemState -> {
                    DownloadCompleteInfo(
                        totalBytes = downloadState.info.size,
                        status = status
                    )
                }
                else -> {
                    Text(
                        text = "Status: ${status.name}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
fun DownloadProgressInfo(
    progress: Float,
    downloadSpeed: Long,
    eta: Long
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Speed: ${bytesToReadableStr(downloadSpeed)}/s",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "ETA: ${formatETA(eta)}",
                style = MaterialTheme.typography.bodySmall
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth()
        )
        
        Text(
            text = "${(progress * 100).toInt()}%",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.align(Alignment.End)
        )
    }
}

@Composable
fun DownloadCompleteInfo(
    totalBytes: Long,
    status: DownloadStatus
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Size: ${bytesToReadableStr(totalBytes)}",
            style = MaterialTheme.typography.bodySmall
        )
        
        val statusText = when (status) {
            DownloadStatus.COMPLETED -> "Completed"
            DownloadStatus.FAILED -> "Failed"
            DownloadStatus.CANCELED -> "Canceled"
            else -> status.name
        }
        
        Text(
            text = "Status: $statusText",
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun DownloadListTabRow(
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    tabs: List<String>
) {
    TabRow(
        selectedTabIndex = selectedTabIndex,
        modifier = Modifier.fillMaxWidth()
    ) {
        tabs.forEachIndexed { index, title ->
            Tab(
                selected = selectedTabIndex == index,
                onClick = { onTabSelected(index) },
                text = { Text(title) }
            )
        }
    }
}
