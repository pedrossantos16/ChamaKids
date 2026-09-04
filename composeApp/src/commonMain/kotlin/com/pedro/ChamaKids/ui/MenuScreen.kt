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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pedro.ChamaKids.ui.theme.ChamaKidsAction
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun MenuScreen(
    onVoltar: () -> Unit,
    onChamada: () -> Unit,
    onMembros: () -> Unit,
    onLista: () -> Unit,
    onClassificar: () -> Unit,
    onRanking: () -> Unit,
    onRelatorio: () -> Unit,
    onGuia: () -> Unit,
    onHistorico: () -> Unit
) {
    ChamaKidsScreen(
        titulo = null,
        onVoltar = onVoltar,
        conteudoCentral = {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1C9997))
                    .border(2.dp, Color.Black, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "!",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(start = 20.dp, end = 20.dp, top = 40.dp, bottom = 40.dp)
        ) {
            OpcaoMenu("CHAMADA", IconeMenu.CHAMADA, onChamada)
            Spacer(modifier = Modifier.height(24.dp))
            OpcaoMenu("MEMBROS", IconeMenu.MEMBROS, onMembros)
            Spacer(modifier = Modifier.height(24.dp))
            OpcaoMenu("LISTA", IconeMenu.LISTA, onLista)
            Spacer(modifier = Modifier.height(24.dp))
            OpcaoMenu("CLASSIFICAR", IconeMenu.CLASSIFICAR, onClassificar)
            Spacer(modifier = Modifier.height(24.dp))
            OpcaoMenu("RANKING", IconeMenu.RANKING, onRanking)
            Spacer(modifier = Modifier.height(24.dp))
            OpcaoMenu("RELATÓRIO", IconeMenu.RELATORIO, onRelatorio)
            Spacer(modifier = Modifier.height(24.dp))
            OpcaoMenu("GUIA", IconeMenu.GUIA, onGuia)
            Spacer(modifier = Modifier.height(24.dp))
            OpcaoMenu("HISTÓRICO", IconeMenu.HISTORICO, onHistorico)
        }
    }
}

private enum class IconeMenu {
    CHAMADA, MEMBROS, LISTA, CLASSIFICAR, RANKING, RELATORIO, GUIA, HISTORICO
}

@Composable
private fun OpcaoMenu(titulo: String, icone: IconeMenu, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .border(1.5.dp, Color.Black, RoundedCornerShape(8.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = ChamaKidsAction)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconeOpcaoMenu(tipo = icone)
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = titulo, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
            Text(text = ">", fontSize = 26.sp)
        }
    }
}

@Composable
private fun IconeOpcaoMenu(tipo: IconeMenu) {
    Canvas(modifier = Modifier.size(28.dp)) {
        val cor = Color.Black
        val largura = 2.dp.toPx()
        when (tipo) {
            IconeMenu.CHAMADA -> {
                drawCircle(cor, radius = 10.dp.toPx(), center = Offset(14.dp.toPx(), 14.dp.toPx()), style = Stroke(largura))
                drawLine(cor, Offset(14.dp.toPx(), 9.dp.toPx()), Offset(14.dp.toPx(), 19.dp.toPx()), largura)
                drawLine(cor, Offset(9.dp.toPx(), 14.dp.toPx()), Offset(19.dp.toPx(), 14.dp.toPx()), largura)
            }
            IconeMenu.MEMBROS -> {
                drawCircle(cor, 4.dp.toPx(), Offset(10.dp.toPx(), 8.dp.toPx()), style = Stroke(largura))
                drawCircle(cor, 3.5.dp.toPx(), Offset(19.dp.toPx(), 10.dp.toPx()), style = Stroke(largura))
                drawArc(cor, 200f, 140f, false, Offset(3.dp.toPx(), 13.dp.toPx()), androidx.compose.ui.geometry.Size(14.dp.toPx(), 11.dp.toPx()), style = Stroke(largura, cap = StrokeCap.Round))
                drawArc(cor, 200f, 140f, false, Offset(14.dp.toPx(), 15.dp.toPx()), androidx.compose.ui.geometry.Size(11.dp.toPx(), 8.dp.toPx()), style = Stroke(largura, cap = StrokeCap.Round))
            }
            IconeMenu.LISTA -> {
                for (y in listOf(7f, 14f, 21f)) {
                    drawCircle(cor, 1.7.dp.toPx(), Offset(5.dp.toPx(), y.dp.toPx()))
                    drawLine(cor, Offset(10.dp.toPx(), y.dp.toPx()), Offset(24.dp.toPx(), y.dp.toPx()), largura, StrokeCap.Round)
                }
            }
            IconeMenu.CLASSIFICAR -> {
                val center = Offset(14.dp.toPx(), 14.dp.toPx())
                val radius = 10.dp.toPx()
                val path = Path().apply {
                    val points = 5
                    val innerRadius = radius / 2.5f
                    for (i in 0 until points * 2) {
                        val angle = PI * i / points - PI / 2
                        val r = if (i % 2 == 0) radius else innerRadius
                        val x = center.x + (r * cos(angle)).toFloat()
                        val y = center.y + (r * sin(angle)).toFloat()
                        if (i == 0) moveTo(x, y) else lineTo(x, y)
                    }
                    close()
                }
                drawPath(path, cor, style = Stroke(largura))
            }
            IconeMenu.RANKING -> {
                drawLine(cor, Offset(5.dp.toPx(), 22.dp.toPx()), Offset(5.dp.toPx(), 12.dp.toPx()), largura, StrokeCap.Round)
                drawLine(cor, Offset(14.dp.toPx(), 22.dp.toPx()), Offset(14.dp.toPx(), 5.dp.toPx()), largura, StrokeCap.Round)
                drawLine(cor, Offset(23.dp.toPx(), 22.dp.toPx()), Offset(23.dp.toPx(), 16.dp.toPx()), largura, StrokeCap.Round)
                drawLine(cor, Offset(3.dp.toPx(), 23.dp.toPx()), Offset(25.dp.toPx(), 23.dp.toPx()), largura, StrokeCap.Round)
            }
            IconeMenu.RELATORIO -> {
                drawRect(cor, Offset(6.dp.toPx(), 4.dp.toPx()), androidx.compose.ui.geometry.Size(16.dp.toPx(), 20.dp.toPx()), style = Stroke(largura))
                drawLine(cor, Offset(9.dp.toPx(), 9.dp.toPx()), Offset(19.dp.toPx(), 9.dp.toPx()), largura)
                drawLine(cor, Offset(9.dp.toPx(), 14.dp.toPx()), Offset(19.dp.toPx(), 14.dp.toPx()), largura)
                drawLine(cor, Offset(9.dp.toPx(), 19.dp.toPx()), Offset(15.dp.toPx(), 19.dp.toPx()), largura)
            }
            IconeMenu.GUIA -> {
                drawCircle(cor, radius = 10.dp.toPx(), center = Offset(14.dp.toPx(), 14.dp.toPx()), style = Stroke(largura))
                drawCircle(cor, radius = 1.5.dp.toPx(), center = Offset(14.dp.toPx(), 20.dp.toPx()))
                drawArc(cor, startAngle = 140f, sweepAngle = 260f, useCenter = false, topLeft = Offset(10.dp.toPx(), 8.dp.toPx()), size = androidx.compose.ui.geometry.Size(8.dp.toPx(), 8.dp.toPx()), style = Stroke(largura, cap = StrokeCap.Round))
            }
            IconeMenu.HISTORICO -> {
                drawCircle(cor, 10.dp.toPx(), Offset(14.dp.toPx(), 14.dp.toPx()), style = Stroke(largura))
                drawLine(cor, Offset(14.dp.toPx(), 14.dp.toPx()), Offset(14.dp.toPx(), 8.dp.toPx()), largura, StrokeCap.Round)
                drawLine(cor, Offset(14.dp.toPx(), 14.dp.toPx()), Offset(19.dp.toPx(), 14.dp.toPx()), largura, StrokeCap.Round)
            }
        }
    }
}
