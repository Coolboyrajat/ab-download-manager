
package com.abdownloadmanager.mobile.platform

/**
 * Mobile platform types supported by the application
 */
enum class MobilePlatform {
    ANDROID_GMS, // Google Mobile Services
    ANDROID_HMS, // Huawei Mobile Services
    IOS          // Apple iOS
}

/**
 * Interface for platform-specific functionality
 */
interface MobilePlatformProvider {
    /**
     * Get the current platform
     */
    fun getCurrentPlatform(): MobilePlatform

    /**
     * Check if HMS services are available
     */
    fun isHmsAvailable(): Boolean

    /**
     * Check if GMS services are available
     */
    fun isGmsAvailable(): Boolean
}
