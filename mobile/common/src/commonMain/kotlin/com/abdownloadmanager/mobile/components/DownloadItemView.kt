
package com.abdownloadmanager.mobile.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ir.amirab.downloader.core.api.DownloadItem
import com.abdownloadmanager.shared.utils.ui.icon.MyIcons

/**
 * Shared DownloadItem view component for all mobile platforms
 */
@Composable
fun DownloadItemView(
    downloadItem: DownloadItem,
    isCompact: Boolean = false,
    showExtension: Boolean = true,
    onItemClick: () -> Unit = {},
    onPauseClick: () -> Unit = {},
    onResumeClick: () -> Unit = {},
    onCancelClick: () -> Unit = {}
) {
    val isDownloading = downloadItem.state == DownloadItem.State.DOWNLOADING

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                // File name and extension
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = downloadItem.fileName + (if (showExtension) downloadItem.fileExtension else ""),
                        style = MaterialTheme.typography.titleMedium
                    )

                    if (!isCompact) {
                        Text(
                            text = downloadItem.url,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Action buttons
                Row(
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isDownloading) {
                        IconButton(onClick = onPauseClick) {
                            Icon(
                                imageVector = MyIcons.pause,
                                contentDescription = "Pause Download"
                            )
                        }
                    } else {
                        IconButton(onClick = onResumeClick) {
                            Icon(
                                imageVector = MyIcons.resume,
                                contentDescription = "Resume Download"
                            )
                        }
                    }

                    IconButton(onClick = onCancelClick) {
                        Icon(
                            imageVector = MyIcons.stop,
                            contentDescription = "Cancel Download"
                        )
                    }
                }
            }

            // Progress indication
            if (!isCompact) {
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { downloadItem.progress.toFloat() / 100f },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "${downloadItem.downloadedBytes}/${downloadItem.totalBytes} bytes",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "${downloadItem.speedInKBps} KB/s",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}
