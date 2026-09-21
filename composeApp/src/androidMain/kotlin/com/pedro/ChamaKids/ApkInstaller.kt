package com.pedro.ChamaKids

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

actual object ApkInstaller {
    private var context: Context? = null

    fun setContext(ctx: Context) {
        context = ctx
    }

    actual fun install(apkData: ByteArray) {
        val ctx = context ?: return
        try {
            val apkFile = File(ctx.externalCacheDir, "update.apk")
            if (apkFile.exists()) apkFile.delete()
            
            FileOutputStream(apkFile).use { output ->
                output.write(apkData)
            }

            val uri = FileProvider.getUriForFile(
                ctx, 
                "${ctx.packageName}.fileprovider", 
                apkFile
            )

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/vnd.android.package-archive")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            
            ctx.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
