package com.pedro.ChamaKids

import platform.Foundation.NSFileManager

actual object FileUtils {
    actual fun excluirArquivo(caminho: String?) {
        if (caminho.isNullOrBlank()) return
        try {
            NSFileManager.defaultManager.removeItemAtPath(caminho, null)
        } catch (_: Exception) { }
    }
}
