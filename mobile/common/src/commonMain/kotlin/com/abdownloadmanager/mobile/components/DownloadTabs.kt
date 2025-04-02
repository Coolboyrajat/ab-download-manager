
package com.abdownloadmanager.mobile.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Tab row component for download categories
 * 
 * @param selectedTabIndex Currently selected tab index
 * @param onTabSelected Callback when a tab is selected
 * @param allCount Count of all downloads
 * @param downloadingCount Count of downloading items
 * @param finishedCount Count of finished downloads
 * @param errorCount Count of error downloads
 */
@Composable
fun DownloadTabs(
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    allCount: Int,
    downloadingCount: Int,
    finishedCount: Int,
    errorCount: Int
) {
    val tabs = listOf(
        "All" to allCount,
        "DOWNLOADING" to downloadingCount,
        "FINISHED" to finishedCount,
        "ERROR" to errorCount
    )
    
    TabRow(
        selectedTabIndex = selectedTabIndex,
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
    ) {
        tabs.forEachIndexed { index, (title, count) ->
            Tab(
                selected = selectedTabIndex == index,
                onClick = { onTabSelected(index) },
                text = {
                    Row {
                        Text(title)
                        if (count > 0) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Surface(
                                shape = MaterialTheme.shapes.small,
                                color = if (selectedTabIndex == index) 
                                    MaterialTheme.colorScheme.primary
                                else 
                                    MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f)
                            ) {
                                Text(
                                    text = count.toString(),
                                    modifier = Modifier.padding(horizontal = 4.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                }
            )
        }
    }
}
