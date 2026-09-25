package com.pedro.ChamaKids.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pedro.ChamaKids.data.FirebaseSyncManager
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
    val syncing by FirebaseSyncManager.syncing.collectAsState()
    val isOnline by FirebaseSyncManager.isOnline.collectAsState()
    val syncError by FirebaseSyncManager.errorMessage.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(ChamaKidsHeaderGray)
            .statusBarsPadding()
            .drawBehind {
                val strokeWidth = 5.dp.toPx()
                drawLine(
                    color = Color.Black,
                    start = Offset(0f, size.height - strokeWidth / 2),
                    end = Offset(size.width, size.height - strokeWidth / 2),
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

            // ÁREA DE ÍCONES DE STATUS E AÇÕES NO CANTO SUPERIOR DIREITO
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // 1. ANIMAÇÃO DE CARREGAMENTO CIRCULAR (Durante sincronização/processamento local e em nuvem)
                if (syncing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.5.dp
                    )
                }

                // 2. SÍMBOLO DE WI-FI CORTADO (Se a conexão falhar ou houver desconexão com a nuvem)
                if (!isOnline || syncError != null) {
                    IconeWifiCortado(
                        modifier = Modifier.size(26.dp),
                        color = Color(0xFFFF5252)
                    )
                }

                // Ações específicas de cada tela
                if (acoesDireita != null) {
                    acoesDireita()
                }
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
fun IconeWifiCortado(modifier: Modifier = Modifier, color: Color = Color(0xFFFF5252)) {
    Canvas(modifier = modifier) {
        val largura = 2.dp.toPx()
        val centroX = size.width / 2
        val centroY = size.height / 2 + 2.dp.toPx()

        // Arcos do Wi-Fi
        drawArc(
            color = color,
            startAngle = 220f,
            sweepAngle = 100f,
            useCenter = false,
            topLeft = Offset(centroX - 10.dp.toPx(), centroY - 10.dp.toPx()),
            size = Size(20.dp.toPx(), 20.dp.toPx()),
            style = Stroke(largura, cap = StrokeCap.Round)
        )
        drawArc(
            color = color,
            startAngle = 230f,
            sweepAngle = 80f,
            useCenter = false,
            topLeft = Offset(centroX - 6.dp.toPx(), centroY - 6.dp.toPx()),
            size = Size(12.dp.toPx(), 12.dp.toPx()),
            style = Stroke(largura, cap = StrokeCap.Round)
        )
        drawCircle(
            color = color,
            radius = 1.8.dp.toPx(),
            center = Offset(centroX, centroY + 4.dp.toPx())
        )

        // Traço Vermelho Cortando (Wi-Fi Indisponível)
        drawLine(
            color = color,
            start = Offset(2.dp.toPx(), size.height - 2.dp.toPx()),
            end = Offset(size.width - 2.dp.toPx(), 2.dp.toPx()),
            strokeWidth = 2.5.dp.toPx(),
            cap = StrokeCap.Round
        )
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

@Composable
fun CampoFicha(
    valor: String,
    titulo: String,
    habilitado: Boolean,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onChange: (String) -> Unit
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onChange,
        enabled = habilitado,
        label = { Text(titulo) },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = visualTransformation,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            disabledContainerColor = Color(0xFFE2E2E2),
            disabledTextColor = Color(0xFF666666),
            disabledBorderColor = Color(0xFFAAAAAA)
        ),
        modifier = modifier.fillMaxWidth()
    )
}
