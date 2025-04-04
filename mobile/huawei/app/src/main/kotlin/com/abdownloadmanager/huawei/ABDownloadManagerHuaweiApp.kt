
package com.abdownloadmanager.huawei

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.abdownloadmanager.mobile.di.mobileCommonModule
import com.abdownloadmanager.huawei.di.huaweiAppModule
import com.abdownloadmanager.shared.app.di.appModule
import com.abdownloadmanager.shared.utils.di.utilsModule
import ir.amirab.downloader.core.di.downloaderCoreModule
import ir.amirab.downloader.monitor.di.downloaderMonitorModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class ABDownloadManagerHuaweiApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize Koin DI
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@ABDownloadManagerHuaweiApp)
            modules(
                listOf(
                    mobileCommonModule,
                    huaweiAppModule,
                    appModule,
                    utilsModule,
                    downloaderCoreModule,
                    downloaderMonitorModule
                )
            )
        }
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        // Enable MultiDex for large applications
        MultiDex.install(this)
    }
}
