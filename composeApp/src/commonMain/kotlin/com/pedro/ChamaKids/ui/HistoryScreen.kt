package com.pedro.ChamaKids.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.datetime.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HistoryScreen(
    viewModel: AttendanceViewModel,
    onVoltar: () -> Unit,
    onAbrirChamada: (Int) -> Unit
) {
    val chamadas by viewModel.chamadas.collectAsState()
    val selecionados = remember { mutableStateListOf<Int>() }

    ChamaKidsScreen(
        titulo = "HISTÓRICO",
        onVoltar = {
            if (selecionados.isNotEmpty()) selecionados.clear()
            else onVoltar()
        },
        acoesDireita = {
            if (selecionados.isNotEmpty()) {
                IconButton(onClick = {
                    viewModel.excluirChamadas(selecionados.toList())
                    selecionados.clear()
                }) {
                    Canvas(modifier = Modifier.size(28.dp)) {
                        val cor = Color(0xFFD32F2F)
                        val largura = 2.dp.toPx()
                        drawRect(color = cor, topLeft = Offset(4.dp.toPx(), 8.dp.toPx()), size = Size(20.dp.toPx(), 18.dp.toPx()), style = Stroke(largura))
                        drawLine(color = cor, start = Offset(2.dp.toPx(), 6.dp.toPx()), end = Offset(26.dp.toPx(), 6.dp.toPx()), strokeWidth = largura)
                        drawRect(color = cor, topLeft = Offset(10.dp.toPx(), 2.dp.toPx()), size = Size(8.dp.toPx(), 4.dp.toPx()), style = Stroke(largura))
                        drawLine(cor, Offset(10.dp.toPx(), 12.dp.toPx()), Offset(10.dp.toPx(), 22.dp.toPx()), largura)
                        drawLine(cor, Offset(14.dp.toPx(), 12.dp.toPx()), Offset(14.dp.toPx(), 22.dp.toPx()), largura)
                        drawLine(cor, Offset(18.dp.toPx(), 12.dp.toPx()), Offset(18.dp.toPx(), 22.dp.toPx()), largura)
                    }
                }
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            if (chamadas.isEmpty()) {
                Text(text = "Nenhuma chamada registrada.", color = Color.Gray)
            } else {
                chamadas.forEach { chamada ->
                    val isSelecionado = selecionados.contains(chamada.id)
                    
                    val zdt = Instant.fromEpochMilliseconds(chamada.dataHora).toLocalDateTime(TimeZone.currentSystemDefault())
                    val data = "${zdt.dayOfMonth.toString().padStart(2, '0')}/${zdt.monthNumber.toString().padStart(2, '0')}/${zdt.year}"
                    val hora = "${zdt.hour.toString().padStart(2, '0')}:${zdt.minute.toString().padStart(2, '0')}"

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .combinedClickable(
                                onClick = {
                                    if (selecionados.isNotEmpty()) {
                                        if (isSelecionado) selecionados.remove(chamada.id)
                                        else selecionados.add(chamada.id)
                                    } else onAbrirChamada(chamada.id)
                                },
                                onLongClick = {
                                    if (!isSelecionado) selecionados.add(chamada.id)
                                }
                            ),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelecionado) Color(0xFFE3F2FD) else Color.White
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(18.dp).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                if (!chamada.nome.isNullOrBlank()) {
                                    Text(
                                        text = chamada.nome,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelecionado) Color(0xFF1976D2) else Color.Black
                                    )
                                }
                                Text(
                                    text = "$data - $hora",
                                    fontSize = 14.sp,
                                    color = Color.Gray,
                                    fontWeight = if (isSelecionado) FontWeight.Bold else FontWeight.Normal
                                )
                            }

                            if (isSelecionado) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF1976D2)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = "✓", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}
