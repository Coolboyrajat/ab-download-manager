
package com.abdownloadmanager.huawei.di

import com.abdownloadmanager.huawei.download.HuaweiDownloadSystem
import com.abdownloadmanager.huawei.platform.HuaweiPlatformProvider
import com.abdownloadmanager.mobile.platform.MobilePlatformProvider
import ir.amirab.downloader.core.api.DownloadSystem
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 * Dependency injection module for Huawei implementation
 */
val huaweiAppModule = module {
    // Provide Huawei-specific download system implementation
    single<DownloadSystem> { HuaweiDownloadSystem.getDownloadSystem(androidContext()) }

    // Provide Huawei platform provider
    single<MobilePlatformProvider> { HuaweiPlatformProvider(androidContext()) }
}
