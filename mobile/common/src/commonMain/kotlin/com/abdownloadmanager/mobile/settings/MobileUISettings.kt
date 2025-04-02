
package com.abdownloadmanager.mobile.settings

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import com.abdownloadmanager.shared.ui.components.TopBarAction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Common mobile UI settings class
 */
class MobileUISettings {
    // UI Mode settings
    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()
    
    // Layout settings
    private val _isCompactLayout = MutableStateFlow(false)
    val isCompactLayout: StateFlow<Boolean> = _isCompactLayout.asStateFlow()
    
    private val _showFileExtensions = MutableStateFlow(true)
    val showFileExtensions: StateFlow<Boolean> = _showFileExtensions.asStateFlow()
    
    // TopBar settings
    private val _showTitle = MutableStateFlow(true)
    val showTitle: StateFlow<Boolean> = _showTitle.asStateFlow()
    
    private val _showTabs = MutableStateFlow(true)
    val showTabs: StateFlow<Boolean> = _showTabs.asStateFlow()
    
    // Action visibility settings
    var showExitButton by mutableStateOf(true)
    var showSortButton by mutableStateOf(true)
    var showQrButton by mutableStateOf(true)
    var showSearchButton by mutableStateOf(true)
    var showBrowserButton by mutableStateOf(true)
    var showMagnetButton by mutableStateOf(true) // New Magnet button option
    
    // Action position settings (top or bottom)
    private val _actionsAtBottom = MutableStateFlow(false)
    val actionsAtBottom: StateFlow<Boolean> = _actionsAtBottom.asStateFlow()
    
    // Download progress display configurations
    var downloadProgressFormat by mutableStateOf(DownloadProgressFormat.INTEGER)
    var useSequentialDownloadPattern by mutableStateOf(false)
    
    // Default set of actions
    private val defaultActions = listOf(
        TopBarActionType.HAMBURGER,
        TopBarActionType.TITLE
    )
    
    // User configurable actions
    private val configurableActions = listOf(
        TopBarActionType.EXIT,
        TopBarActionType.SORT,
        TopBarActionType.QR,
        TopBarActionType.SEARCH,
        TopBarActionType.BROWSER,
        TopBarActionType.MAGNET, // New Magnet action
        TopBarActionType.MORE_MENU
    )
    
    // Current order of actions
    private var actionOrder = mutableListOf(
        TopBarActionType.HAMBURGER,
        TopBarActionType.TITLE,
        TopBarActionType.EXIT,
        TopBarActionType.SORT,
        TopBarActionType.QR,
        TopBarActionType.SEARCH,
        TopBarActionType.BROWSER,
        TopBarActionType.MAGNET, // Added to default order
        TopBarActionType.MORE_MENU
    )
    
    /**
     * Get actions based on current settings
     */
    fun getConfiguredTopBarActions(
        onExitClick: () -> Unit,
        onSortClick: () -> Unit,
        onQrClick: () -> Unit,
        onSearchClick: () -> Unit,
        onBrowserClick: () -> Unit,
        onMagnetClick: () -> Unit,
        onMoreClick: () -> Unit
    ): List<TopBarAction> {
        val actions = mutableListOf<TopBarAction>()
        
        for (action in actionOrder) {
            when (action) {
                TopBarActionType.EXIT -> if (showExitButton) {
                    actions.add(TopBarAction(
                        icon = Icons.Default.ExitToApp,
                        description = "Exit",
                        onClick = onExitClick
                    ))
                }
                TopBarActionType.SORT -> if (showSortButton) {
                    actions.add(TopBarAction(
                        icon = Icons.Default.Sort,
                        description = "Sort",
                        onClick = onSortClick
                    ))
                }
                TopBarActionType.QR -> if (showQrButton) {
                    actions.add(TopBarAction(
                        icon = Icons.Default.QrCode,
                        description = "QR Code",
                        onClick = onQrClick
                    ))
                }
                TopBarActionType.SEARCH -> if (showSearchButton) {
                    actions.add(TopBarAction(
                        icon = Icons.Default.Search,
                        description = "Search",
                        onClick = onSearchClick
                    ))
                }
                TopBarActionType.BROWSER -> if (showBrowserButton) {
                    actions.add(TopBarAction(
                        icon = Icons.Default.Language,
                        description = "Browser",
                        onClick = onBrowserClick
                    ))
                }
                TopBarActionType.MAGNET -> if (showMagnetButton) {
                    actions.add(TopBarAction(
                        icon = Icons.Default.Link,
                        description = "Magnet",
                        onClick = onMagnetClick
                    ))
                }
                TopBarActionType.MORE_MENU -> {
                    actions.add(TopBarAction(
                        icon = Icons.Default.MoreVert,
                        description = "More",
                        onClick = onMoreClick
                    ))
                }
                else -> { /* Handled elsewhere */ }
            }
        }
        
        return actions
    }
    
    /**
     * Get actions for bottom bar if enabled
     */
    fun getBottomBarActions(
        onExitClick: () -> Unit,
        onSortClick: () -> Unit,
        onQrClick: () -> Unit,
        onSearchClick: () -> Unit,
        onBrowserClick: () -> Unit,
        onMagnetClick: () -> Unit
    ): List<TopBarAction> {
        if (!actionsAtBottom.value) return emptyList()
        
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
                icon = Icons.Default.Language,
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
    
    /**
     * Reorder actions
     */
    fun reorderActions(newOrder: List<TopBarActionType>) {
        // Validate that the new order contains required default actions
        if (defaultActions.all { it in newOrder }) {
            actionOrder.clear()
            actionOrder.addAll(newOrder)
        }
    }
    
    /**
     * Toggle dark theme
     */
    suspend fun setDarkTheme(enabled: Boolean) {
        _isDarkTheme.emit(enabled)
    }
    
    /**
     * Toggle compact layout
     */
    suspend fun setCompactLayout(enabled: Boolean) {
        _isCompactLayout.emit(enabled)
    }
    
    /**
     * Toggle file extensions
     */
    suspend fun setShowFileExtensions(enabled: Boolean) {
        _showFileExtensions.emit(enabled)
    }
    
    /**
     * Toggle title visibility
     */
    suspend fun setShowTitle(show: Boolean) {
        _showTitle.emit(show)
    }
    
    /**
     * Toggle tabs visibility
     */
    suspend fun setShowTabs(show: Boolean) {
        _showTabs.emit(show)
    }
    
    /**
     * Toggle actions position (top or bottom)
     */
    suspend fun setActionsAtBottom(atBottom: Boolean) {
        _actionsAtBottom.emit(atBottom)
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
        showMagnetButton = true
        
        actionOrder.clear()
        actionOrder.addAll(listOf(
            TopBarActionType.HAMBURGER,
            TopBarActionType.TITLE,
            TopBarActionType.EXIT,
            TopBarActionType.SORT,
            TopBarActionType.QR, 
            TopBarActionType.SEARCH,
            TopBarActionType.BROWSER,
            TopBarActionType.MAGNET,
            TopBarActionType.MORE_MENU
        ))
    }
}

/**
 * TopBar action types
 */
enum class TopBarActionType {
    HAMBURGER,
    TITLE,
    EXIT,
    SORT,
    QR,
    SEARCH,
    BROWSER,
    MAGNET,
    MORE_MENU
}

/**
 * Download progress format types
 */
enum class DownloadProgressFormat {
    INTEGER,       // 40%
    SINGLE_DECIMAL, // 80.2%
    DOUBLE_DECIMAL  // 80.82%
}
