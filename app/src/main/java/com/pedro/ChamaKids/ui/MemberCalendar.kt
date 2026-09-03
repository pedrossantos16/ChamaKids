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
import java.util.*

@Composable
fun CalendarioMensal(
    ano: Int,
    mes: Int,
    historico: List<MemberAttendanceInfo>? = null,
    historicoEstrelas: List<Long>? = null
) {
    val calendar = Calendar.getInstance().apply {
        set(Calendar.YEAR, ano)
        set(Calendar.MONTH, mes)
        set(Calendar.DAY_OF_MONTH, 1)
    }

    val diasNoMes = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    val primeiroDiaSemana = calendar.get(Calendar.DAY_OF_WEEK)

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
                                val calReg = Calendar.getInstance().apply { timeInMillis = it.dataHora }
                                calReg.get(Calendar.YEAR) == ano &&
                                        calReg.get(Calendar.MONTH) == mes &&
                                        calReg.get(Calendar.DAY_OF_MONTH) == localDia
                            }

                            if (registrosNoDia.isNotEmpty()) {
                                corFundo = if (registrosNoDia.any { it.presente }) Color(0xFF00A381) else Color(0xFFD32F2F)
                                corTexto = Color.White
                                negrito = true
                            }
                        } else if (historicoEstrelas != null) {
                            val temEstrela = historicoEstrelas.any {
                                val calReg = Calendar.getInstance().apply { timeInMillis = it }
                                calReg.get(Calendar.YEAR) == ano &&
                                        calReg.get(Calendar.MONTH) == mes &&
                                        calReg.get(Calendar.DAY_OF_MONTH) == localDia
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
                Row(verticalAlignment = Alignment.CenterVertically) {
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
