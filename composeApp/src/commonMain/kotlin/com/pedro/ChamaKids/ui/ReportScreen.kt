package com.pedro.ChamaKids.ui

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pedro.ChamaKids.PdfGenerator
import com.pedro.ChamaKids.data.MemberWithStarStats
import com.pedro.ChamaKids.data.MemberWithStats
import com.pedro.ChamaKids.data.PeriodStat
import kotlinx.datetime.*

@Composable
fun ReportScreen(
    onVoltar: () -> Unit,
    memberViewModel: MemberViewModel,
    attendanceViewModel: AttendanceViewModel
) {
    val hoje = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    var mesSelecionado by remember { mutableStateOf(hoje.monthNumber - 1) } // 0-11
    var anoSelecionado by remember { mutableStateOf(hoje.year) }
    
    var mostrarSeletor by remember { mutableStateOf(false) }

    var childMostPresent by remember { mutableStateOf<MemberWithStats?>(null) }
    var childBestBehavior by remember { mutableStateOf<MemberWithStarStats?>(null) }
    var dayHighestAttendance by remember { mutableStateOf<PeriodStat?>(null) }
    var dayMostStars by remember { mutableStateOf<PeriodStat?>(null) }
    var carregando by remember { mutableStateOf(true) }

    LaunchedEffect(mesSelecionado, anoSelecionado) {
        carregando = true
        
        val inicio = LocalDate(anoSelecionado, mesSelecionado + 1, 1).atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()
        val fim = if (mesSelecionado == 11) {
            LocalDate(anoSelecionado + 1, 1, 1).atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds() - 1
        } else {
            LocalDate(anoSelecionado, mesSelecionado + 2, 1).atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds() - 1
        }

        childMostPresent = attendanceViewModel.buscarMembroMaisPresente(inicio, fim)
        childBestBehavior = memberViewModel.buscarMembroMaisEstrelas(inicio, fim)
        dayHighestAttendance = attendanceViewModel.buscarDiaMaiorAssiduidade(inicio, fim)
        dayMostStars = memberViewModel.buscarDiaMaisEstrelas(inicio, fim)
        
        carregando = false
    }

    val nomesMeses = listOf("Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro")
    val mesAnoStr = "${nomesMeses[mesSelecionado]} $anoSelecionado".uppercase()

    ChamaKidsScreen(
        titulo = "RELATÓRIO",
        onVoltar = onVoltar,
        acoesDireita = {
            IconButton(onClick = {
                PdfGenerator.gerarEPrompt(
                    mesAno = mesAnoStr,
                    membroMaisPresente = childMostPresent,
                    melhorComportamento = childBestBehavior,
                    diaMaiorAssiduidade = dayHighestAttendance,
                    diaMaisEstrelas = dayMostStars
                )
            }) {
                Canvas(modifier = Modifier.size(28.dp)) {
                    val cor = Color.Black
                    val largura = 2.dp.toPx()
                    drawRect(
                        color = cor,
                        topLeft = Offset(4.dp.toPx(), 2.dp.toPx()),
                        size = Size(20.dp.toPx(), 24.dp.toPx()),
                        style = androidx.compose.ui.graphics.drawscope.Stroke(largura)
                    )
                    drawLine(cor, Offset(8.dp.toPx(), 10.dp.toPx()), Offset(20.dp.toPx(), 10.dp.toPx()), largura)
                    drawLine(cor, Offset(8.dp.toPx(), 15.dp.toPx()), Offset(20.dp.toPx(), 15.dp.toPx()), largura)
                    drawLine(cor, Offset(8.dp.toPx(), 20.dp.toPx()), Offset(14.dp.toPx(), 20.dp.toPx()), largura)
                }
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .clickable { mostrarSeletor = true },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = mesAnoStr,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "▼", fontSize = 12.sp, color = Color.Gray)
                }
            }

            if (carregando) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.Black)
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    SecaoRelatorio(titulo = "CRIANÇA MAIS PRESENTE") {
                        if (childMostPresent != null) {
                            InfoMembroRelatorio(
                                nome = childMostPresent!!.nome,
                                fotoUri = childMostPresent!!.fotoUri,
                                subtitulo = "${childMostPresent!!.count} presenças no mês"
                            )
                        } else {
                            Text("Sem dados de presença este mês.", color = Color.Gray)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    SecaoRelatorio(titulo = "MELHOR COMPORTAMENTO") {
                        if (childBestBehavior != null) {
                            Column {
                                InfoMembroRelatorio(
                                    nome = childBestBehavior!!.nome,
                                    fotoUri = childBestBehavior!!.fotoUri,
                                    subtitulo = "${childBestBehavior!!.count} estrelas recebidas ★"
                                )
                                if (!childBestBehavior!!.comentario.isNullOrBlank()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "\"${childBestBehavior!!.comentario}\"",
                                        fontSize = 13.sp,
                                        color = Color.DarkGray,
                                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                    )
                                }
                            }
                        } else {
                            Text("Sem estrelas distribuídas este mês.", color = Color.Gray)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    SecaoRelatorio(titulo = "DIA COM MAIS CRIANÇAS") {
                        if (dayHighestAttendance != null) {
                            Column {
                                val date = Instant.fromEpochMilliseconds(dayHighestAttendance!!.timestamp).toLocalDateTime(TimeZone.UTC).date
                                Text(
                                    text = "${date.dayOfMonth.toString().padStart(2, '0')}/${date.monthNumber.toString().padStart(2, '0')}/${date.year}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = "${dayHighestAttendance!!.count} crianças presentes",
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                            }
                        } else {
                            Text("Sem chamadas este mês.", color = Color.Gray)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    SecaoRelatorio(titulo = "DIA COM MAIS ESTRELAS") {
                        if (dayMostStars != null) {
                            Column {
                                val date = Instant.fromEpochMilliseconds(dayMostStars!!.timestamp).toLocalDateTime(TimeZone.UTC).date
                                Text(
                                    text = "${date.dayOfMonth.toString().padStart(2, '0')}/${date.monthNumber.toString().padStart(2, '0')}/${date.year}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = "${dayMostStars!!.count} estrelas distribuídas ★",
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                            }
                        } else {
                            Text("Sem estrelas este mês.", color = Color.Gray)
                        }
                    }

                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }
    }

    if (mostrarSeletor) {
        SeletorMesDialog(
            mesAtual = mesSelecionado,
            anoAtual = anoSelecionado,
            onDismiss = { mostrarSeletor = false },
            onConfirm = { mes, ano ->
                mesSelecionado = mes
                anoSelecionado = ano
                mostrarSeletor = false
            }
        )
    }
}

@Composable
private fun SecaoRelatorio(titulo: String, conteudo: @Composable () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFE0E0E0), RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                .padding(vertical = 10.dp, horizontal = 16.dp)
        ) {
            Text(text = titulo, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp))
                .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp))
                .padding(16.dp)
        ) {
            conteudo()
        }
    }
}

@Composable
private fun InfoMembroRelatorio(nome: String, fotoUri: String?, subtitulo: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(Color(0xFFF1F1F5)),
            contentAlignment = Alignment.Center
        ) {
            MemberImage(
                fotoUri = fotoUri,
                modifier = Modifier.fillMaxSize(),
                placeholderText = nome.first().uppercase()
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = nome, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(text = subtitulo, fontSize = 14.sp, color = Color.Gray)
        }
    }
}
