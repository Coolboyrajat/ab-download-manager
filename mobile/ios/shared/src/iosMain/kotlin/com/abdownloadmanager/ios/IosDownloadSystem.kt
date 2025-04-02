
package com.abdownloadmanager.ios

import com.abdownloadmanager.shared.domain.DownloadSystem
import com.abdownloadmanager.shared.domain.IDownloadMonitor
import ir.amirab.downloader.core.DownloadManager
import ir.amirab.downloader.monitor.DefaultDownloadMonitor
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSUserDomainMask

/**
 * iOS-specific implementation of DownloadSystem
 */
object IosDownloadSystem {
    private var _downloadSystem: DownloadSystem? = null
    
    /**
     * Get the download system, creating it if needed
     */
    fun getDownloadSystem(): DownloadSystem {
        if (_downloadSystem == null) {
            val downloadDir = getDownloadDirectory()
            val downloadManager = DownloadManager(downloadDir)
            val downloadMonitor = DefaultDownloadMonitor(downloadManager)
            _downloadSystem = DownloadSystem(downloadManager, downloadMonitor)
        }
        
        return _downloadSystem!!
    }
    
    /**
     * Get the iOS-specific download directory
     */
    private fun getDownloadDirectory(): String {
        val paths = NSSearchPathForDirectoriesInDomains(
            NSDocumentDirectory,
            NSUserDomainMask,
            true
        )
        
        val documentsDirectory = paths.firstOrNull() as? String ?: ""
        val downloadDirectory = "$documentsDirectory/downloads"
        
        // Create the directory if it doesn't exist
        val fileManager = NSFileManager.defaultManager
        if (!fileManager.fileExistsAtPath(downloadDirectory)) {
            fileManager.createDirectoryAtPath(
                downloadDirectory,
                true,
                null,
                null
            )
        }
        
        return downloadDirectory
    }
}
