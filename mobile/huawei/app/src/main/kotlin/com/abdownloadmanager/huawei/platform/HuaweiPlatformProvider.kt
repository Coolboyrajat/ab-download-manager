
package com.abdownloadmanager.huawei.platform

import android.content.Context
import com.abdownloadmanager.mobile.platform.MobilePlatform
import com.abdownloadmanager.mobile.platform.MobilePlatformProvider
import com.huawei.hms.api.HuaweiApiAvailability

class HuaweiPlatformProvider(private val context: Context) : MobilePlatformProvider {

    override fun getCurrentPlatform(): MobilePlatform {
        return if (isHmsAvailable()) {
            MobilePlatform.ANDROID_HMS
        } else {
            // Even if HMS not available on this device, we're still in Huawei build
            MobilePlatform.ANDROID_HMS
        }
    }

    override fun isHmsAvailable(): Boolean {
        return try {
            val hmsApiAvailability = HuaweiApiAvailability.getInstance()
            val result = hmsApiAvailability.isHuaweiMobileServicesAvailable(context)
            result == com.huawei.hms.api.ConnectionResult.SUCCESS
        } catch (e: Exception) {
            false
        }
    }

    override fun isGmsAvailable(): Boolean {
        // Always return false in Huawei implementation
        return false
    }
}
