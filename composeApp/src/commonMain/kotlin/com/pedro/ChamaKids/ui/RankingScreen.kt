package com.pedro.ChamaKids.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pedro.ChamaKids.data.MemberWithRanking

@Composable
fun RankingScreen(
    onVoltar: () -> Unit,
    viewModel: MemberViewModel
) {
    var ranking by remember { mutableStateOf<List<MemberWithRanking>>(emptyList()) }

    LaunchedEffect(Unit) {
        ranking = viewModel.buscarRanking()
    }

    ChamaKidsScreen(
        titulo = "RANKING",
        onVoltar = onVoltar
    ) {
        if (ranking.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Ainda não há dados para o ranking.", color = Color.Gray)
            }
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                // ÁREA DO PÓDIO (Canvas + Fotos)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(320.dp)
                        .padding(top = 40.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    // Desenho do Pódio
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val larguraPodio = size.width * 0.8f
                        val centroX = size.width / 2
                        val base = size.height - 20.dp.toPx()
                        
                        val larguraColuna = larguraPodio / 3
                        
                        // 2º Lugar (Esquerda)
                        drawRect(
                            color = Color(0xFFBDBDBD),
                            topLeft = Offset(centroX - larguraColuna * 1.5f, base - 80.dp.toPx()),
                            size = androidx.compose.ui.geometry.Size(larguraColuna, 80.dp.toPx())
                        )
                        
                        // 1º Lugar (Centro)
                        drawRect(
                            color = Color(0xFFFFD600),
                            topLeft = Offset(centroX - larguraColuna / 2, base - 120.dp.toPx()),
                            size = androidx.compose.ui.geometry.Size(larguraColuna, 120.dp.toPx())
                        )
                        
                        // 3º Lugar (Direita)
                        drawRect(
                            color = Color(0xFFCD7F32),
                            topLeft = Offset(centroX + larguraColuna / 2, base - 60.dp.toPx()),
                            size = androidx.compose.ui.geometry.Size(larguraColuna, 60.dp.toPx())
                        )
                    }

                    // Fotos dos Ganhadores
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        // 2º Lugar
                        if (ranking.size > 1) {
                            FotoPodio(ranking[1], 70.dp, Modifier.padding(end = 10.dp).offset(y = (-80).dp))
                        }
                        // 1º Lugar
                        if (ranking.size > 0) {
                            FotoPodio(ranking[0], 90.dp, Modifier.offset(y = (-120).dp))
                        }
                        // 3º Lugar
                        if (ranking.size > 2) {
                            FotoPodio(ranking[2], 60.dp, Modifier.padding(start = 10.dp).offset(y = (-60).dp))
                        }
                    }
                }

                // RESTANTE DA LISTA
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
                    contentPadding = PaddingValues(bottom = 20.dp)
                ) {
                    // Começamos do 4º lugar (índice 3)
                    val restante = if (ranking.size > 3) ranking.subList(3, ranking.size) else emptyList()
                    
                    itemsIndexed(restante) { index, membro ->
                        ItemRanking(membro, index + 4)
                    }
                }
            }
        }
    }
}

@Composable
fun FotoPodio(membro: MemberWithRanking, tamanho: androidx.compose.ui.unit.Dp, modifier: Modifier) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
        // Info acima da foto
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(bottom = 4.dp)) {
            Text(
                text = "${membro.totalEstrelas} ★",
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFFFFD600)
            )
            Text(
                text = "${membro.totalPresencas} pres.",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray
            )
        }

        Box(
            modifier = Modifier
                .size(tamanho)
                .clip(CircleShape)
                .background(Color.White)
                .border(3.dp, Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            MemberImage(
                fotoUri = membro.fotoUri,
                modifier = Modifier.fillMaxSize(),
                placeholderText = membro.nome.first().uppercase()
            )
        }
        Text(
            text = membro.nome.split(" ").first(),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.background(Color.White.copy(alpha = 0.7f), RoundedCornerShape(4.dp)).padding(horizontal = 4.dp)
        )
    }
}

@Composable
fun ItemRanking(membro: MemberWithRanking, posicao: Int) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "${posicao}º", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(40.dp))
            
            Text(text = membro.nome, fontSize = 16.sp, modifier = Modifier.weight(1f))
            
            Column(horizontalAlignment = Alignment.End) {
                Text(text = "${membro.totalEstrelas} ★", color = Color(0xFFFFD600), fontWeight = FontWeight.Bold)
                Text(text = "${membro.totalPresencas} pres.", fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}
