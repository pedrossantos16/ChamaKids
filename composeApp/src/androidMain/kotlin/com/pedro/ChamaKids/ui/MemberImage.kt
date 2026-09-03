package com.pedro.ChamaKids.ui

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import java.io.File

@Composable
actual fun MemberImage(fotoUri: String?, modifier: Modifier, placeholderText: String) {
    val context = LocalContext.current
    val bitmap = remember(fotoUri) {
        if (fotoUri.isNullOrBlank()) null
        else {
            try {
                if (fotoUri.startsWith("/")) {
                    BitmapFactory.decodeFile(fotoUri)
                } else {
                    context.contentResolver.openInputStream(Uri.parse(fotoUri))?.use {
                        BitmapFactory.decodeStream(it)
                    }
                }
            } catch (_: Exception) {
                null
            }
        }
    }

    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(Color(0xFFD9D9D9)),
        contentAlignment = Alignment.Center
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap.asImageBitmap(),
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
