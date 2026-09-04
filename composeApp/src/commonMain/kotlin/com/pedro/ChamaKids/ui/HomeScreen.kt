package com.pedro.ChamaKids.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pedro.ChamaKids.ui.theme.ChamaKidsAction
import com.pedro.ChamaKids.ui.theme.ChamaKidsBlue
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun HomeScreen(
    onMenuClick: () -> Unit
) {
    val yellowStar = Color(0xFFFFD600)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray)
    ) {
        // Apenas 2 Faixas Amarelas nos Cantos Inferiores
        Canvas(modifier = Modifier.fillMaxSize()) {
            val larguraFaixa = 60.dp.toPx()
            
            // Faixa Esquerda
            val pathEsquerdo = Path().apply {
                moveTo(0f, size.height - 120.dp.toPx())
                lineTo(120.dp.toPx(), size.height)
                lineTo(60.dp.toPx(), size.height)
                lineTo(0f, size.height - 60.dp.toPx())
                close()
            }
            drawPath(pathEsquerdo, yellowStar)

            // Faixa Direita
            val pathDireito = Path().apply {
                moveTo(size.width, size.height - 120.dp.toPx())
                lineTo(size.width - 120.dp.toPx(), size.height)
                lineTo(size.width - 60.dp.toPx(), size.height)
                lineTo(size.width, size.height - 60.dp.toPx())
                close()
            }
            drawPath(pathDireito, yellowStar)
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(80.dp))

            // Estrela Superior (Proporção do desenho)
            Canvas(modifier = Modifier.size(120.dp)) {
                val centro = Offset(size.width / 2, size.height / 2)
                val pontos = 5
                val raioExterno = size.width / 2
                val raioInterno = raioExterno / 2.5f
                val path = Path()
                
                for (i in 0 until pontos * 2) {
                    val raio = if (i % 2 == 0) raioExterno else raioInterno
                    val angulo = PI * i / pontos - PI / 2
                    val x = centro.x + (raio * cos(angulo)).toFloat()
                    val y = centro.y + (raio * sin(angulo)).toFloat()
                    if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                }
                path.close()
                drawPath(path, yellowStar)
            }

            Spacer(modifier = Modifier.height(60.dp))

            // Título Chama Kids com Faixa Azul e Detalhes Brancos
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(0.dp)) // Garante o recorte nas bordas
                    .background(ChamaKidsBlue)
                    .border(width = 2.dp, color = Color.Black),
                contentAlignment = Alignment.Center
            ) {
                // Faixas Brancas em Ângulo (Fundo da faixa azul)
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val larguraFaixaBranca = 20.dp.toPx()
                    val espacamento = 60.dp.toPx()
                    val corBranca = Color.White.copy(alpha = 0.3f) // Branco suave para não ofuscar o texto

                    for (i in -10..20) {
                        val xStart = i * espacamento
                        drawLine(
                            color = corBranca,
                            start = Offset(xStart, 0f),
                            end = Offset(xStart + 100.dp.toPx(), size.height),
                            strokeWidth = larguraFaixaBranca
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.offset(x = (-40).dp)) {
                        Text(
                            text = "Chama",
                            fontSize = 72.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontStyle = FontStyle.Italic,
                            color = Color.White,
                            style = TextStyle(drawStyle = Stroke(width = 8f))
                        )
                        Text(
                            text = "Chama",
                            fontSize = 72.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontStyle = FontStyle.Italic,
                            color = Color.Black
                        )
                    }
                    
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.offset(x = 40.dp, y = (-5).dp)) {
                        Text(
                            text = "Kids",
                            fontSize = 72.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontStyle = FontStyle.Italic,
                            color = Color.White,
                            style = TextStyle(drawStyle = Stroke(width = 8f))
                        )
                        Text(
                            text = "Kids",
                            fontSize = 72.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontStyle = FontStyle.Italic,
                            color = Color.Black
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(80.dp))

            // Botão Avançar (Proporção do desenho)
            Box(
                modifier = Modifier
                    .size(width = 150.dp, height = 85.dp)
                    .clip(RoundedCornerShape(25.dp))
                    .background(ChamaKidsAction)
                    .border(3.dp, Color.Black, RoundedCornerShape(25.dp))
                    .clickable { onMenuClick() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = ">",
                    fontSize = 54.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}
