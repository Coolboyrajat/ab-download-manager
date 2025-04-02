
package com.abdownloadmanager.mobile.di

import com.abdownloadmanager.mobile.settings.MobileUISettings
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Dependency injection module for mobile platforms
 */
val mobileCommonModule = module {
    // Provide shared mobile UI settings
    single { MobileUISettings() }
}
