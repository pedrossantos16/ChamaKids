package com.pedro.ChamaKids.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight

@Composable
actual fun MemberImage(fotoUri: String?, modifier: Modifier, placeholderText: String) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(Color(0xFFD9D9D9)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = placeholderText,
            fontWeight = FontWeight.Bold,
            color = Color.Gray
        )
    }
}
