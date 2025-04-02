
package com.abdownloadmanager.mobile.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.abdownloadmanager.shared.ui.components.TopBarAction

/**
 * Customizable TopBar for mobile platforms with tab integration
 * 
 * @param title Title to display in the center of the TopBar
 * @param actions List of actions to display in the TopBar
 * @param onNavigationClick Callback when the navigation icon is clicked
 * @param showTitle Whether to show the title or not
 * @param currentTab Current selected tab (displayed instead of title when title is hidden)
 * @param showTabs Whether tabs are shown in the UI
 * @param bottomActionsEnabled Whether actions can be moved to bottom bar
 */
@Composable
fun MobileTopAppBar(
    title: String,
    actions: List<TopBarAction>,
    onNavigationClick: () -> Unit,
    showTitle: Boolean = true,
    currentTab: String? = null,
    showTabs: Boolean = true,
    bottomActionsEnabled: Boolean = false
) {
    val displayTitle = if (showTitle) {
        title
    } else if (!showTabs && currentTab != null) {
        currentTab
    } else {
        ""
    }

    SmallTopAppBar(
        title = {
            Text(
                text = displayTitle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            IconButton(onClick = onNavigationClick) {
                Icon(Icons.Default.Menu, contentDescription = "Menu")
            }
        },
        actions = {
            actions.forEach { action ->
                IconButton(onClick = action.onClick) {
                    Icon(action.icon, contentDescription = action.description)
                }
            }
        },
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    )
}

/**
 * Bottom bar component that can display actions moved from top bar
 * 
 * @param actions List of actions to display in the bottom bar
 */
@Composable
fun MobileBottomBar(actions: List<TopBarAction>) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.primaryContainer,
        tonalElevation = 3.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            actions.forEach { action ->
                IconButton(onClick = action.onClick) {
                    Icon(
                        action.icon,
                        contentDescription = action.description,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}
