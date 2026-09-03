package com.pedro.ChamaKids.ui

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pedro.ChamaKids.data.MemberEntity
import com.pedro.ChamaKids.ui.theme.ChamaKidsAction

@Composable
fun ClassifyDetailScreen(
    membroId: Int,
    viewModel: MemberViewModel,
    onVoltar: () -> Unit
) {
    val context = LocalContext.current
    var membro by remember { mutableStateOf<MemberEntity?>(null) }
    var comentario by remember { mutableStateOf("") }

    LaunchedEffect(membroId) {
        membro = viewModel.buscarMembroPorId(membroId)
    }

    ChamaKidsScreen(
        titulo = "CLASSIFICAR",
        onVoltar = onVoltar
    ) {
        if (membro == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Carregando...")
            }
        } else {
            val m = membro!!
            val bitmapFoto = remember(m.fotoUri) {
                if (m.fotoUri.isNullOrBlank()) null
                else {
                    try {
                        context.contentResolver.openInputStream(Uri.parse(m.fotoUri))?.use {
                            BitmapFactory.decodeStream(it)
                        }
                    } catch (e: Exception) { null }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(60.dp))

                // Texto de instrução
                Text(
                    text = "${m.nome.split(" ").firstOrNull()?.uppercase()} receberá 1 estrela ★",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 50.dp)
                )

                // Foto centralizada com Estrela atrás (Desenhada via Canvas)
                Box(
                    modifier = Modifier.size(300.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Estrela Gigante atrás desenhada à mão
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val centro = Offset(size.width / 2, size.height / 2)
                        val pontos = 5
                        val raioExterno = size.width / 2
                        val raioInterno = raioExterno / 2.5f
                        val path = androidx.compose.ui.graphics.Path()
                        
                        for (i in 0 until pontos * 2) {
                            val raio = if (i % 2 == 0) raioExterno else raioInterno
                            val angulo = Math.PI * i / pontos - Math.PI / 2
                            val x = centro.x + (raio * Math.cos(angulo)).toFloat()
                            val y = centro.y + (raio * Math.sin(angulo)).toFloat()
                            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                        }
                        path.close()
                        drawPath(path, Color(0xFFFFD600))
                    }

                    // Foto Circular
                    Box(
                        modifier = Modifier
                            .size(170.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFD9D9D9))
                            .border(6.dp, Color.White, CircleShape),
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
                                text = m.nome.firstOrNull()?.uppercase() ?: "?",
                                fontSize = 54.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

                Text(
                    text = m.nome.uppercase(),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(50.dp))

                // Linha de comentário opcional
                OutlinedTextField(
                    value = comentario,
                    onValueChange = { comentario = it },
                    label = { Text("Comentário (opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(40.dp))

                Button(
                    onClick = { 
                        viewModel.darEstrela(membroId, comentario) {
                            onVoltar()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 45.dp) // Subir botão
                        .height(54.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ChamaKidsAction, contentColor = Color.Black),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color.Black)
                ) {
                    Text("CONFIRMAR ESTRELA", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
