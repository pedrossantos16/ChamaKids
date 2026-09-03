package com.pedro.ChamaKids.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pedro.ChamaKids.data.MemberAttendanceInfo
import kotlinx.datetime.*

@Composable
fun CalendarioMensal(
    ano: Int,
    mes: Int, // 0-11
    historico: List<MemberAttendanceInfo>? = null,
    historicoEstrelas: List<Long>? = null
) {
    val mesKmp = mes + 1
    val primeiroDia = LocalDate(ano, mesKmp, 1)
    
    val diasNoMes = when (mesKmp) {
        1, 3, 5, 7, 8, 10, 12 -> 31
        4, 6, 9, 11 -> 30
        2 -> if (ano % 4 == 0 && (ano % 100 != 0 || ano % 400 == 0)) 29 else 28
        else -> 30
    }
    
    val primeiroDiaSemana = when (primeiroDia.dayOfWeek) {
        DayOfWeek.SUNDAY -> 1
        DayOfWeek.MONDAY -> 2
        DayOfWeek.TUESDAY -> 3
        DayOfWeek.WEDNESDAY -> 4
        DayOfWeek.THURSDAY -> 5
        DayOfWeek.FRIDAY -> 6
        DayOfWeek.SATURDAY -> 7
    }

    val diasSemana = listOf("D", "S", "T", "Q", "Q", "S", "S")

    Column {
        Row(modifier = Modifier.fillMaxWidth()) {
            diasSemana.forEach { dia ->
                Text(
                    text = dia,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        var diaAtual = 1
        for (semana in 0..5) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (diaSemana in 1..7) {
                    val indiceGlobal = semana * 7 + diaSemana
                    if (indiceGlobal < primeiroDiaSemana || diaAtual > diasNoMes) {
                        Spacer(modifier = Modifier.weight(1f))
                    } else {
                        val localDia = diaAtual
                        
                        var corFundo = Color.Transparent
                        var corTexto = Color.Black
                        var negrito = false

                        if (historico != null) {
                            val registrosNoDia = historico.filter {
                                val dateTime = Instant.fromEpochMilliseconds(it.dataHora).toLocalDateTime(TimeZone.UTC)
                                dateTime.year == ano &&
                                        dateTime.monthNumber == mesKmp &&
                                        dateTime.dayOfMonth == localDia
                            }

                            if (registrosNoDia.isNotEmpty()) {
                                corFundo = if (registrosNoDia.any { it.presente }) Color(0xFF00A381) else Color(0xFFD32F2F)
                                corTexto = Color.White
                                negrito = true
                            }
                        } else if (historicoEstrelas != null) {
                            val temEstrela = historicoEstrelas.any {
                                val dateTime = Instant.fromEpochMilliseconds(it).toLocalDateTime(TimeZone.UTC)
                                dateTime.year == ano &&
                                        dateTime.monthNumber == mesKmp &&
                                        dateTime.dayOfMonth == localDia
                            }

                            if (temEstrela) {
                                corFundo = Color(0xFFFFD600)
                                corTexto = Color.Black
                                negrito = true
                            }
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(2.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(corFundo),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = diaAtual.toString(),
                                fontSize = 12.sp,
                                color = corTexto,
                                fontWeight = if (negrito) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                        diaAtual++
                    }
                }
            }
            if (diaAtual > diasNoMes) break
        }
    }
}

@Composable
fun SeletorMesDialog(
    mesAtual: Int,
    anoAtual: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int, Int) -> Unit
) {
    var mesSelecionado by remember { mutableStateOf(mesAtual) }
    var anoSelecionado by remember { mutableStateOf(anoAtual) }
    
    val meses = listOf(
        "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
        "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Selecionar Mês/Ano") },
        text = {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                    IconButton(onClick = { anoSelecionado-- }) { Text("-") }
                    Text(text = anoSelecionado.toString(), fontWeight = FontWeight.Bold)
                    IconButton(onClick = { anoSelecionado++ }) { Text("+") }
                }
                
                meses.chunked(3).forEachIndexed { rowIndex, mesGroup ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        mesGroup.forEachIndexed { colIndex, nomeMes ->
                            val index = rowIndex * 3 + colIndex
                            TextButton(
                                onClick = { mesSelecionado = index },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.textButtonColors(
                                    containerColor = if (mesSelecionado == index) Color.LightGray else Color.Transparent
                                )
                            ) {
                                Text(text = nomeMes.take(3), fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(mesSelecionado, anoSelecionado) }) {
                Text("OK")
            }
        }
    )
}
