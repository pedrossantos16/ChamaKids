package com.pedro.ChamaKids.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import com.pedro.ChamaKids.ui.theme.ChamaKidsBlack
import com.pedro.ChamaKids.ui.theme.ChamaKidsBlue
import com.pedro.ChamaKids.ui.theme.ChamaKidsHeaderGray
import com.pedro.ChamaKids.ui.theme.ChamaKidsMenu

@Composable
fun ChamaKidsHeader(
    titulo: String? = null,
    onVoltar: () -> Unit,
    mostrarVoltar: Boolean = true,
    conteudoCentral: (@Composable () -> Unit)? = null,
    acoesDireita: (@Composable RowScope.() -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(ChamaKidsHeaderGray)
            .statusBarsPadding()
            .drawBehind {
                val strokeWidth = 5.dp.toPx()
                drawLine(
                    color = Color.Black,
                    start = androidx.compose.ui.geometry.Offset(0f, size.height - strokeWidth / 2),
                    end = androidx.compose.ui.geometry.Offset(size.width, size.height - strokeWidth / 2),
                    strokeWidth = strokeWidth
                )
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // VOLTAR
            if (mostrarVoltar) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clickable { onVoltar() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "←", fontSize = 35.sp, fontWeight = FontWeight.Bold)
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))

            // AÇÕES DIREITA
            if (acoesDireita != null) {
                acoesDireita()
            }
        }

        // TÍTULO CENTRAL
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp),
            contentAlignment = Alignment.Center
        ) {
            if (conteudoCentral != null) {
                conteudoCentral()
            } else if (titulo != null) {
                Text(
                    text = titulo,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = ChamaKidsBlack,
                    style = TextStyle(drawStyle = Stroke(width = 3f))
                )
                Text(
                    text = titulo,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = ChamaKidsMenu
                )
            }
        }
    }
}

@Composable
fun ChamaKidsScreen(
    titulo: String? = null,
    onVoltar: () -> Unit,
    mostrarVoltar: Boolean = true,
    conteudoCentral: (@Composable () -> Unit)? = null,
    acoesDireita: (@Composable RowScope.() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ChamaKidsBlue)
    ) {
        ChamaKidsHeader(
            titulo = titulo,
            onVoltar = onVoltar,
            mostrarVoltar = mostrarVoltar,
            conteudoCentral = conteudoCentral,
            acoesDireita = acoesDireita
        )

        Box(modifier = Modifier.fillMaxSize()) {
            content()
        }
    }
}
