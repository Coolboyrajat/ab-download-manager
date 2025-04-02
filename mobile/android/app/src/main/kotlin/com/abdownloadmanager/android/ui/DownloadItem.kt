
package com.abdownloadmanager.android.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abdownloadmanager.android.settings.UserInterfaceSettings
import ir.amirab.downloader.downloaditem.DownloadItem
import ir.amirab.downloader.downloaditem.DownloadStatus
import ir.amirab.downloader.monitor.DownloadInfo
import ir.amirab.downloader.monitor.IDownloadItemState
import java.text.SimpleDateFormat
import java.util.*

/**
 * Legacy DownloadItem component 
 */
@Composable
fun DownloadItem(
    downloadItem: IDownloadItemState,
    settings: UserInterfaceSettings,
    onPlayPause: () -> Unit = {},
    onMoreOptions: () -> Unit = {}
) {
    val fileName = downloadItem.fileName ?: "Unknown file"
    val progress = downloadItem.progress ?: 0f
    val downloadedSize = downloadItem.downloadedLength?.let { formatSize(it) } ?: "0B"
    val totalSize = downloadItem.totalLength?.let { formatSize(it) } ?: "Unknown"
    val speed = downloadItem.speed?.let { formatSpeed(it) } ?: "0B/s"
    val remainingTime = downloadItem.eta?.let { formatTime(it) } ?: "--"
    val status = downloadItem.status

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // File type icon
        Icon(
            imageVector = Icons.Default.InsertDriveFile,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.width(12.dp))

        // File details
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = fileName,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Custom progress display based on settings
            DownloadProgress(progress, status, settings)

            Spacer(modifier = Modifier.height(4.dp))

            // File size and speed info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "$downloadedSize / $totalSize",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )

                if (status is DownloadStatus.Downloading) {
                    Text(
                        text = "$speed • $remainingTime",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                } else {
                    Text(
                        text = remainingTime,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Play/Pause/Retry button based on status
        IconButton(
            onClick = onPlayPause
        ) {
            when (status) {
                is DownloadStatus.Downloading -> Icon(Icons.Default.Pause, "Pause")
                is DownloadStatus.Paused -> Icon(Icons.Default.PlayArrow, "Resume") 
                is DownloadStatus.Error -> Icon(Icons.Default.Refresh, "Retry")
                is DownloadStatus.Finished -> Icon(Icons.Default.PlayArrow, "Open")
                else -> Icon(Icons.Default.PlayArrow, "Start")
            }
        }

        // More options button
        IconButton(
            onClick = onMoreOptions
        ) {
            Icon(Icons.Default.MoreVert, "More Options")
        }
    }
}

/**
 * Updated card style DownloadItem component
 */
@Composable
fun DownloadItemCard(
    downloadInfo: DownloadInfo,
    settings: UserInterfaceSettings,
    onPlayPauseClick: (String) -> Unit,
    onMoreClick: (String) -> Unit
) {
    val downloadItem = downloadInfo.downloadItem

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF212121)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // File name and controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FileTypeIcon(downloadItem.name)

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                ) {
                    Text(
                        text = downloadItem.name,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Control buttons
                Row {
                    PlayPauseButton(
                        downloadStatus = downloadItem.status,
                        onClick = { onPlayPauseClick(downloadItem.id.toString()) }
                    )

                    IconButton(
                        onClick = { onMoreClick(downloadItem.id.toString()) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More",
                            tint = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Download size info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatFileSize(downloadInfo.downloadedBytes) + " / " + formatFileSize(downloadInfo.totalBytes),
                    color = Color.LightGray,
                    fontSize = 14.sp
                )

                when (downloadItem.status) {
                    DownloadStatus.Completed -> {
                        Text(
                            text = "Completed",
                            color = Color(0xFF4CAF50),
                            fontSize = 14.sp
                        )
                    }
                    DownloadStatus.Paused -> {
                        Text(
                            text = "Paused",
                            color = Color(0xFFFFA000),
                            fontSize = 14.sp
                        )
                    }
                    DownloadStatus.Error -> {
                        Text(
                            text = "Error",
                            color = Color(0xFFF44336),
                            fontSize = 14.sp
                        )
                    }
                    else -> {
                        downloadInfo.estimatedTimeRemainingText?.let { timeText ->
                            Text(
                                text = timeText,
                                color = Color.LightGray,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Progress indicator
            DownloadProgress(
                downloadedBytes = downloadInfo.downloadedBytes,
                totalBytes = downloadInfo.totalBytes,
                status = downloadItem.status,
                progressFormat = settings.downloadProgressFormat,
                useSequentialPattern = settings.useSequentialDownloadPattern,
                downloadSpeed = downloadInfo.downloadSpeed,
                uploadSpeed = downloadInfo.uploadSpeed,
                timeRemaining = downloadInfo.estimatedTimeRemainingText
            )
        }
    }
}

@Composable
private fun FileTypeIcon(fileName: String) {
    val fileExtension = fileName.substringAfterLast('.', "")
    val icon = when (fileExtension.lowercase(Locale.ROOT)) {
        "mp4", "mkv", "avi", "mov" -> Icons.Default.VideoFile
        "mp3", "wav", "ogg", "flac" -> Icons.Default.AudioFile
        "jpg", "jpeg", "png", "gif" -> Icons.Default.Image
        "pdf" -> Icons.Default.PictureAsPdf
        "doc", "docx" -> Icons.Default.Description
        "xls", "xlsx" -> Icons.Default.TableChart
        "zip", "rar", "7z" -> Icons.Default.Archive
        "exe", "msi" -> Icons.Default.Apps
        else -> Icons.Default.InsertDriveFile
    }

    Icon(
        imageVector = icon,
        contentDescription = "File type",
        tint = Color.White,
        modifier = Modifier.size(24.dp)
    )
}

@Composable
private fun PlayPauseButton(
    downloadStatus: DownloadStatus,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(32.dp)
    ) {
        Icon(
            imageVector = when (downloadStatus) {
                DownloadStatus.Downloading -> Icons.Default.Pause
                DownloadStatus.Completed -> Icons.Default.CheckCircle
                DownloadStatus.Error -> Icons.Default.Error
                else -> Icons.Default.PlayArrow
            },
            contentDescription = when (downloadStatus) {
                DownloadStatus.Downloading -> "Pause"
                DownloadStatus.Completed -> "Completed"
                DownloadStatus.Error -> "Error"
                else -> "Resume"
            },
            tint = when (downloadStatus) {
                DownloadStatus.Downloading -> Color.White
                DownloadStatus.Completed -> Color(0xFF4CAF50)
                DownloadStatus.Error -> Color(0xFFF44336)
                else -> Color.White
            }
        )
    }
}

// Helper functions for formatting
private fun formatSize(bytes: Long): String {
    if (bytes < 1024) return "$bytes B"
    val kb = bytes / 1024.0
    if (kb < 1024) return "%.2f KB".format(kb)
    val mb = kb / 1024.0
    if (mb < 1024) return "%.2f MB".format(mb)
    val gb = mb / 1024.0
    return "%.2f GB".format(gb)
}

private fun formatSpeed(bytesPerSecond: Long): String {
    return formatSize(bytesPerSecond) + "/s"
}

private fun formatTime(seconds: Long): String {
    if (seconds < 60) return "${seconds}s"
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    if (minutes < 60) return "${minutes}m ${remainingSeconds}s"
    val hours = minutes / 60
    val remainingMinutes = minutes % 60
    return "${hours}h ${remainingMinutes}m"
}

private fun formatFileSize(bytes: Long): String {
    if (bytes <= 0) return "0 B"

    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val digitGroups = (Math.log10(bytes.toDouble()) / Math.log10(1024.0)).toInt()

    return "%.2f %s".format(
        bytes / Math.pow(1024.0, digitGroups.toDouble()),
        units[digitGroups]
    )
}
