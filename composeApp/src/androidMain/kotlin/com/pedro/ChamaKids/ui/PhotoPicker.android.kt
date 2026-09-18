package com.pedro.ChamaKids.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.pedro.ChamaKids.FileUtils

@Composable
actual fun rememberPhotoPicker(
    onResult: (String?) -> Unit
): () -> Unit {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            val path = FileUtils.salvarFotoInterna(context, uri)
            onResult(path)
        } else {
            onResult(null)
        }
    }

    return {
        launcher.launch("image/*")
    }
}
