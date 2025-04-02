
package com.abdownloadmanager.android.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.abdownloadmanager.android.settings.UserInterfaceSettings
import ir.amirab.downloader.downloaditem.DownloadStatus

// Component for displaying download progress with configurable precision
@Composable
fun DownloadProgress(
    progress: Float,
    status: DownloadStatus,
    settings: UserInterfaceSettings
) {
    if (settings.useBarProgressStyle) {
        BarProgressDisplay(progress, status, settings)
    } else {
        if (status is DownloadStatus.Finished) {
            Text("Completed", color = Color.Green)
        } else if (settings.useSequentialDownloadPattern) {
            SequentialProgressDisplay(progress, status, settings)
        } else {
            StandardProgressDisplay(progress, status, settings)
        }
    }
}

// Standard progress bar display
@Composable
fun BarProgressDisplay(
    progress: Float,
    status: DownloadStatus,
    settings: UserInterfaceSettings
) {
    val progressColor = when (status) {
        is DownloadStatus.Error -> Color.Red
        is DownloadStatus.Paused -> Color(0xFFFFAA00) // Amber
        is DownloadStatus.Finished -> Color.Green
        else -> MaterialTheme.colorScheme.primary
    }
    
    val statusText = when (status) {
        is DownloadStatus.Error -> "Error"
        is DownloadStatus.Paused -> "Paused"
        is DownloadStatus.Finished -> "Completed"
        is DownloadStatus.Downloading -> "Downloading"
        else -> "Waiting"
    }
    
    // Display progress with user-selected precision when not finished
    val progressText = if (status is DownloadStatus.Finished) {
        "100%"
    } else {
        settings.formatProgress(progress * 100)
    }
    
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(progressText, color = progressColor)
            Text(statusText, color = progressColor)
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        LinearProgressIndicator(
            progress = { if (status is DownloadStatus.Finished) 1f else progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = progressColor,
            trackColor = Color.DarkGray
        )
    }
}

// Sequential download pattern display
@Composable
fun SequentialProgressDisplay(
    progress: Float,
    status: DownloadStatus,
    settings: UserInterfaceSettings
) {
    val segments = 8 // Number of segments to display
    val completedSegments = (progress * segments).toInt()
    val progressText = settings.formatProgress(progress * 100)
    
    Column {
        Text(progressText)
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            for (i in 0 until segments) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(24.dp)
                        .padding(horizontal = 1.dp)
                        .background(
                            if (i < completedSegments) Color.Blue
                            else Color.DarkGray
                        )
                )
            }
        }
    }
}

// Simple text display for non-bar UI style
@Composable
fun StandardProgressDisplay(
    progress: Float,
    status: DownloadStatus,
    settings: UserInterfaceSettings
) {
    val progressText = if (status is DownloadStatus.Finished) {
        "100%"
    } else {
        settings.formatProgress(progress * 100)
    }
    
    val statusText = when (status) {
        is DownloadStatus.Error -> "Error"
        is DownloadStatus.Paused -> "Paused"
        is DownloadStatus.Finished -> "Completed"
        is DownloadStatus.Downloading -> "Downloading"
        else -> "Waiting"
    }
    
    Text(
        text = "$progressText - $statusText",
        color = when (status) {
            is DownloadStatus.Error -> Color.Red
            is DownloadStatus.Paused -> Color(0xFFFFAA00) // Amber
            is DownloadStatus.Finished -> Color.Green
            else -> Color.White
        }
    )
}
package com.abdownloadmanager.android.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abdownloadmanager.android.settings.DownloadProgressFormat
import ir.amirab.downloader.downloaditem.DownloadStatus
import kotlin.math.pow
import kotlin.math.round

/**
 * Displays download progress according to user settings
 */
@Composable
fun DownloadProgress(
    downloadedBytes: Long,
    totalBytes: Long,
    status: DownloadStatus,
    progressFormat: DownloadProgressFormat,
    useSequentialPattern: Boolean,
    downloadSpeed: Float? = null,
    uploadSpeed: Float? = null,
    timeRemaining: String? = null,
    modifier: Modifier = Modifier
) {
    val progress = calculateProgress(downloadedBytes, totalBytes)
    val formattedProgress = formatProgressValue(progress, progressFormat)
    
    if (status == DownloadStatus.Completed) {
        CompletedDownloadIndicator(modifier)
    } else if (useSequentialPattern) {
        SequentialDownloadIndicator(progress, formattedProgress, modifier)
    } else {
        StandardDownloadIndicator(progress, formattedProgress, status, modifier)
    }
    
    // Display speed and time info if available
    if (downloadSpeed != null && status == DownloadStatus.Downloading) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "${formatSpeed(downloadSpeed)} ↓ ${if (uploadSpeed != null) "${formatSpeed(uploadSpeed)} ↑" else "0 B/s ↑"}",
                color = Color.White,
                fontSize = 12.sp
            )
            
            if (timeRemaining != null) {
                Text(
                    text = timeRemaining,
                    color = Color.White,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun StandardDownloadIndicator(
    progress: Float,
    formattedProgress: String,
    status: DownloadStatus,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxWidth()) {
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = when (status) {
                DownloadStatus.Downloading -> Color(0xFF9C27B0) // Purple
                DownloadStatus.Paused -> Color(0xFFFFA000) // Amber
                DownloadStatus.Error -> Color(0xFFF44336) // Red
                DownloadStatus.Completed -> Color(0xFF4CAF50) // Green
                else -> Color(0xFF2196F3) // Blue
            },
            trackColor = Color(0xFF424242),
            strokeCap = StrokeCap.Round
        )
        
        if (status != DownloadStatus.Completed) {
            Text(
                text = "$formattedProgress%",
                color = Color.White,
                fontSize = 12.sp,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(4.dp)
            )
        }
    }
}

@Composable
private fun SequentialDownloadIndicator(
    progress: Float,
    formattedProgress: String,
    modifier: Modifier = Modifier
) {
    val segments = 8
    val segmentWidth = 1f / segments
    
    Column(modifier = modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth()) {
            for (i in 0 until segments) {
                val segmentProgress = calculateSegmentProgress(progress, i, segments)
                
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(24.dp)
                        .padding(horizontal = 1.dp)
                        .background(
                            color = if (segmentProgress > 0f) Color(0xFF9C27B0).copy(alpha = segmentProgress) else Color(0xFF424242),
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp)
                        )
                )
            }
        }
        
        Text(
            text = "$formattedProgress%",
            color = Color.White,
            fontSize = 12.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
private fun CompletedDownloadIndicator(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxWidth()) {
        LinearProgressIndicator(
            progress = { 1f },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = Color(0xFF4CAF50), // Green
            trackColor = Color(0xFF424242),
            strokeCap = StrokeCap.Round
        )
        
        Text(
            text = "100%",
            color = Color.White,
            fontSize = 12.sp,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(4.dp)
        )
    }
}

/**
 * Calculate the progress as a float between 0 and 1
 */
private fun calculateProgress(downloaded: Long, total: Long): Float {
    return if (total <= 0) 0f else (downloaded.toFloat() / total).coerceIn(0f, 1f)
}

/**
 * Format the progress according to user preference
 */
private fun formatProgressValue(progress: Float, format: DownloadProgressFormat): String {
    val percentage = progress * 100
    return when (format) {
        DownloadProgressFormat.INTEGER -> percentage.toInt().toString()
        DownloadProgressFormat.SINGLE_DECIMAL -> {
            val rounded = round(percentage * 10) / 10
            "%.1f".format(rounded)
        }
        DownloadProgressFormat.DOUBLE_DECIMAL -> {
            val rounded = round(percentage * 100) / 100
            "%.2f".format(rounded)
        }
    }
}

/**
 * Calculate progress for a single segment in sequential download pattern
 */
private fun calculateSegmentProgress(overallProgress: Float, segmentIndex: Int, totalSegments: Int): Float {
    val segmentSize = 1f / totalSegments
    val segmentStart = segmentIndex * segmentSize
    val segmentEnd = segmentStart + segmentSize
    
    return when {
        overallProgress <= segmentStart -> 0f
        overallProgress >= segmentEnd -> 1f
        else -> (overallProgress - segmentStart) / segmentSize
    }
}

/**
 * Format bytes/second speed to human readable format
 */
private fun formatSpeed(bytesPerSecond: Float): String {
    val units = arrayOf("B/s", "KB/s", "MB/s", "GB/s")
    var speed = bytesPerSecond.toDouble()
    var unitIndex = 0
    
    while (speed > 1024 && unitIndex < units.size - 1) {
        speed /= 1024
        unitIndex++
    }
    
    return "%.1f %s".format(speed, units[unitIndex])
}
