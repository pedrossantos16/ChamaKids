package com.pedro.ChamaKids.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import com.pedro.ChamaKids.data.ActionLogEntity
import com.pedro.ChamaKids.ui.theme.ChamaKidsAction
import com.pedro.ChamaKids.ui.theme.ChamaKidsBlue
import kotlinx.datetime.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HistoryScreen(
    viewModel: AttendanceViewModel,
    onVoltar: () -> Unit,
    onAbrirChamada: (String) -> Unit
) {
    var abaSelecionada by remember { mutableIntStateOf(0) } // 0 = Chamadas, 1 = Ações

    val chamadas by viewModel.chamadas.collectAsState()
    val acoes by viewModel.acoes.collectAsState()

    val selecionadosChamadas = remember { mutableStateListOf<String>() }
    val selecionadosAcoes = remember { mutableStateListOf<String>() }

    var acaoDetalhe by remember { mutableStateOf<ActionLogEntity?>(null) }

    val temSelecao = if (abaSelecionada == 0) selecionadosChamadas.isNotEmpty() else selecionadosAcoes.isNotEmpty()

    ChamaKidsScreen(
        titulo = "HISTÓRICO",
        onVoltar = {
            if (temSelecao) {
                if (abaSelecionada == 0) selecionadosChamadas.clear()
                else selecionadosAcoes.clear()
            } else onVoltar()
        },
        acoesDireita = {
            if (temSelecao) {
                IconButton(onClick = {
                    if (abaSelecionada == 0) {
                        viewModel.excluirChamadas(selecionadosChamadas.toList())
                        selecionadosChamadas.clear()
                    } else {
                        viewModel.excluirAcoes(selecionadosAcoes.toList())
                        selecionadosAcoes.clear()
                    }
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
        Column(modifier = Modifier.fillMaxSize()) {
            // SELETOR DE ABAS (CHAMADAS vs AÇÕES)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
                    .background(Color(0xFFF0F0F0), RoundedCornerShape(12.dp))
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (abaSelecionada == 0) ChamaKidsBlue else Color.Transparent)
                        .border(if (abaSelecionada == 0) 1.5.dp else 0.dp, Color.Black, RoundedCornerShape(8.dp))
                        .clickable { abaSelecionada = 0 },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "CHAMADAS",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = if (abaSelecionada == 0) Color.White else Color.Gray
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (abaSelecionada == 1) ChamaKidsBlue else Color.Transparent)
                        .border(if (abaSelecionada == 1) 1.5.dp else 0.dp, Color.Black, RoundedCornerShape(8.dp))
                        .clickable { abaSelecionada = 1 },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "AÇÕES",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = if (abaSelecionada == 1) Color.White else Color.Gray
                    )
                }
            }

            // CONTEÚDO DA ABA SELECIONADA
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                if (abaSelecionada == 0) {
                    // --- ABA CHAMADAS ---
                    if (chamadas.isEmpty()) {
                        Text(text = "Nenhuma chamada registrada.", color = Color.Gray, modifier = Modifier.padding(top = 16.dp))
                    } else {
                        chamadas.forEach { chamada ->
                            val isSelecionado = selecionadosChamadas.contains(chamada.serverId)
                            
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
                                            if (selecionadosChamadas.isNotEmpty()) {
                                                if (isSelecionado) selecionadosChamadas.remove(chamada.serverId)
                                                else selecionadosChamadas.add(chamada.serverId)
                                            } else onAbrirChamada(chamada.serverId)
                                        },
                                        onLongClick = {
                                            if (!isSelecionado) selecionadosChamadas.add(chamada.serverId)
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
                    }
                } else {
                    // --- ABA AÇÕES ---
                    if (acoes.isEmpty()) {
                        Text(text = "Nenhuma ação registrada no histórico.", color = Color.Gray, modifier = Modifier.padding(top = 16.dp))
                    } else {
                        acoes.forEach { acao ->
                            val isSelecionado = selecionadosAcoes.contains(acao.serverId)

                            val zdt = Instant.fromEpochMilliseconds(acao.dataHora).toLocalDateTime(TimeZone.currentSystemDefault())
                            val dataStr = "${zdt.dayOfMonth.toString().padStart(2, '0')}/${zdt.monthNumber.toString().padStart(2, '0')}/${zdt.year}"
                            val horaStr = "${zdt.hour.toString().padStart(2, '0')}:${zdt.minute.toString().padStart(2, '0')}"

                            val icone = when {
                                acao.tipoAcao.contains("Membro", ignoreCase = true) -> "📝"
                                acao.tipoAcao.contains("Chamada", ignoreCase = true) -> "📋"
                                acao.tipoAcao.contains("Estrela", ignoreCase = true) -> "⭐"
                                else -> "👤"
                            }

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .combinedClickable(
                                        onClick = {
                                            if (selecionadosAcoes.isNotEmpty()) {
                                                if (isSelecionado) selecionadosAcoes.remove(acao.serverId)
                                                else selecionadosAcoes.add(acao.serverId)
                                            } else {
                                                acaoDetalhe = acao
                                            }
                                        },
                                        onLongClick = {
                                            if (!isSelecionado) selecionadosAcoes.add(acao.serverId)
                                        }
                                    ),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelecionado) Color(0xFFE3F2FD) else Color.White
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(text = icone, fontSize = 20.sp, modifier = Modifier.padding(end = 8.dp))
                                            Text(
                                                text = acao.tipoAcao,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.sp,
                                                color = Color.Black
                                            )
                                        }
                                        Box(
                                            modifier = Modifier
                                                .background(Color(0xFFE0F7FA), RoundedCornerShape(12.dp))
                                                .border(1.dp, Color(0xFF00838F), RoundedCornerShape(12.dp))
                                                .padding(horizontal = 8.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "por ${acao.usuarioNome}",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF006064)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = acao.descricao,
                                        fontSize = 14.sp,
                                        color = Color(0xFF333333),
                                        fontWeight = FontWeight.Medium
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "$dataStr - $horaStr",
                                            fontSize = 12.sp,
                                            color = Color.Gray
                                        )
                                        if (isSelecionado) {
                                            Box(
                                                modifier = Modifier
                                                    .size(20.dp)
                                                    .clip(CircleShape)
                                                    .background(Color(0xFF1976D2)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(text = "✓", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }

    // DIÁLOGO DE DETALHES DA AÇÃO
    if (acaoDetalhe != null) {
        val log = acaoDetalhe!!
        val zdt = Instant.fromEpochMilliseconds(log.dataHora).toLocalDateTime(TimeZone.currentSystemDefault())
        val dataStr = "${zdt.dayOfMonth.toString().padStart(2, '0')}/${zdt.monthNumber.toString().padStart(2, '0')}/${zdt.year}"
        val horaStr = "${zdt.hour.toString().padStart(2, '0')}:${zdt.minute.toString().padStart(2, '0')}:${zdt.second.toString().padStart(2, '0')}"

        AlertDialog(
            onDismissRequest = { acaoDetalhe = null },
            title = {
                Text(text = "DETALHES DA AÇÃO", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            },
            text = {
                Column {
                    Text(text = "Tipo:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Gray)
                    Text(text = log.tipoAcao, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text(text = "Usuário Responsável:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Gray)
                    Text(text = log.usuarioNome, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF006064))
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text(text = "Data e Horário:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Gray)
                    Text(text = "$dataStr às $horaStr", fontSize = 14.sp, color = Color.Black)
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text(text = "O que foi feito:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Gray)
                    Text(text = log.descricao, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = Color.Black)
                }
            },
            confirmButton = {
                Button(
                    onClick = { acaoDetalhe = null },
                    colors = ButtonDefaults.buttonColors(containerColor = ChamaKidsAction, contentColor = Color.Black)
                ) {
                    Text("FECHAR", fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}
