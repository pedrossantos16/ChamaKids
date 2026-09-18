package com.pedro.ChamaKids

import platform.UIKit.UIDevice

actual object DeviceIdentifier {
    actual fun getUniqueId(): String {
        return UIDevice.currentDevice.identifierForVendor?.UUIDString ?: "unknown_ios_device"
    }

    actual fun closeApp() {
        // iOS doesn't allow closing apps programmatically easily, but for security we can crash or exit
        // darwin.posix.exit(0) 
    }
}
