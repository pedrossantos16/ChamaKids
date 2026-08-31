package com.pedro.ChamaKids.ui

import android.net.Uri

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll

import androidx.compose.material3.*

import androidx.compose.runtime.*

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import java.time.Instant
import java.time.ZoneOffset

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height

import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import com.pedro.ChamaKids.ui.theme.ChamaKidsBlue

import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextFieldDefaults
import com.pedro.ChamaKids.ui.theme.ChamaKidsHeaderGray

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.OutlinedButton
import com.pedro.ChamaKids.data.MemberEntity
import com.pedro.ChamaKids.ui.theme.ChamaKidsAction

import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.text.font.FontWeight
import com.pedro.ChamaKids.ui.theme.ChamaKidsCard

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMemberScreen(
    onVoltar: () -> Unit,
    viewModel: MemberViewModel
) {

    var nome by remember {
        mutableStateOf("")
    }

    var cpf by remember {
        mutableStateOf("")
    }

    var rg by remember {
        mutableStateOf("")
    }

    var endereco by remember {
        mutableStateOf("")
    }

    var celularMembro by remember {
        mutableStateOf("")
    }

    var telefone by remember {
        mutableStateOf("")
    }

    var nomePai by remember {
        mutableStateOf("")
    }

    var celularPai by remember {
        mutableStateOf("")
    }

    var nomeMae by remember {
        mutableStateOf("")
    }

    var celularMae by remember {
        mutableStateOf("")
    }

    var dataNascimento by remember {
        mutableStateOf<LocalDate?>(null)
    }

    var fotoUri by remember {
        mutableStateOf<Uri?>(null)
    }

    val context = LocalContext.current


    // =====================================================
    // SELETOR DE FOTO
    // =====================================================

    val seletorFoto =
        rememberLauncherForActivityResult(
            contract =
                ActivityResultContracts.GetContent()
        ) { uri ->

            if (uri != null) {
                fotoUri = uri
            }
        }


    // =====================================================
    // SELETOR DE DATA
    // =====================================================

    var mostrarCalendario by remember {
        mutableStateOf(false)
    }

    val datePickerState =
        rememberDatePickerState()


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
            titulo = "CADASTRO",
            onVoltar = onVoltar
        )

        // =================================================
        // CONTEÚDO DA FICHA
        // =================================================

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(20.dp)
        ) {

            // =================================================
            // FOTO
            // =================================================

            val bitmapFoto = remember(fotoUri) {

                if (fotoUri == null) {

                    null

                } else {

                    try {

                        context.contentResolver
                            .openInputStream(fotoUri!!)
                            ?.use { inputStream ->

                                BitmapFactory.decodeStream(
                                    inputStream
                                )
                            }

                    } catch (e: Exception) {

                        null
                    }
                }
            }


            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),

                contentAlignment = Alignment.Center
            ) {

                Card(
                    modifier = Modifier
                        .size(180.dp)
                        .clickable {

                            seletorFoto.launch("image/*")
                        },

                    shape = RoundedCornerShape(16.dp),

                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {

                        if (bitmapFoto != null) {

                            Image(
                                bitmap = bitmapFoto.asImageBitmap(),
                                contentDescription = "Foto do membro",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Color.Black.copy(
                                            alpha = 0.35f
                                        )
                                    ),

                                contentAlignment = Alignment.Center
                            ) {

                                Text(
                                    text = "ALTERAR FOTO",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                        } else {

                            Text(
                                text = "+ FOTO"
                            )
                        }
                    }
                }
            }


            Spacer(
                modifier = Modifier.height(20.dp)
            )


            // =================================================
            // NOME
            // =================================================

            CampoFicha(
                valor = nome,
                titulo = "Nome completo",
                habilitado = true
            ) {
                nome = it
            }


            Spacer(
                modifier = Modifier.height(12.dp)
            )


            // =================================================
            // CPF
            // =================================================

            CampoFicha(
                valor = cpf,
                titulo = "CPF",
                habilitado = true,
                keyboardType = KeyboardType.Number,
                visualTransformation = CpfVisualTransformation()
            ) { valor ->

                cpf = valor
                    .filter { it.isDigit() }
                    .take(11)
            }


            Spacer(
                modifier = Modifier.height(12.dp)
            )


            // =================================================
            // RG + DATA DE NASCIMENTO
            // =================================================

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                CampoFicha(
                    valor = rg,
                    titulo = "RG",
                    habilitado = true,
                    modifier = Modifier.weight(1f)
                ) {
                    rg = it
                }


                Spacer(
                    modifier = Modifier.width(10.dp)
                )


                OutlinedButton(
                    onClick = {

                        mostrarCalendario = true
                    },

                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .padding(top = 4.dp),

                    shape = RoundedCornerShape(4.dp),

                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF555555)
                    ),

                    contentPadding = PaddingValues(
                        horizontal = 12.dp
                    )
                ) {

                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Text(
                            text =
                                dataNascimento?.format(
                                    DateTimeFormatter.ofPattern(
                                        "dd/MM/yyyy"
                                    )
                                ) ?: "Nascimento",

                            modifier = Modifier.align(
                                Alignment.CenterStart
                            ),

                            fontSize = 14.sp
                        )
                    }
                }
            }


            Spacer(
                modifier = Modifier.height(12.dp)
            )


            // =================================================
            // ENDEREÇO
            // =================================================

            CampoFicha(
                valor = endereco,
                titulo = "Endereço completo",
                habilitado = true
            ) {
                endereco = it
            }


            Spacer(
                modifier = Modifier.height(12.dp)
            )


            // =================================================
            // CELULAR + TELEFONE
            // =================================================

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {

                CampoFicha(
                    valor = celularMembro,
                    titulo = "Cel. da criança",
                    habilitado = true,
                    modifier = Modifier.weight(1f)
                ) {
                    celularMembro = it
                }


                Spacer(
                    modifier = Modifier.width(10.dp)
                )


                CampoFicha(
                    valor = telefone,
                    titulo = "Telefone",
                    habilitado = true,
                    modifier = Modifier.weight(1f)
                ) {
                    telefone = it
                }
            }


            Spacer(
                modifier = Modifier.height(12.dp)
            )


            // =================================================
            // PAI
            // =================================================

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {

                CampoFicha(
                    valor = nomePai,
                    titulo = "Nome do pai",
                    habilitado = true,
                    modifier = Modifier.weight(1f)
                ) {
                    nomePai = it
                }


                Spacer(
                    modifier = Modifier.width(10.dp)
                )


                CampoFicha(
                    valor = celularPai,
                    titulo = "Cel. do pai",
                    habilitado = true,
                    modifier = Modifier.weight(1f)
                ) {
                    celularPai = it
                }
            }


            Spacer(
                modifier = Modifier.height(12.dp)
            )


            // =================================================
            // MÃE
            // =================================================

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {

                CampoFicha(
                    valor = nomeMae,
                    titulo = "Nome da mãe",
                    habilitado = true,
                    modifier = Modifier.weight(1f)
                ) {
                    nomeMae = it
                }


                Spacer(
                    modifier = Modifier.width(10.dp)
                )


                CampoFicha(
                    valor = celularMae,
                    titulo = "Cel. da mãe",
                    habilitado = true,
                    modifier = Modifier.weight(1f)
                ) {
                    celularMae = it
                }
            }


            Spacer(
                modifier = Modifier.height(30.dp)
            )


            // =================================================
            // LIMPAR
            // =================================================

            OutlinedButton(
                onClick = {

                    nome = ""
                    cpf = ""
                    rg = ""
                    endereco = ""
                    celularMembro = ""
                    telefone = ""
                    nomePai = ""
                    celularPai = ""
                    nomeMae = ""
                    celularMae = ""

                    dataNascimento = null
                    fotoUri = null
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),

                colors = ButtonDefaults.buttonColors(
                    containerColor = ChamaKidsCard,
                    contentColor = Color.Black
                ),

                border = BorderStroke(
                    width = 1.5.dp,
                    color = Color.Black
                )
            ) {

                Text(
                    text = "LIMPAR INFORMAÇÕES"
                )
            }


            Spacer(
                modifier = Modifier.height(12.dp)
            )


            // =================================================
            // ADICIONAR MEMBRO
            // =================================================

            Button(
                onClick = {

                    if (nome.isNotBlank()) {

                        val novoMembro = MemberEntity(
                            nome = nome.trim(),
                            cpf = cpf,
                            rg = rg,
                            dataNascimento = dataNascimento?.toString(),
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

                        viewModel.adicionarMembro(
                            membro = novoMembro,
                            onSucesso = {
                                onVoltar()
                            }
                        )
                    }
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),

                colors = ButtonDefaults.buttonColors(
                    containerColor = ChamaKidsAction,
                    contentColor = Color.Black
                ),

                border = BorderStroke(
                    width = 1.5.dp,
                    color = Color.Black
                )
            ) {

                Text(
                    text = "ADICIONAR MEMBRO",
                    fontWeight = FontWeight.SemiBold
                )
            }


            Spacer(
                modifier = Modifier.height(20.dp)
            )
        }

    // =====================================================
    // CALENDÁRIO
    // =====================================================

    if (mostrarCalendario) {

        DatePickerDialog(

            onDismissRequest = {
                mostrarCalendario =
                    false
            },

            confirmButton = {

                TextButton(
                    onClick = {

                        val millis =
                            datePickerState.selectedDateMillis

                        if (millis != null) {

                            dataNascimento =
                                Instant
                                    .ofEpochMilli(millis)
                                    .atZone(ZoneOffset.UTC)
                                    .toLocalDate()
                        }

                        mostrarCalendario = false
                    }
                ) {

                    Text("OK")
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {

                        mostrarCalendario =
                            false
                    }
                ) {

                    Text("CANCELAR")
                }
            }
        ) {

            DatePicker(
                state =
                    datePickerState
            )
        }
    }
}
}


@Composable
private fun CampoTexto(
    valor: String,
    titulo: String,
    modifier: Modifier = Modifier,
    onChange: (String) -> Unit
) {

    OutlinedTextField(
        value = valor,

        onValueChange =
            onChange,

        label = {
            Text(titulo)
        },

        modifier =
            modifier
                .fillMaxWidth(),

        colors =
            OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color.White,
                focusedBorderColor = Color(0xFF666666),
                unfocusedBorderColor = Color(0xFF888888)
            )
    )
}

private fun formatarCpf(
    entrada: String
): String {

    val numeros =
        entrada
            .filter { it.isDigit() }
            .take(11)

    return buildString {

        numeros.forEachIndexed { indice, caractere ->

            append(caractere)

            when (indice) {

                2, 5 -> {
                    if (indice < numeros.lastIndex) {
                        append(".")
                    }
                }

                8 -> {
                    if (indice < numeros.lastIndex) {
                        append("-")
                    }
                }
            }
        }
    }
}
