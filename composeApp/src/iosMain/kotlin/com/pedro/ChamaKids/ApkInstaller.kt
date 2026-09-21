package com.pedro.ChamaKids

actual object ApkInstaller {
    actual fun install(apkData: ByteArray) { }
    actual fun downloadAndInstall(url: String) { }
    actual fun getDownloadProgress(): Float = -1f
    actual fun setOnCompleteCallback(callback: () -> Unit) { }
}
