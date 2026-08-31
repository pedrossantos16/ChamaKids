package com.pedro.ChamaKids.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding

import androidx.compose.material3.Card
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pedro.ChamaKids.ui.theme.ChamaKidsBlue

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter


@Composable
fun HistoryScreen(
    viewModel: AttendanceViewModel,
    onVoltar: () -> Unit,
    onAbrirChamada: (Int) -> Unit
) {

    val chamadas by
    viewModel.chamadas.collectAsState()

    val formatter =
        DateTimeFormatter.ofPattern(
            "dd/MM/yyyy - HH:mm"
        )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                ChamaKidsBlue
            )
    ) {

        // =================================================
        // CABEÇALHO PADRÃO
        // =================================================

        ChamaKidsHeader(
            titulo = "HISTÓRICO",
            onVoltar = onVoltar
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {

            if (chamadas.isEmpty()) {

                Text(
                    text = "Nenhuma chamada registrada."
                )

            } else {

                chamadas.forEach { chamada ->

                    val data =
                        Instant
                            .ofEpochMilli(
                                chamada.dataHora
                            )
                            .atZone(
                                ZoneId.systemDefault()
                            )
                            .format(
                                formatter
                            )

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable{
                                onAbrirChamada(
                                    chamada.id
                                )
                            }
                    ) {

                        Text(
                            text = data,
                            fontSize = 17.sp,
                            modifier = Modifier.padding(
                                18.dp
                            )
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(
                            12.dp
                        )
                    )
                }
            }
        }
    }
}