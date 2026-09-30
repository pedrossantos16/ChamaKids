package com.pedro.ChamaKids.ui

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
actual fun MemberImage(
    fotoUri: String?, 
    modifier: Modifier, 
    placeholderText: String,
    onClick: (() -> Unit)?
) {
    val context = LocalContext.current
    var bitmap by remember(fotoUri) { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(fotoUri) {
        if (fotoUri.isNullOrBlank()) {
            bitmap = null
            return@LaunchedEffect
        }

        withContext(Dispatchers.IO) {
            try {
                val loadedBitmap = when {
                    fotoUri.startsWith("data:image/") -> {
                        val base64Data = fotoUri.substringAfter("base64,")
                        val imageBytes = android.util.Base64.decode(base64Data, android.util.Base64.DEFAULT)
                        BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)?.asImageBitmap()
                    }
                    fotoUri.startsWith("http://") || fotoUri.startsWith("https://") -> {
                        val url = java.net.URL(fotoUri)
                        val connection = url.openConnection()
                        connection.connectTimeout = 8000
                        connection.readTimeout = 8000
                        connection.getInputStream().use { stream ->
                            BitmapFactory.decodeStream(stream)?.asImageBitmap()
                        }
                    }
                    fotoUri.startsWith("/") -> {
                        BitmapFactory.decodeFile(fotoUri)?.asImageBitmap()
                    }
                    else -> {
                        context.contentResolver.openInputStream(Uri.parse(fotoUri))?.use { stream ->
                            BitmapFactory.decodeStream(stream)?.asImageBitmap()
                        }
                    }
                }
                bitmap = loadedBitmap
            } catch (_: Exception) {
                bitmap = null
            }
        }
    }

    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(Color(0xFFD9D9D9))
            .then(
                if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap!!,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Text(
                text = placeholderText,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
        }
    }
}
