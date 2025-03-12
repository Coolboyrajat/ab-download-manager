
package com.abdownloadmanager.shared.remote

import kotlinx.serialization.Serializable

@Serializable
sealed class RemoteCommand {
    @Serializable
    data class AddDownload(
        val url: String,
        val filename: String? = null,
        val directory: String? = null
    ) : RemoteCommand()
    
    @Serializable
    data class PauseDownload(val downloadId: String) : RemoteCommand()
    
    @Serializable
    data class ResumeDownload(val downloadId: String) : RemoteCommand()
    
    @Serializable
    data class CancelDownload(val downloadId: String) : RemoteCommand()
    
    @Serializable
    data object GetDownloadsList : RemoteCommand()
    
    @Serializable
    data class DownloadStatus(
        val id: String,
        val name: String,
        val progress: Float,
        val speed: Long,
        val status: String
    ) : RemoteCommand()
}
