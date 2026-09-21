package com.pedro.ChamaKids

expect object ApkInstaller {
    fun install(apkData: ByteArray)
    fun downloadAndInstall(url: String)
    fun getDownloadProgress(): Float
    fun setOnCompleteCallback(callback: () -> Unit)
}
