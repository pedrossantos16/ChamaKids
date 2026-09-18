package com.pedro.ChamaKids.ui

import androidx.compose.runtime.Composable

@Composable
expect fun rememberPhotoPicker(
    onResult: (String?) -> Unit
): () -> Unit
