package com.pedro.ChamaKids.ui

import androidx.compose.runtime.Composable

@Composable
actual fun rememberPhotoPicker(
    onResult: (String?) -> Unit
): () -> Unit {
    return {
        // TODO: Implementar PHPickerViewController para iOS
        onResult(null)
    }
}
