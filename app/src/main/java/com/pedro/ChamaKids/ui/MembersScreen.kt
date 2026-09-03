package com.pedro.ChamaKids.ui

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pedro.ChamaKids.ui.theme.ChamaKidsAction

@Composable
fun MembersScreen(
    onVoltar: () -> Unit,
    onAdicionarMembro: () -> Unit,
    onAbrirMembro: (Int) -> Unit,
    viewModel: MemberViewModel
) {
    val context = LocalContext.current
    val membros by viewModel.membros.collectAsState()

    ChamaKidsScreen(
        titulo = "MEMBROS",
        onVoltar = onVoltar
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            // LISTA ROLÁVEL
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                if (membros.isEmpty()) {
                    Text(
                        text = "Nenhum membro cadastrado.",
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                } else {
                    membros.forEach { membro ->
                        val bitmapFoto = remember(membro.fotoUri) {
                            if (membro.fotoUri.isNullOrBlank()) null
                            else {
                                try {
                                    context.contentResolver.openInputStream(Uri.parse(membro.fotoUri))?.use {
                                        BitmapFactory.decodeStream(it)
                                    }
                                } catch (_: Exception) { null }
                            }
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                                .clickable { onAbrirMembro(membro.id) },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F1F5))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // FOTO
                                Box(
                                    modifier = Modifier
                                        .size(52.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFD9D9D9)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (bitmapFoto != null) {
                                        Image(
                                            bitmap = bitmapFoto.asImageBitmap(),
                                            contentDescription = null,
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        Text(
                                            text = membro.nome.firstOrNull()?.uppercase() ?: "?",
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Gray
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                // NOME
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = membro.nome,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "Abrir informações",
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }

                                Text(text = ">", fontSize = 24.sp)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            // BOTÃO FIXO
            Button(
                onClick = onAdicionarMembro,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp, bottom = 45.dp) // Subir o botão da extremidade
                    .height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ChamaKidsAction, contentColor = Color.Black),
                border = BorderStroke(1.5.dp, Color.Black)
            ) {
                Text(text = "ADICIONAR MEMBRO", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
