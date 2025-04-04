
package com.abdownloadmanager.huawei.download

import android.content.Context
import com.huawei.hmf.tasks.Task
import com.huawei.hms.common.ApiException
import ir.amirab.downloader.core.api.DownloadItem
import ir.amirab.downloader.core.api.DownloadQueue
import ir.amirab.downloader.core.api.DownloadSystem
import ir.amirab.downloader.core.api.DownloadSystemConfig
import ir.amirab.downloader.core.api.PersistenceManager
import ir.amirab.downloader.core.api.ProgressBroadcaster
import ir.amirab.downloader.core.internal.DefaultDownloadQueue
import ir.amirab.downloader.core.internal.DefaultDownloadSystem
import ir.amirab.downloader.core.internal.DefaultPersistenceManager
import ir.amirab.downloader.core.internal.DefaultProgressBroadcaster
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.coroutines.CoroutineContext

/**
 * Huawei-specific implementation of the DownloadSystem.
 * Uses HMS capabilities when available and falls back to default implementation when needed.
 */
class HuaweiDownloadSystem private constructor(
    private val context: Context,
    private val defaultSystem: DefaultDownloadSystem
) : DownloadSystem, CoroutineScope {

    override val coroutineContext: CoroutineContext = SupervisorJob() + Dispatchers.IO

    private val _isHmsAvailable = MutableStateFlow(false)
    val isHmsAvailable: Flow<Boolean> = _isHmsAvailable.asStateFlow()

    init {
        checkHmsAvailability(context)
    }

    private fun checkHmsAvailability(context: Context) {
        try {
            // Check if HMS Core is available
            val hmsApiAvailability = com.huawei.hms.api.HuaweiApiAvailability.getInstance()
            val result = hmsApiAvailability.isHuaweiMobileServicesAvailable(context)
            _isHmsAvailable.value = (result == com.huawei.hms.api.ConnectionResult.SUCCESS)
        } catch (e: Exception) {
            _isHmsAvailable.value = false
        }
    }

    override val queue: DownloadQueue
        get() = defaultSystem.queue

    override val progressBroadcaster: ProgressBroadcaster
        get() = defaultSystem.progressBroadcaster

    override val persistenceManager: PersistenceManager
        get() = defaultSystem.persistenceManager

    override fun config(): DownloadSystemConfig = defaultSystem.config()

    override fun addDownload(url: String, path: String): DownloadItem = defaultSystem.addDownload(url, path)

    companion object {
        @Volatile
        private var INSTANCE: HuaweiDownloadSystem? = null

        fun getDownloadSystem(context: Context): HuaweiDownloadSystem {
            return INSTANCE ?: synchronized(this) {
                val defaultSystem = DefaultDownloadSystem.getDownloadSystem(context)
                HuaweiDownloadSystem(context, defaultSystem).also {
                    INSTANCE = it
                }
            }
        }
    }
}
