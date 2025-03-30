
package com.abdownloadmanager.android.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import com.abdownloadmanager.android.TopBarAction

/**
 * Settings class to store user interface preferences
 */
class UserInterfaceSettings {
    // TopBar button settings
    var showExitButton by mutableStateOf(true)
    var showSortButton by mutableStateOf(true)
    var showQrButton by mutableStateOf(false)
    var showSearchButton by mutableStateOf(true)
    var showBrowserButton by mutableStateOf(true)
    
    // Download progress display settings
    enum class ProgressPrecision {
        INTEGER,         // e.g., 40%
        SINGLE_DECIMAL,  // e.g., 80.2%
        DOUBLE_DECIMAL   // e.g., 80.82%
    }
    
    var progressPrecision by mutableStateOf(ProgressPrecision.INTEGER)
    
    // Download pattern settings
    var useSequentialDownloadPattern by mutableStateOf(false)
    
    // Progress bar UI style
    var useBarProgressStyle by mutableStateOf(true)
    
    // Get configured action buttons in preferred order
    fun getConfiguredTopBarActions(onExitClick: () -> Unit = {}, 
                                  onSortClick: () -> Unit = {},
                                  onQrClick: () -> Unit = {}, 
                                  onSearchClick: () -> Unit = {},
                                  onBrowserClick: () -> Unit = {}): List<TopBarAction> {
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
        
        return actions
    }
    
    // Format progress according to user preference
    fun formatProgress(progress: Float): String {
        return when (progressPrecision) {
            ProgressPrecision.INTEGER -> "${progress.toInt()}%"
            ProgressPrecision.SINGLE_DECIMAL -> "%.1f%%".format(progress)
            ProgressPrecision.DOUBLE_DECIMAL -> "%.2f%%".format(progress)
        }
    }
}
package com.abdownloadmanager.android.settings

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import com.abdownloadmanager.android.TopBarAction

/**
 * Class to manage user interface preferences and settings
 */
class UserInterfaceSettings {
    // TopAppBar configurations
    var showExitButton by mutableStateOf(true)
    var showSortButton by mutableStateOf(true)
    var showQrButton by mutableStateOf(true)
    var showSearchButton by mutableStateOf(true)
    var showBrowserButton by mutableStateOf(true)
    
    // Download progress display configurations
    var downloadProgressFormat by mutableStateOf(DownloadProgressFormat.INTEGER)
    var useSequentialDownloadPattern by mutableStateOf(false)
    
    // Default set of actions to always display
    private val defaultActions = listOf(
        TopBarAction.HAMBURGER,
        TopBarAction.TITLE,
        TopBarAction.MORE_MENU
    )
    
    // User configurable actions
    private val configurableActions = listOf(
        TopBarAction.EXIT,
        TopBarAction.SORT,
        TopBarAction.QR,
        TopBarAction.SEARCH,
        TopBarAction.BROWSER
    )
    
    // Current order of actions (this could be persisted to storage)
    private var actionOrder = mutableListOf(
        TopBarAction.HAMBURGER,
        TopBarAction.TITLE,
        TopBarAction.EXIT,
        TopBarAction.SORT,
        TopBarAction.QR,
        TopBarAction.SEARCH,
        TopBarAction.BROWSER,
        TopBarAction.MORE_MENU
    )
    
    /**
     * Get the list of configured top bar actions based on user preferences
     */
    fun getConfiguredTopBarActions(
        onExitClick: () -> Unit,
        onSortClick: () -> Unit,
        onQrClick: () -> Unit,
        onSearchClick: () -> Unit,
        onBrowserClick: () -> Unit
    ): List<TopBarAction> {
        val actions = mutableListOf<TopBarAction>()
        
        for (action in actionOrder) {
            when (action) {
                TopBarAction.EXIT -> if (showExitButton) {
                    actions.add(TopBarAction(
                        icon = Icons.Default.ExitToApp,
                        description = "Exit",
                        onClick = onExitClick
                    ))
                }
                TopBarAction.SORT -> if (showSortButton) {
                    actions.add(TopBarAction(
                        icon = Icons.Default.Sort,
                        description = "Sort",
                        onClick = onSortClick
                    ))
                }
                TopBarAction.QR -> if (showQrButton) {
                    actions.add(TopBarAction(
                        icon = Icons.Default.QrCode,
                        description = "QR Code",
                        onClick = onQrClick
                    ))
                }
                TopBarAction.SEARCH -> if (showSearchButton) {
                    actions.add(TopBarAction(
                        icon = Icons.Default.Search,
                        description = "Search",
                        onClick = onSearchClick
                    ))
                }
                TopBarAction.BROWSER -> if (showBrowserButton) {
                    actions.add(TopBarAction(
                        icon = Icons.Default.Language,
                        description = "Browser",
                        onClick = onBrowserClick
                    ))
                }
                // Default actions are always included
                else -> { /* These are handled by the CustomTopAppBar component */ }
            }
        }
        
        return actions
    }
    
    /**
     * Reorder the TopBarAction items
     */
    fun reorderActions(newOrder: List<TopBarAction.Type>) {
        // Validate that the new order contains all required default actions
        if (defaultActions.all { it in newOrder }) {
            actionOrder.clear()
            actionOrder.addAll(newOrder)
        }
    }
    
    /**
     * Reset to default settings
     */
    fun resetToDefaults() {
        showExitButton = true
        showSortButton = true
        showQrButton = true
        showSearchButton = true
        showBrowserButton = true
        downloadProgressFormat = DownloadProgressFormat.INTEGER
        useSequentialDownloadPattern = false
        actionOrder.clear()
        actionOrder.addAll(listOf(
            TopBarAction.HAMBURGER,
            TopBarAction.TITLE,
            TopBarAction.EXIT,
            TopBarAction.SORT,
            TopBarAction.QR, 
            TopBarAction.SEARCH,
            TopBarAction.BROWSER,
            TopBarAction.MORE_MENU
        ))
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
    INTEGER, // 40%
    SINGLE_DECIMAL, // 80.2%
    DOUBLE_DECIMAL // 80.82%
}
