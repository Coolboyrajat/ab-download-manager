
package com.abdownloadmanager.android.di

import android.content.Context
import com.abdownloadmanager.shared.domain.DownloadSystem
import com.abdownloadmanager.shared.domain.IDownloadMonitor
import ir.amirab.downloader.core.DownloadManager
import ir.amirab.downloader.monitor.DefaultDownloadMonitor
import org.koin.core.module.Module
import org.koin.dsl.module
import java.io.File

/**
 * Android-specific DI module
 */
val androidModule = module {
    // Provide download system
    single { createDownloadSystem(get()) }
    
    // Provide download monitor
    single<IDownloadMonitor> { get<DownloadSystem>().downloadMonitor }
}

/**
 * Create Android-specific download system
 */
private fun createDownloadSystem(context: Context): DownloadSystem {
    val downloadDir = File(context.getExternalFilesDir(null), "downloads")
    if (!downloadDir.exists()) {
        downloadDir.mkdirs()
    }
    
    val downloadManager = DownloadManager(downloadDir.absolutePath)
    val downloadMonitor = DefaultDownloadMonitor(downloadManager)
    
    return DownloadSystem(downloadManager, downloadMonitor)
}
