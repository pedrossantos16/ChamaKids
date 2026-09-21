package com.pedro.ChamaKids.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll

import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.pedro.ChamaKids.data.AttendanceRecordEntity
import com.pedro.ChamaKids.data.MemberEntity

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import kotlinx.datetime.*

@Composable
fun AttendanceDetailScreen(
    chamadaId: String,
    attendanceViewModel: AttendanceViewModel,
    memberViewModel: MemberViewModel,
    onVoltar: () -> Unit
) {
    var registros by remember { mutableStateOf<List<AttendanceRecordEntity>>(emptyList()) }
    var membros by remember { mutableStateOf<Map<String, MemberEntity>>(emptyMap()) }
    var membrosComEstrelaNoDia by remember { mutableStateOf<Set<String>>(emptySet()) }
    var carregando by remember { mutableStateOf(true) }

    LaunchedEffect(chamadaId) {
        val chamada = attendanceViewModel.buscarChamadaPorId(chamadaId)
        val dataChamada = chamada?.dataHora ?: 0L

        registros = attendanceViewModel.buscarRegistrosDaChamada(chamadaId)

        val mapaMembros = mutableMapOf<String, MemberEntity>()
        val idsComEstrela = mutableSetOf<String>()

        registros.forEach { registro ->
            val membro = memberViewModel.buscarMembroPorId(registro.memberId)
            if (membro != null) {
                mapaMembros[registro.memberId] = membro
                
                val historicoEstrelas = memberViewModel.buscarHistoricoEstrelas(membro.serverId)
                val temEstrelaNoDia = historicoEstrelas.any { tsEstrela ->
                    val dt1 = Instant.fromEpochMilliseconds(dataChamada).toLocalDateTime(TimeZone.currentSystemDefault())
                    val dt2 = Instant.fromEpochMilliseconds(tsEstrela).toLocalDateTime(TimeZone.currentSystemDefault())
                    dt1.year == dt2.year && dt1.dayOfYear == dt2.dayOfYear
                }
                
                if (temEstrelaNoDia) {
                    idsComEstrela.add(membro.serverId)
                }
            }
        }

        membros = mapaMembros
        membrosComEstrelaNoDia = idsComEstrela
        carregando = false
    }

    ChamaKidsScreen(
        titulo = "DETALHES",
        onVoltar = onVoltar
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            if (carregando) {
                Text(text = "Carregando...")
            } else if (registros.isEmpty()) {
                Text(text = "Nenhum registro encontrado para esta chamada.")
            } else {
                val presentes = registros.count { it.presente }
                val faltas = registros.size - presentes
                val porcentagemPresentes = if (registros.isNotEmpty()) (presentes * 100) / registros.size else 0

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Presentes: $presentes", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        Text(text = "Faltas: $faltas", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    }

                    Box {
                        Text(
                            text = "$porcentagemPresentes%",
                            fontSize = 34.sp,
                            fontWeight = FontWeight.Bold,
                            style = TextStyle(drawStyle = Stroke(width = 3f)),
                            color = Color.Black
                        )
                        Text(
                            text = "$porcentagemPresentes%",
                            fontSize = 34.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF39FF14)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                registros.forEach { registro ->
                    val membro = membros[registro.memberId]
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (membrosComEstrelaNoDia.contains(registro.memberId)) Color(0xFFFFD600) else Color.White
                        )
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(18.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = membro?.nome ?: "Membro não encontrado",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.weight(1f)
                            )
                            Canvas(modifier = Modifier.size(18.dp)) {
                                drawCircle(
                                    color = if (registro.presente) Color(0xFF39FF14) else Color(0xFFFF1744)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
