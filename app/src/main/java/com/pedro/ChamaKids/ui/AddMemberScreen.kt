package com.pedro.ChamaKids.ui

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pedro.ChamaKids.FileUtils
import com.pedro.ChamaKids.data.MemberEntity
import com.pedro.ChamaKids.ui.theme.ChamaKidsAction
import com.pedro.ChamaKids.ui.theme.ChamaKidsCard
import java.io.File
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMemberScreen(
    onVoltar: () -> Unit,
    viewModel: MemberViewModel
) {
    var nome by remember { mutableStateOf("") }
    var cpf by remember { mutableStateOf("") }
    var rg by remember { mutableStateOf("") }
    var endereco by remember { mutableStateOf("") }
    var celularMembro by remember { mutableStateOf("") }
    var telefone by remember { mutableStateOf("") }
    var nomePai by remember { mutableStateOf("") }
    var celularPai by remember { mutableStateOf("") }
    var nomeMae by remember { mutableStateOf("") }
    var celularMae by remember { mutableStateOf("") }
    var dataNascimento by remember { mutableStateOf<LocalDate?>(null) }
    var fotoUri by remember { mutableStateOf<Uri?>(null) }

    val context = LocalContext.current
    val seletorFoto = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            val caminhoLocal = FileUtils.salvarFotoInterna(context, uri)
            if (caminhoLocal != null) {
                // Se já tinha selecionado uma foto antes nesta sessão, apaga a anterior
                if (fotoUri != null) {
                    FileUtils.excluirArquivo(fotoUri?.path)
                }
                fotoUri = Uri.fromFile(File(caminhoLocal))
            }
        }
    }

    var mostrarCalendario by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    ChamaKidsScreen(
        titulo = "CADASTRO",
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

                // FOTO
                val bitmapFoto = remember(fotoUri) {
                    if (fotoUri == null) null
                    else {
                        try {
                            context.contentResolver.openInputStream(fotoUri!!)?.use {
                                BitmapFactory.decodeStream(it)
                            }
                        } catch (_: Exception) { null }
                    }
                }

                Box(
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier.size(180.dp).clickable { seletorFoto.launch("image/*") },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            if (bitmapFoto != null) {
                                Image(
                                    bitmap = bitmapFoto.asImageBitmap(),
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                                Box(
                                    modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.35f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = "ALTERAR FOTO", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            } else {
                                Text(text = "+ FOTO")
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                CampoFicha(valor = nome, titulo = "Nome completo", habilitado = true) { nome = it }
                Spacer(modifier = Modifier.height(12.dp))

                CampoFicha(
                    valor = cpf,
                    titulo = "CPF",
                    habilitado = true,
                    keyboardType = KeyboardType.Number,
                    visualTransformation = CpfVisualTransformation()
                ) { valor ->
                    cpf = valor.filter { it.isDigit() }.take(11)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    CampoFicha(
                        valor = rg,
                        titulo = "RG",
                        habilitado = true,
                        modifier = Modifier.weight(1f),
                        keyboardType = KeyboardType.Number,
                        visualTransformation = RgVisualTransformation()
                    ) { valor ->
                        rg = valor.filter { it.isDigit() }.take(9)
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    OutlinedButton(
                        onClick = { mostrarCalendario = true },
                        modifier = Modifier.weight(1f).height(56.dp).padding(top = 4.dp),
                        shape = RoundedCornerShape(4.dp),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White, contentColor = Color(0xFF555555)),
                        contentPadding = PaddingValues(horizontal = 12.dp)
                    ) {
                        Text(
                            text = dataNascimento?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) ?: "Nascimento",
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                CampoFicha(valor = endereco, titulo = "Endereço completo", habilitado = true) { endereco = it }
                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    CampoFicha(
                        valor = celularMembro,
                        titulo = "Cel. da criança",
                        habilitado = true,
                        modifier = Modifier.weight(1f),
                        keyboardType = KeyboardType.Number,
                        visualTransformation = CellphoneVisualTransformation()
                    ) { valor ->
                        celularMembro = valor.filter { it.isDigit() }.take(11)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    CampoFicha(
                        valor = telefone,
                        titulo = "Telefone",
                        habilitado = true,
                        modifier = Modifier.weight(1f),
                        keyboardType = KeyboardType.Number,
                        visualTransformation = PhoneVisualTransformation()
                    ) { valor ->
                        telefone = valor.filter { it.isDigit() }.take(10)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    CampoFicha(valor = nomePai, titulo = "Nome do pai", habilitado = true, modifier = Modifier.weight(1f)) { nomePai = it }
                    Spacer(modifier = Modifier.width(10.dp))
                    CampoFicha(
                        valor = celularPai,
                        titulo = "Cel. do pai",
                        habilitado = true,
                        modifier = Modifier.weight(1f),
                        keyboardType = KeyboardType.Number,
                        visualTransformation = CellphoneVisualTransformation()
                    ) { valor ->
                        celularPai = valor.filter { it.isDigit() }.take(11)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    CampoFicha(valor = nomeMae, titulo = "Nome da mãe", habilitado = true, modifier = Modifier.weight(1f)) { nomeMae = it }
                    Spacer(modifier = Modifier.width(10.dp))
                    CampoFicha(
                        valor = celularMae,
                        titulo = "Cel. da mãe",
                        habilitado = true,
                        modifier = Modifier.weight(1f),
                        keyboardType = KeyboardType.Number,
                        visualTransformation = CellphoneVisualTransformation()
                    ) { valor ->
                        celularMae = valor.filter { it.isDigit() }.take(11)
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

                OutlinedButton(
                    onClick = {
                        // Se tinha foto, apaga o arquivo físico
                        if (fotoUri != null) {
                            FileUtils.excluirArquivo(fotoUri?.path)
                        }

                        nome = ""; cpf = ""; rg = ""; endereco = ""; celularMembro = ""; telefone = ""
                        nomePai = ""; celularPai = ""; nomeMae = ""; celularMae = ""
                        dataNascimento = null; fotoUri = null
                    },
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ChamaKidsCard, contentColor = Color.Black),
                    border = BorderStroke(1.5.dp, Color.Black)
                ) {
                    Text(text = "LIMPAR INFORMAÇÕES")
                }
                
                Spacer(modifier = Modifier.height(20.dp))
            }

            // BOTÃO FIXO
            Button(
                onClick = {
                    if (nome.isNotBlank()) {
                        val novoMembro = MemberEntity(
                            nome = nome.trim(),
                            cpf = cpf,
                            rg = rg,
                            dataNascimento = dataNascimento?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                            endereco = endereco,
                            celularMembro = celularMembro,
                            telefone = telefone,
                            nomePai = nomePai,
                            celularPai = celularPai,
                            nomeMae = nomeMae,
                            celularMae = celularMae,
                            fotoUri = fotoUri?.toString(),
                            ativo = true
                        )
                        viewModel.adicionarMembro(membro = novoMembro) { onVoltar() }
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(top = 20.dp, bottom = 45.dp).height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ChamaKidsAction, contentColor = Color.Black),
                border = BorderStroke(1.5.dp, Color.Black)
            ) {
                Text(text = "ADICIONAR MEMBRO", fontWeight = FontWeight.SemiBold)
            }
        }
    }

    if (mostrarCalendario) {
        DatePickerDialog(
            onDismissRequest = { mostrarCalendario = false },
            confirmButton = {
                TextButton(onClick = {
                    val millis = datePickerState.selectedDateMillis
                    if (millis != null) {
                        dataNascimento = Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate()
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
