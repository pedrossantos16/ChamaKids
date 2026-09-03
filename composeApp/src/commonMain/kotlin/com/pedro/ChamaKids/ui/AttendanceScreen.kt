package com.pedro.ChamaKids.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pedro.ChamaKids.ui.theme.ChamaKidsAction

@Composable
fun AttendanceScreen(
    onVoltar: () -> Unit,
    memberViewModel: MemberViewModel,
    attendanceViewModel: AttendanceViewModel
) {

    val membrosBanco by memberViewModel.membros.collectAsState()
    val frequencias by attendanceViewModel.frequencias.collectAsState()

    var presencas by remember {
        mutableStateOf<Map<Int, Boolean>>(emptyMap())
    }

    var mostrarDialogNome by remember { mutableStateOf(false) }
    var nomeChamada by remember { mutableStateOf("") }

    LaunchedEffect(membrosBanco) {
        presencas = membrosBanco.associate { membro ->
            membro.id to true
        }
        attendanceViewModel.carregarFrequencias(membrosBanco.map { it.id })
    }

    ChamaKidsScreen(
        titulo = "CHAMADA",
        onVoltar = onVoltar
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                membrosBanco.forEach { membro ->
                    val presente = presencas[membro.id] ?: true
                    val frequencia = frequencias[membro.id]

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(92.dp)
                            .padding(bottom = 15.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(Color.LightGray),
                                contentAlignment = Alignment.Center
                            ) {
                                MemberImage(
                                    fotoUri = membro.fotoUri,
                                    modifier = Modifier.fillMaxSize(),
                                    placeholderText = membro.nome.firstOrNull()?.uppercase() ?: "?"
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = membro.nome,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 2
                                )

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    val status = StatusFrequencia.aPartirDaPorcentagem(frequencia)
                                    Canvas(modifier = Modifier.size(12.dp)) {
                                        drawCircle(color = status.cor)
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (presente) "PRESENTE" else "FALTOU",
                                        color = if (presente) Color(0xFF00A381) else Color(0xFFD32F2F),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }

                            Switch(
                                checked = presente,
                                onCheckedChange = { novoEstado ->
                                    presencas = presencas.toMutableMap().apply {
                                        this[membro.id] = novoEstado
                                    }
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            Button(
                onClick = {
                    if (membrosBanco.isNotEmpty()) {
                        mostrarDialogNome = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp, bottom = 40.dp)
                    .height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ChamaKidsAction, contentColor = Color.Black),
                border = BorderStroke(1.5.dp, Color.Black)
            ) {
                Text(text = "SALVAR CHAMADA", fontWeight = FontWeight.Bold)
            }
        }
    }

    if (mostrarDialogNome) {
        AlertDialog(
            onDismissRequest = { mostrarDialogNome = false },
            title = { Text("Nome da Chamada") },
            text = {
                OutlinedTextField(
                    value = nomeChamada,
                    onValueChange = { nomeChamada = it },
                    label = { Text("Ex: Aula de Domingo") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(onClick = {
                    attendanceViewModel.salvarChamada(
                        nome = nomeChamada.ifBlank { null },
                        presencas = presencas,
                        onSucesso = {
                            onVoltar()
                        }
                    )
                    mostrarDialogNome = false
                }) {
                    Text("SALVAR")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogNome = false }) {
                    Text("CANCELAR")
                }
            }
        )
    }
}
