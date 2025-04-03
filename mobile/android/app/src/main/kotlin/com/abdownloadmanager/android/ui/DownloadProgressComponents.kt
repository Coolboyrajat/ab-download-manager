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
import com.abdownloadmanager.android.settings.UserInterfaceSettings
import ir.amirab.downloader.downloaditem.DownloadStatus
import kotlin.math.pow
import kotlin.math.round

/**
 * Calculates the progress percentage based on downloaded and total bytes
 */
private fun calculateProgress(downloadedBytes: Long, totalBytes: Long): Float {
    if (totalBytes <= 0) return 0f
    return (downloadedBytes.toFloat() / totalBytes).coerceIn(0f, 1f)
}

/**
 * Formats the progress value according to the specified format
 */
private fun formatProgressValue(progress: Float, format: DownloadProgressFormat): String {
    return when (format) {
        DownloadProgressFormat.INTEGER -> (progress * 100).toInt().toString()
        DownloadProgressFormat.ONE_DECIMAL -> String.format("%.1f", progress * 100)
        DownloadProgressFormat.TWO_DECIMAL -> String.format("%.2f", progress * 100)
    }
}

/**
 * Calculates the progress for an individual segment in the sequential indicator
 */
private fun calculateSegmentProgress(progress: Float, segmentIndex: Int, totalSegments: Int): Float {
    val segmentWidth = 1f / totalSegments
    val segmentStart = segmentIndex * segmentWidth
    val segmentEnd = segmentStart + segmentWidth

    return when {
        progress <= segmentStart -> 0f
        progress >= segmentEnd -> 1f
        else -> (progress - segmentStart) / segmentWidth
    }
}

/**
 * Formats the download speed into a human-readable string
 */
private fun formatSpeed(speedBytesPerSecond: Float): String {
    val units = arrayOf("B/s", "KB/s", "MB/s", "GB/s")
    var value = speedBytesPerSecond
    var unitIndex = 0

    while (value > 1024 && unitIndex < units.size - 1) {
        value /= 1024
        unitIndex++
    }

    return if (unitIndex == 0) {
        "${value.toInt()} ${units[unitIndex]}"
    } else {
        String.format("%.2f %s", value, units[unitIndex])
    }
}

/**
 * Displays a completed download indicator
 */
@Composable
fun CompletedDownloadIndicator(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        LinearProgressIndicator(
            progress = { 1f },
            modifier = Modifier.fillMaxWidth(),
            color = Color.Green,
            trackColor = Color(0xFF424242),
            strokeCap = StrokeCap.Round
        )

        Text(
            text = "100%",
            color = Color.White,
            fontSize = 12.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

/**
 * Displays a sequential download progress indicator with multiple segments
 */
@Composable
fun SequentialDownloadIndicator(
    progress: Float,
    formattedProgress: String,
    modifier: Modifier = Modifier
) {
    val segments = 8

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

/**
 * Displays a standard download progress indicator
 */
@Composable
fun StandardDownloadIndicator(
    progress: Float,
    formattedProgress: String,
    status: DownloadStatus,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth(),
            color = when (status) {
                is DownloadStatus.Error -> Color.Red
                is DownloadStatus.Paused -> Color(0xFFFFAA00) // Amber
                is DownloadStatus.Completed -> Color.Green
                else -> Color(0xFF9C27B0) // Purple
            },
            trackColor = Color(0xFF424242),
            strokeCap = StrokeCap.Round
        )

        Text(
            text = "$formattedProgress%",
            color = Color.White,
            fontSize = 12.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

/**
 * Displays download progress status text
 */
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

/**
 * Main component for displaying download progress with various indicators and information
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