package com.pedro.ChamaKids

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSFileManager

actual object FileUtils {
    @OptIn(ExperimentalForeignApi::class)
    actual fun excluirArquivo(caminho: String?) {
        if (caminho.isNullOrBlank()) return
        try {
            NSFileManager.defaultManager.removeItemAtPath(caminho, null)
        } catch (_: Exception) { }
    }
}
