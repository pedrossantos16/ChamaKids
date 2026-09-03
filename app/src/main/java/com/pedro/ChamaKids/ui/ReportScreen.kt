package com.pedro.ChamaKids.ui

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pedro.ChamaKids.FileUtils
import com.pedro.ChamaKids.PdfGenerator
import com.pedro.ChamaKids.data.MemberWithStarStats
import com.pedro.ChamaKids.data.MemberWithStats
import com.pedro.ChamaKids.data.PeriodStat
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ReportScreen(
    onVoltar: () -> Unit,
    memberViewModel: MemberViewModel,
    attendanceViewModel: AttendanceViewModel
) {
    val context = LocalContext.current
    
    var dataSelecionada by remember { mutableStateOf(Calendar.getInstance()) }
    var mostrarSeletor by remember { mutableStateOf(false) }

    var childMostPresent by remember { mutableStateOf<MemberWithStats?>(null) }
    var childBestBehavior by remember { mutableStateOf<MemberWithStarStats?>(null) }
    var dayHighestAttendance by remember { mutableStateOf<PeriodStat?>(null) }
    var dayMostStars by remember { mutableStateOf<PeriodStat?>(null) }
    var carregando by remember { mutableStateOf(true) }

    var arquivoPdfPrevia by remember { mutableStateOf<java.io.File?>(null) }
    var mostrarPrevia by remember { mutableStateOf(false) }

    LaunchedEffect(dataSelecionada) {
        carregando = true
        val cal = dataSelecionada.clone() as Calendar
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        val inicio = cal.timeInMillis

        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        val fim = cal.timeInMillis

        childMostPresent = attendanceViewModel.buscarMembroMaisPresente(inicio, fim)
        childBestBehavior = memberViewModel.buscarMembroMaisEstrelas(inicio, fim)
        dayHighestAttendance = attendanceViewModel.buscarDiaMaiorAssiduidade(inicio, fim)
        dayMostStars = memberViewModel.buscarDiaMaisEstrelas(inicio, fim)
        
        carregando = false
    }

    ChamaKidsScreen(
        titulo = "RELATÓRIO",
        onVoltar = onVoltar,
        acoesDireita = {
            IconButton(onClick = {
                val mesAnoStr = SimpleDateFormat("MMMM yyyy", Locale("pt", "BR"))
                    .format(dataSelecionada.time).uppercase()
                arquivoPdfPrevia = PdfGenerator.gerarRelatorioComoArquivo(
                    context = context,
                    mesAno = mesAnoStr,
                    membroMaisPresente = childMostPresent,
                    melhorComportamento = childBestBehavior,
                    diaMaiorAssiduidade = dayHighestAttendance,
                    diaMaisEstrelas = dayMostStars
                )
                if (arquivoPdfPrevia != null) {
                    mostrarPrevia = true
                }
            }) {
                Canvas(modifier = Modifier.size(28.dp)) {
                    val cor = Color.Black
                    val largura = 2.dp.toPx()
                    drawRect(
                        color = cor,
                        topLeft = Offset(4.dp.toPx(), 2.dp.toPx()),
                        size = Size(20.dp.toPx(), 24.dp.toPx()),
                        style = androidx.compose.ui.graphics.drawscope.Stroke(largura)
                    )
                    drawLine(cor, Offset(8.dp.toPx(), 10.dp.toPx()), Offset(20.dp.toPx(), 10.dp.toPx()), largura)
                    drawLine(cor, Offset(8.dp.toPx(), 15.dp.toPx()), Offset(20.dp.toPx(), 15.dp.toPx()), largura)
                    drawLine(cor, Offset(8.dp.toPx(), 20.dp.toPx()), Offset(14.dp.toPx(), 20.dp.toPx()), largura)
                }
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .clickable { mostrarSeletor = true },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = SimpleDateFormat("MMMM yyyy", Locale("pt", "BR"))
                            .format(dataSelecionada.time).uppercase(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "▼", fontSize = 12.sp, color = Color.Gray)
                }
            }

            if (carregando) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.Black)
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    SecaoRelatorio(titulo = "CRIANÇA MAIS PRESENTE") {
                        if (childMostPresent != null) {
                            InfoMembroRelatorio(
                                nome = childMostPresent!!.nome,
                                fotoUri = childMostPresent!!.fotoUri,
                                subtitulo = "${childMostPresent!!.count} presenças no mês"
                            )
                        } else {
                            Text("Sem dados de presença este mês.", color = Color.Gray)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    SecaoRelatorio(titulo = "MELHOR COMPORTAMENTO") {
                        if (childBestBehavior != null) {
                            Column {
                                InfoMembroRelatorio(
                                    nome = childBestBehavior!!.nome,
                                    fotoUri = childBestBehavior!!.fotoUri,
                                    subtitulo = "${childBestBehavior!!.count} estrelas recebidas ★"
                                )
                                if (!childBestBehavior!!.comentario.isNullOrBlank()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "\"${childBestBehavior!!.comentario}\"",
                                        fontSize = 13.sp,
                                        color = Color.DarkGray,
                                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                    )
                                }
                            }
                        } else {
                            Text("Sem estrelas distribuídas este mês.", color = Color.Gray)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    SecaoRelatorio(titulo = "DIA COM MAIS CRIANÇAS") {
                        if (dayHighestAttendance != null) {
                            Column {
                                Text(
                                    text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(dayHighestAttendance!!.timestamp),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = "${dayHighestAttendance!!.count} crianças presentes",
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                            }
                        } else {
                            Text("Sem chamadas este mês.", color = Color.Gray)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    SecaoRelatorio(titulo = "DIA COM MAIS ESTRELAS") {
                        if (dayMostStars != null) {
                            Column {
                                Text(
                                    text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(dayMostStars!!.timestamp),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = "${dayMostStars!!.count} estrelas distribuídas ★",
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                            }
                        } else {
                            Text("Sem estrelas este mês.", color = Color.Gray)
                        }
                    }

                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }
    }

    if (mostrarPrevia && arquivoPdfPrevia != null) {
        AlertDialog(
            onDismissRequest = { mostrarPrevia = false },
            title = { Text("Prévia do Relatório") },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("O relatório foi gerado com sucesso!")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Mês: ${SimpleDateFormat("MMMM yyyy", Locale("pt", "BR")).format(dataSelecionada.time).uppercase()}", fontSize = 14.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Deseja baixar o arquivo PDF agora?", fontWeight = FontWeight.Bold)
                }
            },
            confirmButton = {
                Button(onClick = {
                    PdfGenerator.salvarPdfDefinitivo(
                        context = context,
                        arquivoCache = arquivoPdfPrevia!!,
                        mesAno = SimpleDateFormat("MMMM yyyy", Locale("pt", "BR")).format(dataSelecionada.time).uppercase()
                    )
                    mostrarPrevia = false
                }) {
                    Text("BAIXAR PDF")
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    FileUtils.excluirArquivo(arquivoPdfPrevia?.absolutePath)
                    mostrarPrevia = false 
                }) {
                    Text("CANCELAR")
                }
            }
        )
    }

    if (mostrarSeletor) {
        SeletorMesDialog(
            mesAtual = dataSelecionada.get(Calendar.MONTH),
            anoAtual = dataSelecionada.get(Calendar.YEAR),
            onDismiss = { mostrarSeletor = false },
            onConfirm = { mes, ano ->
                val novaData = Calendar.getInstance().apply {
                    set(Calendar.YEAR, ano)
                    set(Calendar.MONTH, mes)
                }
                dataSelecionada = novaData
                mostrarSeletor = false
            }
        )
    }
}

@Composable
private fun SecaoRelatorio(titulo: String, conteudo: @Composable () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFE0E0E0), RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                .padding(vertical = 10.dp, horizontal = 16.dp)
        ) {
            Text(text = titulo, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp))
                .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp))
                .padding(16.dp)
        ) {
            conteudo()
        }
    }
}

@Composable
private fun InfoMembroRelatorio(nome: String, fotoUri: String?, subtitulo: String) {
    val context = LocalContext.current
    val bitmap = remember(fotoUri) {
        if (fotoUri.isNullOrBlank()) null
        else {
            try {
                context.contentResolver.openInputStream(Uri.parse(fotoUri))?.use {
                    BitmapFactory.decodeStream(it)
                }
            } catch (_: Exception) { null }
        }
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(Color(0xFFF1F1F5)),
            contentAlignment = Alignment.Center
        ) {
            if (bitmap != null) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Text(nome.first().uppercase(), fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = nome, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(text = subtitulo, fontSize = 14.sp, color = Color.Gray)
        }
    }
}
