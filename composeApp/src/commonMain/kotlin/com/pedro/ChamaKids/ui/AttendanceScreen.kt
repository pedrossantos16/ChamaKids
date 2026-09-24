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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pedro.ChamaKids.ui.theme.ChamaKidsAction
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceScreen(
    onVoltar: () -> Unit,
    memberViewModel: MemberViewModel,
    attendanceViewModel: AttendanceViewModel,
    userViewModel: UserViewModel
) {

    val membrosBanco by memberViewModel.membros.collectAsState()
    val frequencias by attendanceViewModel.frequencias.collectAsState()
    val currentUser by userViewModel.currentUser.collectAsState()

    var presencas by remember {
        mutableStateOf<Map<String, Boolean>>(emptyMap())
    }

    var mostrarDialogNome by remember { mutableStateOf(false) }
    var nomeChamada by remember { mutableStateOf("") }
    var mostrarCalendario by remember { mutableStateOf(false) }
    var dataSelecionadaMillis by remember { mutableLongStateOf(Clock.System.now().toEpochMilliseconds()) }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = dataSelecionadaMillis)

    LaunchedEffect(membrosBanco) {
        presencas = membrosBanco.associate { membro ->
            membro.serverId to true
        }
        attendanceViewModel.carregarFrequencias(membrosBanco.map { it.serverId })
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
                    val presente = presencas[membro.serverId] ?: true
                    val frequencia = frequencias[membro.serverId]

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
                                        this[membro.serverId] = novoEstado
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
        val zdt = Instant.fromEpochMilliseconds(dataSelecionadaMillis).toLocalDateTime(TimeZone.currentSystemDefault())
        val dataFormatada = "${zdt.dayOfMonth.toString().padStart(2, '0')}/${zdt.monthNumber.toString().padStart(2, '0')}/${zdt.year}"

        AlertDialog(
            onDismissRequest = { mostrarDialogNome = false },
            title = { Text("Salvar Chamada") },
            text = {
                Column {
                    OutlinedTextField(
                        value = nomeChamada,
                        onValueChange = { nomeChamada = it },
                        label = { Text("Nome da chamada (opcional)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "Data da Chamada:", fontSize = 12.sp, color = Color.Gray)
                    OutlinedButton(
                        onClick = { mostrarCalendario = true },
                        modifier = Modifier.fillMaxWidth().height(50.dp).padding(top = 4.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("📅 Data: $dataFormatada", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        attendanceViewModel.salvarChamada(
                            nome = nomeChamada.ifBlank { null },
                            presencas = presencas,
                            criadoPor = currentUser?.nome,
                            dataHora = dataSelecionadaMillis,
                            onSucesso = {
                                onVoltar()
                            }
                        )
                        mostrarDialogNome = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ChamaKidsAction, contentColor = Color.Black)
                ) {
                    Text("SALVAR", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogNome = false }) {
                    Text("CANCELAR")
                }
            }
        )
    }

    if (mostrarCalendario) {
        DatePickerDialog(
            onDismissRequest = { mostrarCalendario = false },
            confirmButton = {
                TextButton(onClick = {
                    val millis = datePickerState.selectedDateMillis
                    if (millis != null) {
                        dataSelecionadaMillis = adjustPickerDateToLocalMillis(millis)
                    }
                    mostrarCalendario = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarCalendario = false }) { Text("CANCELAR") }
            }
        ) { DatePicker(state = datePickerState) }
    }
}
