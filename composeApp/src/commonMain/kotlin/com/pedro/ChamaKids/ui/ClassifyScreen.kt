package com.pedro.ChamaKids.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ClassifyScreen(
    onVoltar: () -> Unit,
    onClassificarMembro: (Int) -> Unit,
    viewModel: MemberViewModel
) {
    val membros by viewModel.membros.collectAsState()

    ChamaKidsScreen(
        titulo = "CLASSIFICAR",
        onVoltar = onVoltar
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, bottom = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                // Sombra/Contorno Preto
                Text(
                    text = "Escolha quem receberá uma estrela",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    style = TextStyle(
                        drawStyle = Stroke(
                            width = 4f
                        )
                    )
                )
                // Texto Amarelo Brilhante
                Text(
                    text = "Escolha quem receberá uma estrela",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFFFFD600),
                    textAlign = TextAlign.Center
                )
            }

            if (membros.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Nenhum membro cadastrado.", color = Color.Gray)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(membros) { membro ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable { onClassificarMembro(membro.id) }
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1f)
                                    .clip(CircleShape)
                                    .background(Color(0xFFD9D9D9)),
                                contentAlignment = Alignment.Center
                            ) {
                                MemberImage(
                                    fotoUri = membro.fotoUri,
                                    modifier = Modifier.fillMaxSize(),
                                    placeholderText = membro.nome.firstOrNull()?.uppercase() ?: "?"
                                )
                            }
                            
                            Text(
                                text = membro.nome.split(" ").firstOrNull() ?: "",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(top = 4.dp),
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}
