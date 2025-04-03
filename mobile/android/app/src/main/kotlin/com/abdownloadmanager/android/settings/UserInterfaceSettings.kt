package com.abdownloadmanager.android.settings

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.abdownloadmanager.shared.ui.components.TopBarAction

/**
 * Settings class to store user interface preferences
 */
class UserInterfaceSettings {
    // TopAppBar configurations
    var showExitButton by mutableStateOf(true)
    var showSortButton by mutableStateOf(true)
    var showQrButton by mutableStateOf(true)
    var showSearchButton by mutableStateOf(true)
    var showBrowserButton by mutableStateOf(true)
    var showMagnetButton by mutableStateOf(true)

    // Download progress display configurations
    enum class ProgressPrecision {
        INTEGER,         // e.g., 10%
        SINGLE_DECIMAL,  // e.g., 10.2%
        DOUBLE_DECIMAL   // e.g., 10.82%
    }

    var progressPrecision by mutableStateOf(ProgressPrecision.INTEGER)
    var useSequentialDownloadPattern by mutableStateOf(false)
    var useBarProgressStyle by mutableStateOf(true)

    /**
     * Formats the progress percentage according to user settings
     */
    fun formatProgress(progress: Float): String {
        return when (progressPrecision) {
            ProgressPrecision.INTEGER -> "${progress.toInt()}%"
            ProgressPrecision.SINGLE_DECIMAL -> "%.1f%%".format(progress)
            ProgressPrecision.DOUBLE_DECIMAL -> "%.2f%%".format(progress)
        }
    }

    /**
     * Get the list of configured top bar actions based on user preferences
     */
    fun getConfiguredTopBarActions(
        onExitClick: () -> Unit = {},
        onSortClick: () -> Unit = {},
        onQrClick: () -> Unit = {},
        onSearchClick: () -> Unit = {},
        onBrowserClick: () -> Unit = {},
        onMagnetClick: () -> Unit = {},
        onMoreClick: () -> Unit = {}
    ): List<TopBarAction> {
        val actions = mutableListOf<TopBarAction>()

        if (showExitButton) {
            actions.add(TopBarAction(
                icon = Icons.Default.ExitToApp,
                description = "Exit",
                onClick = onExitClick
            ))
        }

        if (showSortButton) {
            actions.add(TopBarAction(
                icon = Icons.Default.Sort,
                description = "Sort",
                onClick = onSortClick
            ))
        }

        if (showQrButton) {
            actions.add(TopBarAction(
                icon = Icons.Default.QrCode,
                description = "QR Code",
                onClick = onQrClick
            ))
        }

        if (showSearchButton) {
            actions.add(TopBarAction(
                icon = Icons.Default.Search,
                description = "Search",
                onClick = onSearchClick
            ))
        }

        if (showBrowserButton) {
            actions.add(TopBarAction(
                icon = Icons.Default.Public,
                description = "Browser",
                onClick = onBrowserClick
            ))
        }

        if (showMagnetButton) {
            actions.add(TopBarAction(
                icon = Icons.Default.Link,
                description = "Magnet",
                onClick = onMagnetClick
            ))
        }

        // Add more menu option
        actions.add(TopBarAction(
            icon = Icons.Default.MoreVert,
            description = "More",
            onClick = onMoreClick
        ))

        return actions
    }

    /**
     * Get the list of bottom bar actions if bottom action bar is enabled
     */
    fun getBottomBarActions(
        onExitClick: () -> Unit = {},
        onSortClick: () -> Unit = {},
        onQrClick: () -> Unit = {},
        onSearchClick: () -> Unit = {},
        onBrowserClick: () -> Unit = {},
        onMagnetClick: () -> Unit = {}
    ): List<TopBarAction> {
        val actions = mutableListOf<TopBarAction>()

        if (showExitButton) {
            actions.add(TopBarAction(
                icon = Icons.Default.ExitToApp,
                description = "Exit",
                onClick = onExitClick
            ))
        }

        if (showSortButton) {
            actions.add(TopBarAction(
                icon = Icons.Default.Sort,
                description = "Sort",
                onClick = onSortClick
            ))
        }

        if (showQrButton) {
            actions.add(TopBarAction(
                icon = Icons.Default.QrCode,
                description = "QR Code",
                onClick = onQrClick
            ))
        }

        if (showSearchButton) {
            actions.add(TopBarAction(
                icon = Icons.Default.Search,
                description = "Search",
                onClick = onSearchClick
            ))
        }

        if (showBrowserButton) {
            actions.add(TopBarAction(
                icon = Icons.Default.Public,
                description = "Browser",
                onClick = onBrowserClick
            ))
        }

        if (showMagnetButton) {
            actions.add(TopBarAction(
                icon = Icons.Default.Link,
                description = "Magnet",
                onClick = onMagnetClick
            ))
        }

        return actions
    }
}

/**
 * Enum representing the different types of TopBarAction
 */
object TopBarAction {
    // Special action types that are not represented directly as buttons
    val HAMBURGER = Type("HAMBURGER")
    val TITLE = Type("TITLE")
    val MORE_MENU = Type("MORE_MENU")

    // Configurable action types
    val EXIT = Type("EXIT")
    val SORT = Type("SORT")
    val QR = Type("QR")
    val SEARCH = Type("SEARCH")
    val BROWSER = Type("BROWSER")

    data class Type(val id: String)
}

/**
 * Enum for download progress display format
 */
enum class DownloadProgressFormat {
    INTEGER,
    SINGLE_DECIMAL,
    DOUBLE_DECIMAL
}