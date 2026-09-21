package com.pedro.ChamaKids

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.core.content.FileProvider
import java.io.File

actual object ApkInstaller {
    private var context: Context? = null
    private var onDownloadCompleteCallback: (() -> Unit)? = null
    private var lastDownloadId: Long = -1

    fun setContext(ctx: Context) {
        context = ctx
    }

    actual fun install(apkData: ByteArray) { }

    actual fun downloadAndInstall(url: String) {
        val ctx = context ?: return
        
        val oldFile = File(ctx.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "chamakids_update.apk")
        if (oldFile.exists()) oldFile.delete()

        val request = DownloadManager.Request(Uri.parse(url))
            .setTitle("Atualização ChamaKids")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalFilesDir(ctx, Environment.DIRECTORY_DOWNLOADS, "chamakids_update.apk")

        val downloadManager = ctx.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        lastDownloadId = downloadManager.enqueue(request)

        val onComplete = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                if (id == lastDownloadId) {
                    onDownloadCompleteCallback?.invoke()
                    
                    val apkFile = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "chamakids_update.apk")
                    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", apkFile)
                    
                    val installIntent = Intent(Intent.ACTION_VIEW).apply {
                        setDataAndType(uri, "application/vnd.android.package-archive")
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(installIntent)
                    context.unregisterReceiver(this)
                }
            }
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ctx.registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE), Context.RECEIVER_EXPORTED)
        } else {
            ctx.registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        }
    }

    actual fun setOnCompleteCallback(callback: () -> Unit) {
        onDownloadCompleteCallback = callback
    }

    actual fun getDownloadProgress(): Float {
        val ctx = context ?: return -1f
        if (lastDownloadId == -1L) return -1f
        
        val downloadManager = ctx.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val query = DownloadManager.Query().setFilterById(lastDownloadId)
        val cursor = downloadManager.query(query)
        if (cursor.moveToFirst()) {
            val bytesDownloaded = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
            val bytesTotal = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
            cursor.close()
            if (bytesTotal > 0) return bytesDownloaded.toFloat() / bytesTotal
        }
        cursor.close()
        return -1f
    }
}
