package com.pedro.ChamaKids

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

actual object DeviceIdentifier {
    private var context: Context? = null

    fun setContext(ctx: Context) {
        context = ctx
    }

    @SuppressLint("HardwareIds")
    actual fun getUniqueId(): String {
        val ctx = context ?: return "unknown_device"
        return Settings.Secure.getString(ctx.contentResolver, Settings.Secure.ANDROID_ID) ?: "unknown_device"
    }

    actual fun closeApp() {
        (context as? android.app.Activity)?.finishAffinity()
        System.exit(0)
    }
}
