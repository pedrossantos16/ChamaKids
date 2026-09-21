package com.pedro.ChamaKids

actual object ApkInstaller {
    actual fun install(apkData: ByteArray) {
        // iOS doesn't allow installing apps from APKs/IPAs programmatically this way
    }
}
