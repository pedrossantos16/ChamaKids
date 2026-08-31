package com.pedro.ChamaKids.ui

import androidx.compose.foundation.clickable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding

import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import com.pedro.ChamaKids.ui.theme.ChamaKidsAction

import com.pedro.ChamaKids.ui.theme.ChamaKidsBlue

/*
 * MenuScreen
 *
 * Tela central de acesso às funções administrativas
 * do ChamaKids.
 *
 * Futuramente permitirá acessar:
 *
 * - gerenciamento de membros;
 * - lista;
 * - histórico de chamadas;
 * - outras configurações.
 */
@Composable
fun MenuScreen(
    onVoltar: () -> Unit,
    onMembros: () -> Unit,
    onLista: () -> Unit,
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
                    .border(
                        width = 2.dp,
                        color = Color.Black,
                        shape = CircleShape
                    ),
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
                .padding(
                    start = 20.dp,
                    end = 20.dp,
                    top = 40.dp,
                    bottom = 20.dp
                )
        ) {

            // =====================================================
            // MEMBROS
            // =====================================================

            OpcaoMenu(
                titulo = "MEMBROS",
                icone = IconeMenu.MEMBROS,
                onClick = onMembros
            )


            Spacer(
                modifier =
                    Modifier.height(28.dp)
            )


            // =====================================================
            // LISTA
            // =====================================================

            OpcaoMenu(
                titulo = "LISTA",
                icone = IconeMenu.LISTA,
                onClick = onLista
            )


            Spacer(
                modifier =
                    Modifier.height(28.dp)
            )


            // =====================================================
            // HISTÓRICO
            // =====================================================

            OpcaoMenu(
                titulo = "HISTÓRICO",
                icone = IconeMenu.HISTORICO,
                onClick = onHistorico
            )


            Spacer(
                modifier =
                    Modifier.height(28.dp)
            )


            // =====================================================
            // VOLTAR
            // =====================================================

            Spacer(
                modifier =
                    Modifier.weight(1f)
            )

            OpcaoMenu(
                titulo = "VOLTAR",
                icone = IconeMenu.VOLTAR,
                onClick = onVoltar
            )
        }
    }
}

private enum class IconeMenu {
    MEMBROS,
    LISTA,
    HISTORICO,
    VOLTAR
}


/*
 * OpcaoMenu
 *
 * Componente reutilizável para não precisar
 * repetir o código de cada botão/card do menu.
 *
 * Recebe:
 *
 * titulo
 *     texto mostrado ao usuário.
 *
 * onClick
 *     ação executada quando o usuário toca.
 */
@Composable
private fun OpcaoMenu(
    titulo: String,
    icone: IconeMenu,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .border(
                width = 1.5.dp,
                color = Color.Black,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable {
                onClick()
            },

        shape = RoundedCornerShape(8.dp),

        colors = CardDefaults.cardColors(
            containerColor = ChamaKidsAction
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = 18.dp,
                    end = 18.dp
                ),

            verticalAlignment = Alignment.CenterVertically
        ) {

            IconeOpcaoMenu(
                tipo = icone
            )

            Spacer(
                modifier = Modifier.size(16.dp)
            )

            Text(
                text = titulo,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = ">",
                fontSize = 26.sp
            )
        }
    }
}

@Composable
private fun IconeOpcaoMenu(
    tipo: IconeMenu
) {

    Canvas(
        modifier = Modifier.size(28.dp)
    ) {

        val cor = Color.Black
        val largura = 2.dp.toPx()

        when (tipo) {

            IconeMenu.MEMBROS -> {

                // Cabeças
                drawCircle(
                    color = cor,
                    radius = 4.dp.toPx(),
                    center = Offset(
                        10.dp.toPx(),
                        8.dp.toPx()
                    ),
                    style = Stroke(largura)
                )

                drawCircle(
                    color = cor,
                    radius = 3.5.dp.toPx(),
                    center = Offset(
                        19.dp.toPx(),
                        10.dp.toPx()
                    ),
                    style = Stroke(largura)
                )

                // Corpos
                drawArc(
                    color = cor,
                    startAngle = 200f,
                    sweepAngle = 140f,
                    useCenter = false,
                    topLeft = Offset(
                        3.dp.toPx(),
                        13.dp.toPx()
                    ),
                    size = androidx.compose.ui.geometry.Size(
                        14.dp.toPx(),
                        11.dp.toPx()
                    ),
                    style = Stroke(
                        largura,
                        cap = StrokeCap.Round
                    )
                )

                drawArc(
                    color = cor,
                    startAngle = 200f,
                    sweepAngle = 140f,
                    useCenter = false,
                    topLeft = Offset(
                        14.dp.toPx(),
                        15.dp.toPx()
                    ),
                    size = androidx.compose.ui.geometry.Size(
                        11.dp.toPx(),
                        8.dp.toPx()
                    ),
                    style = Stroke(
                        largura,
                        cap = StrokeCap.Round
                    )
                )
            }


            IconeMenu.LISTA -> {

                for (y in listOf(7f, 14f, 21f)) {

                    drawCircle(
                        color = cor,
                        radius = 1.7.dp.toPx(),
                        center = Offset(
                            5.dp.toPx(),
                            y.dp.toPx()
                        )
                    )

                    drawLine(
                        color = cor,
                        start = Offset(
                            10.dp.toPx(),
                            y.dp.toPx()
                        ),
                        end = Offset(
                            24.dp.toPx(),
                            y.dp.toPx()
                        ),
                        strokeWidth = largura,
                        cap = StrokeCap.Round
                    )
                }
            }


            IconeMenu.HISTORICO -> {

                drawCircle(
                    color = cor,
                    radius = 10.dp.toPx(),
                    center = Offset(
                        14.dp.toPx(),
                        14.dp.toPx()
                    ),
                    style = Stroke(largura)
                )

                // Ponteiro vertical
                drawLine(
                    color = cor,
                    start = Offset(
                        14.dp.toPx(),
                        14.dp.toPx()
                    ),
                    end = Offset(
                        14.dp.toPx(),
                        8.dp.toPx()
                    ),
                    strokeWidth = largura,
                    cap = StrokeCap.Round
                )

                // Ponteiro horizontal
                drawLine(
                    color = cor,
                    start = Offset(
                        14.dp.toPx(),
                        14.dp.toPx()
                    ),
                    end = Offset(
                        19.dp.toPx(),
                        14.dp.toPx()
                    ),
                    strokeWidth = largura,
                    cap = StrokeCap.Round
                )
            }


            IconeMenu.VOLTAR -> {

                drawLine(
                    color = cor,
                    start = Offset(
                        23.dp.toPx(),
                        14.dp.toPx()
                    ),
                    end = Offset(
                        6.dp.toPx(),
                        14.dp.toPx()
                    ),
                    strokeWidth = largura,
                    cap = StrokeCap.Round
                )

                drawLine(
                    color = cor,
                    start = Offset(
                        6.dp.toPx(),
                        14.dp.toPx()
                    ),
                    end = Offset(
                        12.dp.toPx(),
                        8.dp.toPx()
                    ),
                    strokeWidth = largura,
                    cap = StrokeCap.Round
                )

                drawLine(
                    color = cor,
                    start = Offset(
                        6.dp.toPx(),
                        14.dp.toPx()
                    ),
                    end = Offset(
                        12.dp.toPx(),
                        20.dp.toPx()
                    ),
                    strokeWidth = largura,
                    cap = StrokeCap.Round
                )
            }
        }
    }
}