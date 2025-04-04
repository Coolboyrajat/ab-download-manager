
package com.abdownloadmanager.shared.app.download

import ir.amirab.downloader.core.api.DownloadSystem

/**
 * Interface for providing platform-specific download system implementations
 */
interface DownloadSystemProvider {
    /**
     * Get the platform-specific download system
     */
    fun getDownloadSystem(): DownloadSystem
}
