package com.pedro.ChamaKids.ui

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pedro.ChamaKids.data.MemberAttendanceInfo
import com.pedro.ChamaKids.data.MemberEntity
import kotlinx.datetime.*

@Composable
fun ListDetailScreen(
    membroId: Int,
    memberViewModel: MemberViewModel,
    attendanceViewModel: AttendanceViewModel,
    onVoltar: () -> Unit
) {
    var membro by remember { mutableStateOf<MemberEntity?>(null) }
    var historico by remember { mutableStateOf<List<MemberAttendanceInfo>>(emptyList()) }
    var historicoEstrelas by remember { mutableStateOf<List<Long>>(emptyList()) }
    var frequencia by remember { mutableStateOf<Float?>(null) }
    
    val hoje = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    var mesSelecionado by remember { mutableStateOf(hoje.monthNumber - 1) }
    var anoSelecionado by remember { mutableStateOf(hoje.year) }
    
    var mostrarSeletorMes by remember { mutableStateOf(false) }

    var expandidoPresencas by remember { mutableStateOf(true) }
    var expandidoGraficoPresencas by remember { mutableStateOf(false) }
    var expandidoCalendarioEstrelas by remember { mutableStateOf(false) }
    var expandidoGraficoEstrelas by remember { mutableStateOf(false) }

    LaunchedEffect(membroId) {
        membro = memberViewModel.buscarMembroPorId(membroId)
        historico = attendanceViewModel.buscarHistorico(membroId)
        frequencia = attendanceViewModel.calcularFrequencia(membroId)
        historicoEstrelas = memberViewModel.buscarHistoricoEstrelas(membroId)
    }

    val frequenciasMensais = remember(historico, anoSelecionado) {
        (0..11).map { mes ->
            val registrosNoMes = historico.filter {
                val dt = Instant.fromEpochMilliseconds(it.dataHora).toLocalDateTime(TimeZone.UTC)
                dt.year == anoSelecionado && dt.monthNumber == (mes + 1)
            }
            if (registrosNoMes.isEmpty()) null
            else {
                val presentes = registrosNoMes.count { it.presente }
                (presentes.toFloat() / registrosNoMes.size.toFloat()) * 100f
            }
        }
    }

    ChamaKidsScreen(
        titulo = "DETALHES",
        onVoltar = onVoltar
    ) {
        if (membro == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Carregando...")
            }
        } else {
            val m = membro!!

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = m.nome.uppercase(), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        Text(text = "Frequência Total:", fontSize = 14.sp, color = Color.Black, modifier = Modifier.padding(top = 4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val status = StatusFrequencia.aPartirDaPorcentagem(frequencia)
                            Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(status.cor))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = status.label, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = status.cor)
                        }
                    }

                    Box(modifier = Modifier.size(70.dp).clip(CircleShape).background(Color(0xFFD9D9D9)), contentAlignment = Alignment.Center) {
                        MemberImage(
                            fotoUri = m.fotoUri,
                            modifier = Modifier.fillMaxSize(),
                            placeholderText = m.nome.firstOrNull()?.uppercase() ?: "?"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

                SecaoExpansivel(
                    titulo = "Calendário de Presenças",
                    expandido = expandidoPresencas,
                    onToggle = { expandidoPresencas = !expandidoPresencas }
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        TextoMesAno(mesSelecionado, anoSelecionado) { mostrarSeletorMes = true }
                        Spacer(modifier = Modifier.height(12.dp))
                        CalendarioMensal(ano = anoSelecionado, mes = mesSelecionado, historico = historico)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                SecaoExpansivel(
                    titulo = "Gráfico de Presenças Mensais",
                    expandido = expandidoGraficoPresencas,
                    onToggle = { expandidoGraficoPresencas = !expandidoGraficoPresencas }
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        GraficoBarrasMensal(frequencias = frequenciasMensais)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Toque em uma barra para ver o valor", fontSize = 10.sp, color = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                SecaoExpansivel(
                    titulo = "Calendário de Estrelas",
                    expandido = expandidoCalendarioEstrelas,
                    onToggle = { expandidoCalendarioEstrelas = !expandidoCalendarioEstrelas }
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        TextoMesAno(mesSelecionado, anoSelecionado) { mostrarSeletorMes = true }
                        Spacer(modifier = Modifier.height(12.dp))
                        CalendarioMensal(ano = anoSelecionado, mes = mesSelecionado, historicoEstrelas = historicoEstrelas)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                SecaoExpansivel(
                    titulo = "Gráfico de Estrelas",
                    expandido = expandidoGraficoEstrelas,
                    onToggle = { expandidoGraficoEstrelas = !expandidoGraficoEstrelas }
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        GraficoLinhasEstrelas(historicoEstrelas = historicoEstrelas, ano = anoSelecionado)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Toque em um ponto para ver a quantidade", fontSize = 10.sp, color = Color.Gray)
                    }
                }
                
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }

    if (mostrarSeletorMes) {
        SeletorMesDialog(
            mesAtual = mesSelecionado,
            anoAtual = anoSelecionado,
            onDismiss = { mostrarSeletorMes = false },
            onConfirm = { mes, ano ->
                mesSelecionado = mes
                anoSelecionado = ano
                mostrarSeletorMes = false
            }
        )
    }
}

@Composable
private fun TextoMesAno(mes: Int, ano: Int, onClick: () -> Unit) {
    val nomesMeses = listOf("Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro")
    Row(modifier = Modifier.clickable { onClick() }, verticalAlignment = Alignment.CenterVertically) {
        Text(text = "${nomesMeses[mes]} $ano", fontSize = 14.sp, color = Color.Gray)
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = "▼", fontSize = 10.sp, color = Color.Gray)
    }
}

@Composable
fun SecaoExpansivel(
    titulo: String,
    expandido: Boolean,
    onToggle: () -> Unit,
    conteudo: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth().clickable { onToggle() },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = titulo, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Text(text = if (expandido) "↑" else "↓", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
            }
            if (expandido) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFEEEEEE))
                conteudo()
            }
        }
    }
}
