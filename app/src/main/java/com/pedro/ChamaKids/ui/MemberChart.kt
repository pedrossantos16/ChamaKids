package com.pedro.ChamaKids.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun GraficoBarrasMensal(frequencias: List<Float?>) {
    val mesesIniciais = listOf("J", "F", "M", "A", "M", "J", "J", "A", "S", "O", "N", "D")
    var mesSelecionado by remember { mutableStateOf<Int?>(null) }
    val scope = rememberCoroutineScope()

    Row(
        modifier = Modifier.fillMaxWidth().height(220.dp).padding(horizontal = 8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxHeight().padding(bottom = 24.dp, end = 8.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.End
        ) {
            listOf("100%", "75%", "50%", "25%", "0%").forEach { pct ->
                Text(text = pct, fontSize = 10.sp, color = Color.Gray)
            }
        }

        Column(modifier = Modifier.weight(1f)) {
            Box(
                modifier = Modifier.fillMaxWidth().weight(1f).pointerInput(Unit) {
                    detectTapGestures { offset ->
                        val larguraColuna = size.width / 12
                        val index = (offset.x / larguraColuna).toInt().coerceIn(0, 11)
                        if (frequencias[index] != null) {
                            mesSelecionado = index
                            scope.launch { delay(2000); if (mesSelecionado == index) mesSelecionado = null }
                        }
                    }
                }
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val altura = size.height
                    val largura = size.width
                    val larguraBarraTotal = largura / 12

                    for (i in 0..4) {
                        val y = (altura / 4) * i
                        drawLine(color = Color.LightGray.copy(alpha = 0.3f), start = Offset(0f, y), end = Offset(largura, y), strokeWidth = 1.dp.toPx())
                    }

                    frequencias.forEachIndexed { index, freq ->
                        if (freq != null) {
                            val status = StatusFrequencia.aPartirDaPorcentagem(freq)
                            val alturaBarra = (freq / 100f) * altura
                            drawRect(
                                color = if (mesSelecionado == index) status.cor.copy(alpha = 0.7f) else status.cor,
                                topLeft = Offset(x = (index * larguraBarraTotal) + (larguraBarraTotal * 0.2f), y = altura - alturaBarra),
                                size = Size(width = larguraBarraTotal * 0.6f, height = alturaBarra)
                            )
                        }
                    }
                }

                mesSelecionado?.let { index ->
                    val freq = frequencias[index]
                    if (freq != null) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopStart) {
                            Text(text = "${freq.toInt()}%", modifier = Modifier.padding(start = (index * 22).dp).background(Color.Black.copy(alpha = 0.8f), RoundedCornerShape(4.dp)).padding(horizontal = 6.dp, vertical = 2.dp), color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                mesesIniciais.forEach { inicial ->
                    Text(text = inicial, modifier = Modifier.weight(1f), textAlign = androidx.compose.ui.text.style.TextAlign.Center, fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun GraficoLinhasEstrelas(historicoEstrelas: List<Long>, ano: Int) {
    val mesesIniciais = listOf("J", "F", "M", "A", "M", "J", "J", "A", "S", "O", "N", "D")
    var mesSelecionado by remember { mutableStateOf<Int?>(null) }
    val scope = rememberCoroutineScope()

    val estrelasPorMes = (0..11).map { mes ->
        historicoEstrelas.count {
            val cal = java.util.Calendar.getInstance().apply { timeInMillis = it }
            cal.get(java.util.Calendar.YEAR) == ano && cal.get(java.util.Calendar.MONTH) == mes
        }
    }

    val maxEstrelas = (estrelasPorMes.maxOrNull() ?: 0).let { if (it < 8) 8 else if (it % 2 == 0) it else it + 1 }
    val escalaY = (maxEstrelas downTo 0 step 2).toList()

    Row(modifier = Modifier.fillMaxWidth().height(220.dp).padding(horizontal = 8.dp)) {
        Column(modifier = Modifier.fillMaxHeight().padding(bottom = 24.dp, end = 8.dp), verticalArrangement = Arrangement.SpaceBetween, horizontalAlignment = Alignment.End) {
            escalaY.forEach { valText -> Text(text = valText.toString(), fontSize = 10.sp, color = Color.Gray) }
        }

        Column(modifier = Modifier.weight(1f)) {
            Box(modifier = Modifier.fillMaxWidth().weight(1f).pointerInput(Unit) {
                detectTapGestures { offset ->
                    val larguraPonto = size.width / 12
                    val index = (offset.x / larguraPonto).toInt().coerceIn(0, 11)
                    mesSelecionado = index
                    scope.launch { delay(2000); if (mesSelecionado == index) mesSelecionado = null }
                }
            }) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val altura = size.height
                    val largura = size.width
                    val larguraSegmento = largura / 12
                    val centroPrimeiro = larguraSegmento / 2

                    // Linhas Horizontais
                    val numLinhas = escalaY.size - 1
                    for (i in 0..numLinhas) {
                        val y = (altura / numLinhas) * i
                        drawLine(color = Color.LightGray.copy(alpha = 0.3f), start = Offset(0f, y), end = Offset(largura, y), strokeWidth = 1.dp.toPx())
                    }

                    // Desenha a linha e os pontos
                    val pontos = estrelasPorMes.mapIndexed { index, count ->
                        Offset(x = centroPrimeiro + (index * larguraSegmento), y = altura - (count.toFloat() / maxEstrelas.toFloat() * altura))
                    }

                    for (i in 0 until pontos.size - 1) {
                        drawLine(color = Color(0xFFFFD600), start = pontos[i], end = pontos[i+1], strokeWidth = 3.dp.toPx(), cap = StrokeCap.Round)
                    }

                    pontos.forEachIndexed { index, point ->
                        drawCircle(color = if (mesSelecionado == index) Color.Black else Color(0xFFFFD600), radius = 5.dp.toPx(), center = point)
                        if (mesSelecionado != index) {
                            drawCircle(color = Color.Black, radius = 5.dp.toPx(), center = point, style = Stroke(1.dp.toPx()))
                        }
                    }
                }

                mesSelecionado?.let { index ->
                    val count = estrelasPorMes[index]
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopStart) {
                        Text(text = "$count ★", modifier = Modifier.padding(start = (index * 22).dp).background(Color(0xFFFFD600), RoundedCornerShape(4.dp)).padding(horizontal = 6.dp, vertical = 2.dp), color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                mesesIniciais.forEach { Text(text = it, modifier = Modifier.weight(1f), textAlign = androidx.compose.ui.text.style.TextAlign.Center, fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Bold) }
            }
        }
    }
}
